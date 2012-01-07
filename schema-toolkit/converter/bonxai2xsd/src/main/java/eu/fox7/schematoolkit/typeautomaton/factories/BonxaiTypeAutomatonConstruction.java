package eu.fox7.schematoolkit.typeautomaton.factories;


import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.Vector;

import org.apache.commons.collections15.BidiMap;
import org.apache.commons.collections15.bidimap.DualHashBidiMap;

import eu.fox7.bonxai.typeautomaton.AnnotatedNFATypeAutomaton;
import eu.fox7.bonxai.typeautomaton.TypeAutomaton;
import eu.fox7.flt.automata.NoSuchStateException;
import eu.fox7.flt.automata.NoSuchSymbolException;
import eu.fox7.flt.automata.NoSuchTransitionException;
import eu.fox7.flt.automata.NotDFAException;
import eu.fox7.flt.automata.converters.AbstractMinimizer;
import eu.fox7.flt.automata.factories.sparse.Determinizer;
import eu.fox7.flt.automata.factories.sparse.ProductDFAFactory;
import eu.fox7.flt.automata.factories.sparse.StateCondition;
import eu.fox7.flt.automata.impl.sparse.AnnotatedSparseNFA;
import eu.fox7.flt.automata.impl.sparse.AnnotatedStateNFA;
import eu.fox7.flt.automata.impl.sparse.ModifiableAnnotatedStateNFA;
import eu.fox7.flt.automata.impl.sparse.ModifiableStateNFA;
import eu.fox7.flt.automata.impl.sparse.SparseNFA;
import eu.fox7.flt.automata.impl.sparse.State;
import eu.fox7.flt.automata.impl.sparse.StateDFA;
import eu.fox7.flt.automata.impl.sparse.StateNFA;
import eu.fox7.flt.automata.impl.sparse.Symbol;
import eu.fox7.flt.automata.impl.sparse.Transition;
import eu.fox7.flt.automata.measures.Simulator;
import eu.fox7.flt.automata.misc.NerodeEquivalenceRelation;
import eu.fox7.schematoolkit.bonxai.om.AncestorPattern;
import eu.fox7.schematoolkit.bonxai.om.AncestorPatternElement;
import eu.fox7.schematoolkit.bonxai.om.Bonxai;
import eu.fox7.schematoolkit.bonxai.om.CardinalityParticle;
import eu.fox7.schematoolkit.bonxai.om.ChildPattern;
import eu.fox7.schematoolkit.bonxai.om.DoubleSlashPrefixedContainer;
import eu.fox7.schematoolkit.bonxai.om.Element;
import eu.fox7.schematoolkit.bonxai.om.ElementPattern;
import eu.fox7.schematoolkit.bonxai.om.Expression;
import eu.fox7.schematoolkit.bonxai.om.OrExpression;
import eu.fox7.schematoolkit.bonxai.om.SequenceExpression;
import eu.fox7.schematoolkit.common.AnyPattern;
import eu.fox7.schematoolkit.common.CountingPattern;
import eu.fox7.schematoolkit.common.ElementProperties;
import eu.fox7.schematoolkit.common.GroupReference;
import eu.fox7.schematoolkit.common.Particle;
import eu.fox7.schematoolkit.common.ParticleContainer;
import eu.fox7.schematoolkit.common.QualifiedName;
import eu.fox7.schematoolkit.converter.bonxai2xsd.ChildPatternConverter;
import eu.fox7.schematoolkit.xsd.om.Type;

public class BonxaiTypeAutomatonConstruction {
	
	private class ProductEquivalenceCondition implements StateCondition {

	    protected AnnotatedStateNFA<Integer, Object> nfa1, nfa2;
	    protected Set<Symbol> alphabet;

	    @SuppressWarnings("unchecked")
		public void setNFAs(StateNFA nfa1, StateNFA nfa2) {
	        this.nfa1 = (AnnotatedStateNFA<Integer,Object>) nfa1;
	        this.nfa2 = (AnnotatedStateNFA<Integer,Object>) nfa2;
	        this.alphabet = new HashSet<Symbol>();
	        this.alphabet.addAll(nfa1.getSymbols());
	        this.alphabet.addAll(nfa2.getSymbols());
	    }

