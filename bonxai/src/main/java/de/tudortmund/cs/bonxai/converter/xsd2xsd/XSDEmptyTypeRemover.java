package de.tudortmund.cs.bonxai.converter.xsd2xsd;

import gjb.flt.automata.NoSuchStateException;
import gjb.flt.automata.factories.sparse.ProductNFAFactory;
import gjb.flt.automata.impl.sparse.SparseNFA;
import gjb.flt.automata.impl.sparse.State;
import gjb.flt.automata.impl.sparse.Symbol;
import gjb.flt.automata.impl.sparse.Transition;
import gjb.flt.automata.measures.EmptynessTest;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import de.tudortmund.cs.bonxai.common.SymbolTableFoundation;
import de.tudortmund.cs.bonxai.common.SymbolTableRef;
import de.tudortmund.cs.bonxai.typeautomaton.TypeAutomaton;
import de.tudortmund.cs.bonxai.typeautomaton.factories.XSDTypeAutomatonFactory;
import de.tudortmund.cs.bonxai.xsd.Type;
import de.tudortmund.cs.bonxai.xsd.XSDSchema;
import de.tudortmund.cs.bonxai.converter.xsd2xsd.Type2AutomatonConverter;

public class XSDEmptyTypeRemover {
	private TypeAutomaton typeAutomaton;
	private LinkedList<State> workingQueue;
	private Map<State,Set<Symbol>> stateAlphabetMap;
	private Set<State> nonEmptyStates;
	
	public XSDEmptyTypeRemover() {
		
	}
	
	public void removeEmptyTypes(XSDSchema schema) {
		XSDTypeAutomatonFactory factory = new XSDTypeAutomatonFactory(true);
		this.typeAutomaton = factory.createTypeAutomaton(schema);
		
//		System.err.println(typeAutomaton);
		
		Collection<State> states = new HashSet<State>(typeAutomaton.getStates());
		
		this.workingQueue = new LinkedList<State>(states);
		this.stateAlphabetMap = new HashMap<State,Set<Symbol>>();
		this.nonEmptyStates = new HashSet<State>();
		this.nonEmptyStates.add(typeAutomaton.getInitialState());
		
		while (! workingQueue.isEmpty()) {
			State state = workingQueue.poll();
			if ((! this.nonEmptyStates.contains(state)) && (! testEmptiness(state))) {
			  System.err.println("State " + state + " has non-empty Type");
			  Set<Transition> inTransitions = typeAutomaton.getIncomingTransitions(state);
			  for (Transition transition: inTransitions) {
				  markSourceState(transition);
			  }
			}
		}
		
//		System.err.println("Non-empty states: " + nonEmptyStates);
		
		for (State state: states) {
			if (! this.nonEmptyStates.contains(state)) {
				try {
					typeAutomaton.removeState(state);
				} catch (NoSuchStateException e) {
					throw new RuntimeException(e);
				}
			}
		}
				
		Set<Type> usedTypes = new HashSet<Type>();
		for (State state: typeAutomaton.reachableStates()) {
			
			SymbolTableRef<Type> type = typeAutomaton.getType(state);
			if (type != null) {
				usedTypes.add(type.getReference());
			}
		}
		
		SymbolTableFoundation<Type> typeSymbolTable = schema.getTypeSymbolTable();
		Set<Type> unusedTypes = new HashSet<Type>(typeSymbolTable.getAllReferencedObjects());
		unusedTypes.removeAll(usedTypes);
		
		XSDTypeRemover typeRemover = new XSDTypeRemover();
		typeRemover.removeTypes(schema, unusedTypes);
	}

	// marks target-state as nonEmpty
	// inserts source-state in working queue
	// updates non-empty alphabet of source-state
	private void markSourceState(Transition transition) {
		System.err.println("markSoureState: " + transition);
		State source = transition.getFromState();
		State target = transition.getToState();
		this.nonEmptyStates.add(target);
		if (! this.nonEmptyStates.contains(source)) {
			this.workingQueue.add(source);
			Set<Symbol> alphabet = this.stateAlphabetMap.get(source);
			if (alphabet == null) {
				alphabet = new HashSet<Symbol>();
				this.stateAlphabetMap.put(source, alphabet);
			}
			alphabet.add(transition.getSymbol());
		}
	}

	private boolean testEmptiness(State state) {
		Type2AutomatonConverter typeConverter = new Type2AutomatonConverter();
		Type type = typeAutomaton.getType(state).getReference();
//		System.err.println("State: " + state +" Type: " + type.getName());
		SparseNFA nfa = typeConverter.convertType(type);
//		System.err.println("Content: " + nfa);
		SparseNFA alphabetNFA = createAlphabetNFA(stateAlphabetMap.get(state));
//		System.err.println("Alphabet: " + stateAlphabetMap.get(state));
		SparseNFA intersection = ProductNFAFactory.intersection(nfa, alphabetNFA);
//		System.err.println("IntersectionDFA: " + intersection);
		EmptynessTest emptynessTest = new EmptynessTest();
		return emptynessTest.test(intersection);
	}

	private SparseNFA createAlphabetNFA(Set<Symbol> symbols) {
		SparseNFA nfa = new SparseNFA();
		State state = new State();
		nfa.addState(state);
		nfa.setInitialState(state);
		nfa.setFinalState(state);
		if (symbols != null) 
			for (Symbol symbol: symbols) {
				nfa.addSymbol(symbol);
				nfa.addTransition(symbol, state, state);
			}
		return nfa;
	}
}