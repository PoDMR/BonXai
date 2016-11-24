package eu.fox7.schematoolkit.converter.xsd2bonxai;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.stream.Collectors;

import eu.fox7.flt.automata.impl.sparse.ModifiableStateDFA;
import eu.fox7.flt.automata.impl.sparse.SparseNFA;
import eu.fox7.flt.automata.impl.sparse.State;
import eu.fox7.flt.automata.impl.sparse.Symbol;
import eu.fox7.flt.automata.impl.sparse.Transition;
import eu.fox7.flt.regex.Regex;
import eu.fox7.flt.regex.converters.EpsilonEmptyEliminator;
import eu.fox7.flt.regex.converters.Normalizer;
import eu.fox7.flt.regex.factories.StateEliminationFactory;
import eu.fox7.schematoolkit.bonxai.om.AncestorPattern;
import eu.fox7.schematoolkit.bonxai.om.AncestorPatternElement;
import eu.fox7.schematoolkit.bonxai.om.CardinalityParticle;
import eu.fox7.schematoolkit.bonxai.om.DoubleSlashPrefixedContainer;
import eu.fox7.schematoolkit.bonxai.om.OrExpression;
import eu.fox7.schematoolkit.bonxai.om.SequenceExpression;
import eu.fox7.schematoolkit.common.Namespace;
import eu.fox7.schematoolkit.common.NamespaceList;
import eu.fox7.schematoolkit.common.QualifiedName;
import eu.fox7.schematoolkit.typeautomaton.TypeAutomaton;
import eu.fox7.util.tree.Node;
import eu.fox7.util.tree.Tree;

import org.apache.commons.lang3.tuple.Pair;


public class AncestryAncestorPatternBuilder implements AncestorPatternBuilder {

	
	
	private TypeAutomaton typeAutomaton;
	private NamespaceList namespaceList;
	private int limit=20;
	
	
	@Override
	public Map<State, AncestorPattern> buildAncestorPatterns(TypeAutomaton typeAutomaton, NamespaceList namespaceList) {
		this.typeAutomaton = typeAutomaton;
		this.namespaceList = namespaceList;
		
		Map<State, AncestorPattern> patternMap = new HashMap<State, AncestorPattern>();
		
		Map<State, List<List<Symbol>>> ancestorStringMap = calculateAncestorStrings();
		
		typeAutomaton.getStates().forEach(state -> {
			AncestorPattern ancestorPattern;
			
			if (typeAutomaton.isInitialState(state))
				return;
			
			List<List<Symbol>> ancestorStrings = ancestorStringMap.get(state);
			
			if (ancestorStrings==null)
				ancestorPattern = this.createAncestorPattern(state);
			else {
				if (ancestorStrings.size()==1)
					ancestorPattern = convertStringToPattern(ancestorStrings.iterator().next());
				else {
					ancestorPattern = new OrExpression();
					ancestorStrings.forEach(string -> {
						((OrExpression) ancestorPattern).addChild(convertStringToPattern(string));
					});
				}
			}
			patternMap.put(state, ancestorPattern);
		});
		
		
		return patternMap;
	}

    private AncestorPattern convertStringToPattern(List<Symbol> string) {
//    	if (string.size()==1)
//    		return this.convertSymbol(string.iterator().next().toString());
    	    	
    	SequenceExpression sequence = new SequenceExpression();

    	string.forEach(symbol -> {
    		sequence.addChild(this.convertSymbol(symbol.toString()));
    	});
    	
    	return new DoubleSlashPrefixedContainer(sequence);
	}

	@SuppressWarnings({ "unchecked" })
	protected AncestorPattern createAncestorPattern(State state) {
    	//Create a dfa from typeAutomaton by choosing a final state.
    	ModifiableStateDFA dfa = new SparseNFA(typeAutomaton);
    	State dfaState = dfa.getState(typeAutomaton.getStateValue(state));
    	dfa.setFinalState(dfaState);
    	System.err.println("DFA: "+ dfa);

    	
    	
    	
//    	Minimizer minimizer = new NFAMinimizer();
//    	minimizer.minimize(dfa);
//    	System.err.println("DFA after minimization: "+ dfa);
    	
    	// create regular expression from dfa
    	StateEliminationFactory regexFactory = new StateEliminationFactory();
    	Regex regex = regexFactory.create(dfa, false);
    	
    	// remove emptyset and epsilon
    	Normalizer normalizer = new EpsilonEmptyEliminator();
    	regex = normalizer.normalize(regex);
    	
    	System.err.println("Regex-Tree: "+ regex.getTree());
    	
    	// create particle from regex
    	Tree<Object> regexTree = regex.getTree();
    	Node<Object> root = regexTree.getRoot();
    	AncestorPattern ancestorPattern = convertTreeNode(root);
    	    	
    	System.err.println("AncestorPattern: " + ancestorPattern);
    	
    	return ancestorPattern;
    }