	    public boolean satisfy(State fromState1, State fromState2) {
	        if ((nfa1.isFinalState(fromState1) && !nfa2.isFinalState(fromState2)) ||
	                (!nfa1.isFinalState(fromState1) && nfa2.isFinalState(fromState2)))
	            return false;
	        if ((nfa1.isFinalState(fromState1) && nfa2.isFinalState(fromState2)) &&
	        		(! nfa1.getAnnotation(fromState1).equals(nfa2.getAnnotation(fromState2))))
	        	return false;
	        
	        for (Symbol symbol : alphabet) {
	            try {
	                State toState1 = ((StateDFA) nfa1).getNextState(symbol, fromState1);
	                State toState2 = ((StateDFA) nfa2).getNextState(symbol, fromState2);
	                if ((toState1 == null && toState2 != null) ||
	                        (toState1 != null && toState2 == null))
	                    return false;
	            } catch (NotDFAException e) {
	                throw new RuntimeException(e);
	            }
	        }
	        return true;
	    }

	}
	
	private class ProductEquivalenceRelation extends NerodeEquivalenceRelation {
		public ProductEquivalenceRelation(StateNFA nfa) {
	        this.nfa = (StateDFA) nfa;
			this.simulator = new Simulator(new ProductEquivalenceCondition());
	    }
	}
	
	private class ProductMinimizer extends AbstractMinimizer {
	    protected void initRelation(ModifiableStateNFA nfa) {
	        relation = new ProductEquivalenceRelation(nfa);
	    }
	}

	/**
	 * Returned type automaton
	 */
	private TypeAutomaton typeAutomaton;
	
	/**
	 * Bidirectional mapping between states of the type automaton and the product automaton
	 */
	private BidiMap<State,State> stateMap;
	
	/**
	 * productAutomaton representing all ancestor patterns.
	 * final states are annotated with the matching ancestor pattern
	 */
	private ModifiableAnnotatedStateNFA<Integer,Object> productDFA;
	
	/**
	 * Symbol used as wildcard in ancestor pattern automata.
	 */
	private static Symbol wildcard = Symbol.create("wildcard");
	
	/**
	 * ChildPatternConverter
	 */
	private ChildPatternConverter childPatternConverter;
	
	private Queue<State> workingQueue;
	private Bonxai bonxai;
	private Vector<Expression> expressions;
	
	private TypeNameGenerator typeNameGenerator;

	public BonxaiTypeAutomatonConstruction() {
	}
	