	public Map<State, List<List<Symbol>>> calculateAncestorStrings() {
		// reminder: all lists of symbols (strings) are reversed
		Map<List<Symbol>, Set<Pair<State, State>>> map = new HashMap<>();
		Deque<Pair<List<Symbol>, Set<Pair<State, State>>>> todo = new ArrayDeque<>();  // Stack
		List<Pair<List<Symbol>, State>> done = new ArrayList<>();
		Set<State> overLimit = new HashSet<>();

		for (Transition tr : typeAutomaton.getTransitionMap().getTransitions()) {
			ArrayList<Symbol> str = new ArrayList<>();
			str.add(tr.getSymbol());
			map.computeIfAbsent(str, key -> new HashSet<>())
				.add(Pair.of(tr.getFromState(), tr.getToState()));
		}
		map.entrySet().forEach(e -> {
			todo.push(Pair.of(e.getKey(), e.getValue()));
		});

		while (!todo.isEmpty()) {
			map = new HashMap<>();
			Pair<List<Symbol>, Set<Pair<State, State>>> pair = todo.pop();
			List<Symbol> string = pair.getKey();
			Set<Pair<State, State>> qps = pair.getValue();
			Set<State> states = qps.stream()
				.map(Pair::getValue).collect(Collectors.toSet());
			
			for (State state: states) {
				if (overLimit.contains(state)) {
					overLimit.addAll(states);
					states = null;
					break;
				}
			}

			if (states==null) {}
			else if (states.size() == 1) {
				done.add(Pair.of(string, states.iterator().next()));
			} else {
				if (string.size() > limit) {
					overLimit.addAll(states);
				} else {
					for (Pair<State, State> fromTo : qps) {
						State from = fromTo.getKey();
						State to = fromTo.getValue();
						for (Transition tr : typeAutomaton.getIncomingTransitions(from)) {
							List<Symbol> newString = new ArrayList<>(string);
							newString.add(tr.getSymbol());
							map.computeIfAbsent(newString, key -> new HashSet<>())
								.add(Pair.of(tr.getFromState(), to));
						}
					}
					map.entrySet().forEach(e -> {
						todo.push(Pair.of(e.getKey(), e.getValue()));
						System.err.println("TODO: " + e.getKey());
					});
				}
			}
		}
		
		Map<State,List<List<Symbol>>> ancestorStringMap = new HashMap<>();
		
		done.forEach(e -> {
			if (!overLimit.contains(e.getValue())) {
				if (!ancestorStringMap.containsKey(e.getValue()))
					ancestorStringMap.put(e.getValue(), new ArrayList<>());
			
				// store the strings in forward direction.
				Collections.reverse(e.getKey());
				ancestorStringMap.get(e.getValue()).add(e.getKey());
			}
		});

		return ancestorStringMap;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected AncestorPattern convertTreeNode(Node<Object> node) {
		String key = node.getKey();
		if (key.equals(Regex.CONCAT_OPERATOR)) { //concatenation
			Vector<AncestorPattern> childParticles = new Vector<AncestorPattern>();
			for (Node child: node.getChildren()) {
				AncestorPattern childParticle = convertTreeNode(child);
				childParticles.add(childParticle);
			}
			return new SequenceExpression(childParticles);
		} else if (key.equals(Regex.ZERO_OR_MORE_OPERATOR)) { // kleene star
			return new CardinalityParticle(convertTreeNode(node.getChild(0)), 0);
		} else if (key.equals(Regex.UNION_OPERATOR)) { // or
			Vector<AncestorPattern> childParticles = new Vector<AncestorPattern>();
			for (Node child: node.getChildren()) {
				AncestorPattern childParticle = convertTreeNode(child);
				childParticles.add(childParticle);
			}
			return new OrExpression(childParticles);
		} else if (key.equals(Regex.EPSILON_SYMBOL)) {
			throw new RuntimeException("Epsilon no supported");
		} else if (key.equals(Regex.EMPTY_SYMBOL)) {
			throw new RuntimeException("Empty no supported");
		} else { // label
			return convertSymbol(key);
		}
	}
	
	protected AncestorPatternElement convertSymbol(String name) {
		String localName = name.substring(name.lastIndexOf("}") + 1);
		String namespaceURI = name.substring(1, name.lastIndexOf("}"));
		Namespace namespace = namespaceList.getNamespaceByUri(namespaceURI);
		QualifiedName qualifiedName = new QualifiedName(namespace, localName); 
		return new AncestorPatternElement(qualifiedName);
		
	}
	
}