	public TypeAutomaton constructTypeAutomaton(Bonxai bonxai, TypeNameGenerator typeNameGenerator) {
		this.typeNameGenerator = typeNameGenerator;
		typeAutomaton = new AnnotatedNFATypeAutomaton();
		stateMap = new DualHashBidiMap<State,State>();
		
		childPatternConverter = new ChildPatternConverter(typeAutomaton, bonxai.getDefaultNamespace());

		expressions = new Vector<Expression>(bonxai.getExpressions());
		List<QualifiedName> rootElements = bonxai.getRootElementNames();
		workingQueue = new LinkedList<State>();

		Set<Symbol> rootSymbols = new HashSet<Symbol>();
		for (QualifiedName rootElement: rootElements) {
			rootSymbols.add(Symbol.create(rootElement.getFullyQualifiedName()));
		}
		
		Set<Symbol> rightHandSymbols = new HashSet<Symbol>();
		rightHandSymbols.addAll(rootSymbols);
		
		// for each rule convert the ancestor expression and extract the alphabet symbols
		SparseNFA[] nfas = new SparseNFA[expressions.size()];
		Iterator<Expression> it = expressions.iterator();
		int i=0;
		while(it.hasNext()) {
			Expression expression = it.next();
			nfas[i] = convertAncestorExpression(expression);
			rightHandSymbols.addAll(getChildSymbols(expression.getChildPattern()));
			++i;
		}
		
		System.err.println("UsedSymbols: " + rightHandSymbols);
		// remove wildcards
		removeWildcards(rightHandSymbols, nfas);
		
		// construct product automaton
		productDFA = constructProductAutomaton(nfas);
		
		System.err.println("Product automaton: " + productDFA);
		
		// minimize product automaton
//		ProductMinimizer minimizer = new ProductMinimizer();
//		minimizer.initRelation(productDFA);
//		minimizer.minimize(productDFA);
//
//		System.err.println("Product automaton after minimization: " + productDFA);

		for (Symbol symbol: rightHandSymbols)
			typeAutomaton.addSymbol(symbol);
		
		//create initial state for TypeAutomaton
		State initialState = new State();
		typeAutomaton.addState(initialState);
		typeAutomaton.setInitialState(initialState);
		stateMap.put(initialState, productDFA.getInitialState());

		//insert root elements
		for (QualifiedName rootElement: rootElements) {
			insertChild(initialState, productDFA.getInitialState(), Symbol.create(rootElement.getFullyQualifiedName()));
		}
		
		try {
			productDFA.annotate(productDFA.getInitialState(), 0);
		} catch (NoSuchStateException e) {
			throw new RuntimeException(e);
		}
		
		// build type automaton until working queue is empty
		while (! workingQueue.isEmpty()) {
			State state = workingQueue.remove();
			State productState = stateMap.get(state);
			if (productState!=null) {
				Integer matchingExpression = productDFA.getAnnotation(productState);
			
				Set<Symbol> children = getChildSymbols(expressions.get(matchingExpression).getChildPattern());
				for (Symbol symbol: children) {
					insertChild(state, productState, symbol);
				}
			} else {
				System.err.println("No product state for state "+state.toString());
			}
		}

		//add ElementProperties
		for (State state: typeAutomaton.getStates()) {
			if (! typeAutomaton.isInitialState(state)) {
				State productState = stateMap.get(state);
				if (productState!=null) {
					Integer matchingExpression = productDFA.getAnnotation(productState);
					ChildPattern childPattern = expressions.get(matchingExpression).getChildPattern();

					ElementProperties elementProperties = new ElementProperties();
					ElementPattern elementPattern = childPattern.getElementPattern();
					if (elementPattern != null && elementPattern.getBonxaiType()!=null) {
						elementProperties.setDefaultValue(elementPattern.getBonxaiType().getDefaultValue());
						elementProperties.setFixedValue(elementPattern.getBonxaiType().getFixedValue());
						elementProperties.setNillable(elementPattern.isMissing());
					
						typeAutomaton.setElementProperties(state, elementProperties);
					}
				} else {
					System.err.println("No product state for state "+state.toString());
				}
			}
		}
		
		
		//add type information
		for (State state: typeAutomaton.getStates()) {
			if (! typeAutomaton.isInitialState(state)) {
				State productState = stateMap.get(state);
				if (productState!=null) {
					Integer matchingExpression = productDFA.getAnnotation(productState);
					ChildPattern childPattern = expressions.get(matchingExpression).getChildPattern();
					QualifiedName typename = typeAutomaton.getTypeName(state);
					Type type = childPatternConverter.convertChildPattern(childPattern, typename, state);
					if (type != null) 
						typeAutomaton.setType(typename, type);
				} else {
					System.err.println("No product state for state "+state.toString());
				}
			}
		}
		return typeAutomaton;
	}
	
	private void insertChild(State state, State productState, Symbol symbol) {
		State newState;
		State newProductState;
		try {
			newProductState = ((StateDFA) productDFA).getNextState(symbol, productState);
//			if (newProductState == null) {
//				throw new RuntimeException("No product state found.");
//			}
		} catch (NotDFAException e) {
			throw new RuntimeException(e);
		}
		
		if (! stateMap.containsValue(newProductState)) {
			newState = new State();
			typeAutomaton.addState(newState);
			AncestorPattern ap = expressions.get(productDFA.getAnnotation(newProductState)).getAncestorPattern();
			QualifiedName typeName = typeNameGenerator.generateTypeName(ap);
			typeAutomaton.setTypeName(newState, typeName);
			stateMap.put(newState, newProductState);
			workingQueue.add(newState);
		} else {
			newState = stateMap.getKey(newProductState);
		}
		typeAutomaton.addTransition(symbol, state, newState);
	}
	
	
	
	private Set<Symbol> getChildSymbols(ChildPattern childPattern) {
		return (childPattern.getElementPattern() == null)?new HashSet<Symbol>():getSymbols(childPattern.getElementPattern().getRegexp());
	}
	
	private Set<Symbol> getSymbols(Particle particle) {
		Set<Symbol> symbols = new HashSet<Symbol>();
		if (particle instanceof Element) {
			Element element = (Element) particle;
			if (element.getType()==null) {
				Symbol symbol = Symbol.create(element.getName().getFullyQualifiedName());
				symbols.add(symbol);
			}
		} else if (particle instanceof CountingPattern) {
			symbols = getSymbols(((CountingPattern) particle).getParticle());
		} else if (particle instanceof ParticleContainer) {
			ParticleContainer particleContainer = (ParticleContainer) particle;
			for (Particle childParticle: particleContainer.getParticles()) {
				symbols.addAll(getSymbols(childParticle));
			}
		} else if (particle instanceof AnyPattern) {
			//nothing to do here. 
		} else if (particle instanceof GroupReference) {
			GroupReference groupRef = (GroupReference) particle;
			QualifiedName groupName = groupRef.getName();
			symbols = getSymbols(bonxai.getElementGroup(groupName).getParticleContainer());
		} // just ignore other Particles. We just need the child symbols here. An error may be thrown during the actual conversion .
		return symbols;
	}


	private static SparseNFA convertAncestorExpression(Expression ex) {
		SparseNFA nfa = new SparseNFA();
		State initialState =  new State();
		State finalState =  new State();
		nfa.addState(initialState);
		nfa.addState(finalState);
		nfa.setInitialState(initialState);
		nfa.setFinalState(finalState);
		
		AncestorPattern ancestorPatternParticle = ex.getAncestorPattern();
		convertAncestorPatternParticle(ancestorPatternParticle, nfa, initialState, finalState);
		
		System.err.println("Expression: "+ ex.getAncestorPattern());
		System.err.println("NFA: "+ nfa);
		return nfa;
	}
	
	private static void convertAncestorPatternParticle(AncestorPattern ancestorPatternParticle, ModifiableStateNFA nfa, State initialState, State finalState) {
		if (ancestorPatternParticle instanceof CardinalityParticle) {
			CardinalityParticle cardinalityParticle = (CardinalityParticle) ancestorPatternParticle;
			AncestorPattern childParticle = cardinalityParticle.getChild();
			if (cardinalityParticle.getMin().equals(1) && cardinalityParticle.getMax().equals(1)) { // just one occurence
				convertAncestorPatternParticle(childParticle, nfa, initialState,finalState);
			} else if (cardinalityParticle.getMin().equals(1) && cardinalityParticle.getMax()==null) { // +
				State state = new State();
				nfa.addState(state);
				nfa.addTransition(Symbol.getEpsilon(),  state, finalState);// adding an epsilon transition from pre final to final state
				nfa.addTransition(Symbol.getEpsilon(),  state, initialState); //adding an epsilon transition from pre final to initial state
				convertAncestorPatternParticle(childParticle, nfa,initialState, state);//
			} else if (cardinalityParticle.getMin().equals(0) && cardinalityParticle.getMax()==null) { // *
				State state = new State();
				nfa.addState(state);
				nfa.addTransition(Symbol.getEpsilon(),  state, finalState);// adding an epsilon transition from pre final to final state
				nfa.addTransition(Symbol.getEpsilon(),  state, initialState); //adding an epsilon transition from pre final to initial state
				nfa.addTransition(Symbol.getEpsilon(),   initialState,finalState); //adding an epsilon transition from initial to final  state
				convertAncestorPatternParticle(childParticle, nfa,initialState, state);//
			} else if (cardinalityParticle.getMin().equals(0) && cardinalityParticle.getMax().equals(1)) { // ?
				State state = new State();
				nfa.addState(state);
				nfa.addTransition(Symbol.getEpsilon(), initialState,finalState); //adding an epsilon transition from initial to final  state
				nfa.addTransition(Symbol.getEpsilon(),  state, finalState); //adding an epsilon transition from pre final to final state
				convertAncestorPatternParticle(childParticle, nfa,initialState, state);
			} else {
				throw new RuntimeException("Invalid cardinalities in cardinality particle.");
			}
		} else if (ancestorPatternParticle instanceof OrExpression) {
			OrExpression orExpression = (OrExpression) ancestorPatternParticle;
			List<AncestorPattern> childParticles = orExpression.getChildren();
			for (AncestorPattern childParticle: childParticles) {
				State state = new State();
				nfa.addState(state);
				nfa.addTransition(Symbol.getEpsilon(), initialState, state);
				convertAncestorPatternParticle(childParticle, nfa, state, finalState);
			}
		} else if (ancestorPatternParticle instanceof SequenceExpression) {  // concatenation (a_1/a_2/.../a_n)
			SequenceExpression sequenceExpression = (SequenceExpression) ancestorPatternParticle;
			List<AncestorPattern> childParticles = sequenceExpression.getChildren();
			State state1 = initialState;
			for (AncestorPattern childParticle: childParticles) {
				State state2 = new State();
				nfa.addState(state2);
				convertAncestorPatternParticle(childParticle, nfa, state1, state2);
				state1 = state2;
			}
			nfa.addTransition(Symbol.getEpsilon(), state1, finalState);
		} else if (ancestorPatternParticle instanceof AncestorPatternElement) { // single label
			AncestorPatternElement ancestorPatternElement =  (AncestorPatternElement) ancestorPatternParticle;
			Symbol symbol = Symbol.create(ancestorPatternElement.getName().getFullyQualifiedName());
			nfa.addSymbol(symbol);
			nfa.addTransition(symbol, initialState, finalState);
		} else if (ancestorPatternParticle instanceof DoubleSlashPrefixedContainer) { // expression prefixed with // (use wildcard as Symbol for transition)
			DoubleSlashPrefixedContainer doubleSlashPrefixedContainer =  (DoubleSlashPrefixedContainer) ancestorPatternParticle;
			nfa.addTransition(wildcard, initialState, initialState);
			nfa.addSymbol(wildcard);
			AncestorPattern childParticle = doubleSlashPrefixedContainer.getChild();
			convertAncestorPatternParticle(childParticle, nfa, initialState, finalState);
		} else {
			throw new RuntimeException("Cannot convert AncestorPattern of class "+ancestorPatternParticle.getClass());
		}
	}
	
	private ModifiableAnnotatedStateNFA<Integer,Object> constructProductAutomaton(SparseNFA...nfas) {
		AnnotatedSparseNFA<Integer,Object> dfa = new AnnotatedSparseNFA<Integer,Object>();
		SparseNFA[] dfas = new SparseNFA[nfas.length];

		for (int i=0; i<nfas.length; i++) {
			dfas[i] = Determinizer.dfa(nfas[i]);
			System.err.println("DFA "+i+": "+dfas[i]);
		}
		
		try {
			ProductDFAFactory.product(dfa, dfas);
		} catch (NotDFAException e) {
			throw new RuntimeException(e);
		}

//		System.err.println("productDFA: "+dfa);

		
		for (State state: dfa.getStates()) {
			Vector<String> stateNames = new Vector<String>();
			String stateName = dfa.getStateValue(state);
			int brackets=0;
			int start=1;
			int end=0;
			for (int i=0; i< stateName.length(); i++) {
//				System.err.println("Pos: "+i+" Char: "+stateName.charAt(i)+" Brackets: "+brackets);
				switch (stateName.charAt(i)) {
				case '[': brackets++; break;
				case ']': brackets--; if (brackets==0) {
					end=i;
//					System.err.println("Start: "+start+" End: "+end);
					stateNames.add(stateName.substring(start, end));
				} break;
				case ',': if (brackets==1) {
					end=i;
//					System.err.println("Start: "+start+" End: "+end);
					stateNames.add(stateName.substring(start, end));
				} break;
				case ' ': if (brackets==1) start=i+1;
				}
			}

//			System.err.println("stateName: "+stateName);
//			System.err.println("stateNames: "+stateNames);

			if (stateNames.size() != nfas.length) {
				throw new RuntimeException("Number of states unequal number of automata in product construction");
			}
				
			boolean isFinal=false;
			int j=nfas.length - 1;
			while ((!isFinal) && (j>=0)) {
				if (dfas[j].isFinalState(stateNames.get(j))) {
					isFinal=true;
					dfa.addFinalState(state);
					dfa.annotate(state, j);
				}
				--j;
			}
		}
		return dfa;
	}

	private static void removeWildcards(Set<Symbol> usedSymbols, SparseNFA...nfas) {
		for (SparseNFA nfa: nfas) {
			Set<Transition> transitions = nfa.getTransitionMap().getTransitions();
			
			if (nfa.hasSymbol(wildcard)) {
				nfa.addSymbols(usedSymbols);
				for (Transition transition: transitions) 
					if (transition.getSymbol() == wildcard ) {
						try {
							nfa.removeTransition(transition.getSymbol(), transition.getFromState(), transition.getToState());
						} catch (NoSuchTransitionException e) {
							throw new RuntimeException(e);
						}
						for (Symbol symbol: usedSymbols) 
							nfa.addTransition(symbol, transition.getFromState(), transition.getToState());
					}
				try {
					nfa.removeSymbol(wildcard);
				} catch (NoSuchSymbolException e) {
					throw new RuntimeException("No such symbol: "+wildcard);
				}
			}
		}
	}
}
