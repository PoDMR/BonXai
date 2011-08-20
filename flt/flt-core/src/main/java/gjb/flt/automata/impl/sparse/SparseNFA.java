package gjb.flt.automata.impl.sparse;

import gjb.flt.automata.DFA;
import gjb.flt.automata.NFAStateAlreadyExistsException;
import gjb.flt.automata.NoSuchStateException;
import gjb.flt.automata.NoSuchSymbolException;
import gjb.flt.automata.NoSuchTransitionException;
import gjb.flt.automata.NotDFAException;
import gjb.flt.automata.misc.StateRemapper;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.SortedSet;

import org.apache.commons.collections15.BidiMap;
import org.apache.commons.collections15.bidimap.DualHashBidiMap;
import org.apache.commons.collections15.bidimap.UnmodifiableBidiMap;

/**
 * 
 * @author gjb
 * @version $Revision: 1.3 $
 */
public class SparseNFA implements DFA, StateDFA, ModifiableStateNFA, ModifiableStateDFA {

    /**
     * Map that associates a String to its corresponding Symbol; the symbols form
     * the alphabet of the NFA
     */
    protected Map<String,Symbol> alphabet;

    /**
     * Map that associates a state value String with a State and vice versa; this
     * represents the set of states of the NFA
     */
    protected BidiMap<String,State> states;

    /**
     * the initial state of the NFA; an NFA has a single initial state
     */
    protected State initialState;

    /**
     * TransitionMap encodes the transition function of the NFA
     */
    protected TransitionMap transitions;

    /**
     * the set of final states of the NFA
     */
    protected Set<State> finalStates;

    /**
     * the set of current states of the NFA; exclusively used during a run
     * of the NFA
     */
    protected SortedSet<State> currentStates;

    /**
     * this comparator allows to order the states according to their value
     */
    protected StateValueComparator stateValueComparator;

    /**
     * indicates whether this NFA is intended as a DFA so that a runtime exception
     * will be thrown when non-determinism is introduced during construction or
     * modification
     */
    protected boolean intendedDFA = false;

    /**
     * constructor that initalizes all data structures for the NFA;  the NFA has an
     * empty alphabet, set of states, transition function, set of final states and its
     * initial state is null
     */
    public SparseNFA() {
        this.alphabet = new HashMap<String,Symbol>();
        this.states = new DualHashBidiMap<String,State>();
        this.initialState = null;
        this.transitions = new TransitionMap();
        this.finalStates = new HashSet<State>();
        this.stateValueComparator = new StateValueComparator(this);
    }

    /**
     * copy constructor: a new NFA is constructed identical to the one specified; note
     * that the sets of states of the two NFAs are disjunct, even though they will
     * have identical values
     * @param nfa
     *            NFA to copy
     */
    public SparseNFA(StateNFA nfa) {
        this(nfa, StateRemapper.stateRemapping(nfa));
    }

    @SuppressWarnings("deprecation")
    public SparseNFA(StateNFA nfa, Map<State,State> stateRemap) {
        this.alphabet = new HashMap<String,Symbol>(nfa.getAlphabet());
        this.states = new DualHashBidiMap<String,State>();
        for (State originalState : stateRemap.keySet()) {
            State state = stateRemap.get(originalState);
            this.states.put(nfa.getStateValue(originalState), state);
        }
        if (nfa.getInitialState() != null)
            this.initialState = stateRemap.get(nfa.getInitialState());
        else
            this.initialState = null;
        try {
            this.transitions = new TransitionMap(nfa.getTransitionMap(), stateRemap);
            this.setIntendedDFA(((SparseNFA) nfa).isIntendedDFA());
        } catch (NotDFAException e) {
            if (isIntendedDFA())
                throw new RuntimeException(e);
        }
        this.finalStates = new HashSet<State>();
        for (State state : nfa.getFinalStates())
            this.finalStates.add(stateRemap.get(state));
        this.stateValueComparator = new StateValueComparator(this);
    }

    protected boolean isIntendedDFA() {
        return intendedDFA;
    }

    protected void setIntendedDFA(boolean intendedDFA) {
        this.intendedDFA = intendedDFA;
        transitions.setIntendedDFA(intendedDFA);
    }

	public Map<State,State> stateRemapping() {
        Map<State,State> map = new HashMap<State,State>();
        for (State state : states.values()) {
            State newState = new State();
            map.put(state, newState);
        }
        return map;
    }

    /* (non-Javadoc)
     * @see gjb.flt.automata.ModifiableNFA#add(gjb.flt.automata.SparseNFA)
     */
    public void add(StateNFA...nfas) {
        for (int i = 0; i < nfas.length; i++) {
            addSymbols(nfas[i].getSymbols());
            addStates(nfas[i].getStates());
            for (Transition transition : nfas[i].getTransitionMap().getTransitions())
                addTransition(transition.getSymbol(), transition.getFromState(),
                              transition.getToState());
        }
    }

    /* (non-Javadoc)
     * @see gjb.flt.automata.ModifiableNFA#add(gjb.flt.automata.SparseNFA)
     */
    public void add(StateNFA nfa) {
        StateNFA[] nfas = { nfa };
        add(nfas);
    }

    /* (non-Javadoc)
     * @see gjb.flt.automata.NFA#alphabet()
     */
    @Deprecated
    public Map<String,Symbol> getAlphabet() {
        return Collections.unmodifiableMap(alphabet);
    }

	public Set<String> getSymbolValues() {
    	return Collections.unmodifiableSet(alphabet.keySet());
    }

	public Collection<Symbol> getSymbols() {
    	return Collections.unmodifiableCollection(alphabet.values());
    }

	/* (non-Javadoc)
     * @see gjb.flt.automata.NFA#numberOfSymbols()
     */
    public int getNumberOfSymbols() {
        return getAlphabet().size();
    }

	/* (non-Javadoc)
     * @see gjb.flt.automata.NFA#hasSymbol(java.lang.String)
     */
    public boolean hasSymbol(String symbolValue) {
    	return alphabet.containsKey(symbolValue);
    }

	/* (non-Javadoc)
     * @see gjb.flt.automata.NFA#hasSymbol(gjb.flt.automata.Symbol)
     */
    public boolean hasSymbol(Symbol symbol) {
    	return alphabet.containsValue(symbol);
    }

    public void addSymbolValues(Collection<String> symbolValues) {
        for (String symbolValue : symbolValues)
        	this.addSymbol(symbolValue);
    }

	public void addSymbols(Collection<Symbol> symbols) {
        for (Symbol symbol : symbols)
            addSymbol(symbol);
    }

	public void addSymbol(String symbolValue) {
        addSymbol(Symbol.create(symbolValue));
    }

    public void addSymbol(Symbol symbol) {
        alphabet.put(symbol.toString(), symbol);
    }

    public void removeSymbol(String symbolValue) throws NoSuchSymbolException {
        if (alphabet.containsKey(symbolValue))
            alphabet.remove(symbolValue);
        else
        	throw new NoSuchSymbolException(symbolValue);
    }

    /* (non-Javadoc)
     * @see gjb.flt.automata.ModifiableNFA#removeSymbol(gjb.flt.automata.Symbol)
     */
    public void removeSymbol(Symbol symbol) throws NoSuchSymbolException {
        removeSymbol(symbol.toString());
    }

    /* (non-Javadoc)
     * @see gjb.flt.automata.NFA#states()
     */
    @Deprecated
    public BidiMap<String,State> getStateMap() {
        return UnmodifiableBidiMap.decorate(states);
    }

    public Set<String> getStateValues() {
    	return Collections.unmodifiableSet(getStateMap().keySet());
    }

	public Collection<State> getStates() {
    	return Collections.unmodifiableCollection(getStateMap().values());
    }

    /* (non-Javadoc)
     * @see gjb.flt.automata.NFA#numberOfStates()
     */
    public int getNumberOfStates() {
        return getStateMap().size();
    }

	/* (non-Javadoc)
     * @see gjb.flt.automata.NFA#state(java.lang.String)
     */
    public State getState(String stateValue) {
        return states.get(stateValue);
    }

	/* (non-Javadoc)
     * @see gjb.flt.automata.NFA#stateValue(gjb.flt.automata.State)
     */
    public String getStateValue(State state) {
        return states.getKey(state);
    }

	/* (non-Javadoc)
     * @see gjb.flt.automata.NFA#hasState(java.lang.String)
     */
    public boolean hasState(String stateValue) {
        return states.containsKey(stateValue);
    }

	/* (non-Javadoc)
     * @see gjb.flt.automata.NFA#hasState(gjb.flt.automata.State)
     */
    public boolean hasState(State state) {
        return states.containsValue(state);
    }

	public void addStateValues(Collection<String> stateValues) {
        for (String stateValue : stateValues)
        	addState(stateValue);
    }

	public void addStates(Collection<State> states) {
        for (State state : states)
            this.addState(state);
    }

	/* (non-Javadoc)
     * @see gjb.flt.automata.ModifiableNFA#addState(java.lang.String)
     */
    public void addState(String stateValue) {
        if (!hasState(stateValue))
            try {
                addState(stateValue, new State());
            } catch (NFAStateAlreadyExistsException e) {
                throw new RuntimeException(e);
            }
    }

    /* (non-Javadoc)
     * @see gjb.flt.automata.ModifiableNFA#addState(gjb.flt.automata.State)
     */
    public void addState(State state) {
        this.states.put(state.toString(), state);
    }

    /* (non-Javadoc)
     * @see gjb.flt.automata.ModifiableNFA#addState(java.lang.String, gjb.flt.automata.State)
     */
    public void addState(String stateValue, State state)
        throws NFAStateAlreadyExistsException {
        if (this.states.containsKey(stateValue) &&
                this.states.get(stateValue) != state) {
            throw new NFAStateAlreadyExistsException(stateValue);
        } else {
            this.states.put(stateValue, state);
        }
    }
    /* (non-Javadoc)
     * @see gjb.flt.automata.ModifiableNFA#removeState(java.lang.String)
     */
    public void removeState(String stateValue) throws NoSuchStateException {
        if (this.states.containsKey(stateValue))
            this.removeState(this.states.get(stateValue));
        else
        	throw new NoSuchStateException(stateValue);
    }

    /* (non-Javadoc)
     * @see gjb.flt.automata.ModifiableNFA#removeState(gjb.flt.automata.State)
     */
    public void removeState(State state) throws NoSuchStateException {
    	if (!hasState(state))
    		throw new NoSuchStateException(state.toString());
        this.transitions.remove(state);
        if (this.isInitialState(state)) {
            this.initialState = null;
        }
        this.states.removeValue(state);
        this.finalStates.remove(state);
    }

    /* (non-Javadoc)
     * @see gjb.flt.automata.NFA#transitions()
     */
    public TransitionMap getTransitionMap() {
        return transitions;
    }

	/* (non-Javadoc)
     * @see gjb.flt.automata.NFA#numberOfTransitions()
     */
    public int getNumberOfTransitions() {
        return getTransitionMap().getTransitions().size();
    }

	/* (non-Javadoc)
     * @see gjb.flt.automata.ModifiableNFA#setTransitionMap(gjb.flt.automata.TransitionMap)
     */
    public void setTransitionMap(TransitionMap transitions) {
    	this.transitions = transitions;
    }

	/* (non-Javadoc)
     * @see gjb.flt.automata.NFA#hasTransition(java.lang.String, java.lang.String, java.lang.String)
     */
    public boolean hasTransition(String symbolValue, String fromValue,
                                 String toValue) {
        return getTransitionMap().contains(Symbol.create(symbolValue),
                                           getState(fromValue), getState(toValue));
    }

    public boolean hasEpsilonTransitions() {
    	return getTransitionMap().hasTransitionsWith(Symbol.getEpsilon());
    }

    /* (non-Javadoc)
     * @see gjb.flt.automata.ModifiableNFA#addTransition(java.lang.String, java.lang.String, java.lang.String)
     */
    public void addTransition(String symbolValue, String fromStateValue,
                              String toStateValue) {
        Symbol symbol;
        if (Symbol.getEpsilon().toString().equals(symbolValue)) {
            symbol = Symbol.getEpsilon();
        } else {
            if (!alphabet.containsKey(symbolValue)) {
                alphabet.put(symbolValue, Symbol.create(symbolValue));
            }
            symbol = alphabet.get(symbolValue);
        }
        if (!states.containsKey(fromStateValue)) {
            State state = new State();
            states.put(fromStateValue, state);
        }
        State fromState = states.get(fromStateValue);
        if (!states.containsKey(toStateValue)) {
            State state = new State();
            states.put(toStateValue, state);
        }
        State toState = states.get(toStateValue);
        try {
            this.transitions.add(symbol, fromState, toState);
        } catch (NotDFAException e) {
            System.err.println(toString());
            throw new RuntimeException(e);
        }
    }

	/* (non-Javadoc)
     * @see gjb.flt.automata.ModifiableNFA#addTransition(gjb.flt.automata.Symbol, gjb.flt.automata.State, gjb.flt.automata.State)
     */
    public void addTransition(Symbol symbol, State fromState, State toState) {
        try {
            this.transitions.add(symbol, fromState, toState);
        } catch (NotDFAException e) {
            if (isIntendedDFA())
                throw new RuntimeException(e);
        }
    }

    /* (non-Javadoc)
     * @see gjb.flt.automata.ModifiableNFA#removeTransition(java.lang.String, java.lang.String, java.lang.String)
     */
    public void removeTransition(String symbolValue, String fromStateValue,
                                 String toStateValue)
            throws NoSuchTransitionException {
        if (!alphabet.containsKey(symbolValue)
                || !hasState(fromStateValue)
                || !hasState(toStateValue)) {
            throw new NoSuchTransitionException(symbolValue,
                    fromStateValue, toStateValue);
        }
        Symbol symbol = Symbol.create(symbolValue);
        State fromState = states.get(fromStateValue);
        State toState = states.get(toStateValue);
        transitions.remove(symbol, fromState, toState);
    }

	/* (non-Javadoc)
     * @see gjb.flt.automata.ModifiableNFA#removeTransition(gjb.flt.automata.Symbol, gjb.flt.automata.State, gjb.flt.automata.State)
     */
    public void removeTransition(Symbol symbol, State fromState, State toState) throws NoSuchTransitionException {
        this.transitions.remove(symbol, fromState, toState);
    }

    public Set<Transition> getIncomingTransitions(State toState) {
    	return Collections.unmodifiableSet(getTransitionMap().incomingTransitions(toState));
    }

    public Set<Transition> getOutgoingTransitions(State toState) {
    	return Collections.unmodifiableSet(getTransitionMap().outgoingTransitions(toState));
    }
    
    /* (non-Javadoc)
     * @see gjb.flt.automata.NFA#nextStateValues(java.lang.String, java.lang.String)
     */
    public Set<String> getNextStateValues(String symbolValue, String stateValue) {
        Set<String> nextStateValues = new HashSet<String>();
        Set<State> nextStates = getTransitionMap().nextStates(Symbol.create(symbolValue),
                                                              getState(stateValue));
        for (State state : nextStates)
            nextStateValues.add(getStateValue(state));
        return nextStateValues;
    }

    @Override
    public boolean hasNextStateValues(String symbolValue, String fromValue) {
        return hasNextStates(Symbol.create(symbolValue), getState(fromValue));
    }

	public String getNextStateValue(String symbolValue, String fromStateValue)
            throws NotDFAException {
    	return getStateValue(getNextState(Symbol.create(symbolValue),
    	                                  getState(fromStateValue)));
    }

    public Set<State> getNextStates(Symbol symbol, State fromState) {
    	Set<State> epsilonClosure = this.getEpsilonClosure(fromState);
    	if (epsilonClosure.size() == 1) {
    		return Collections.unmodifiableSet(getTransitionMap().nextStates(symbol, fromState));
    	} else {
    		Set<State> nextStates = new HashSet<State>();
    		for (State state: epsilonClosure)
    			nextStates.addAll(getTransitionMap().nextStates(symbol, state));
    		return nextStates;
    	}
    }

    public State getNextState(Symbol symbol, State fromState)
            throws NotDFAException {
    	return getTransitionMap().nextState(symbol, fromState);
    }
    
	@Override
	public Set<State> getEpsilonClosure(State state) {
		Set<State> epsilonClosure = new HashSet<State>();
		Queue<State> queue = new LinkedList<State>();
		queue.add(state);
		while (! queue.isEmpty()) {
			State currentState = queue.poll();
			if (! epsilonClosure.contains(currentState)) {
				epsilonClosure.add(currentState);
				Set<Transition> transitions = this.getOutgoingTransitions(currentState);
				for(Transition transition: transitions) 
					if (transition.getSymbol() == Symbol.getEpsilon()) 
						queue.add(transition.getToState());
			}
		}
		return epsilonClosure;
	}
	
	@Override
	public Set<String> getEpsilonClosure(String stateValue) {
		Set<String> eclosure = new HashSet<String>();
		Set<State> epsilonClosure = this.getEpsilonClosure(this.getState(stateValue));
		for (State state: epsilonClosure)
			eclosure.add(this.getStateValue(state));
		
		return eclosure;
	}
    @Override
    public boolean hasNextStates(Symbol symbol, State fromState) {
        return !getTransitionMap().nextStates(symbol, fromState).isEmpty();
    }

	public Set<String> getNextStateValues(String stateValue) {
    	Set<String> nextStateValues = new HashSet<String>();
    	Set<State> nextStates = transitions.nextStates(getState(stateValue));
    	for (State state : nextStates)
    		nextStateValues.add(getStateValue(state));
    	return nextStateValues;
    }

	public Set<State> getNextStates(State fromState) {
    	return Collections.unmodifiableSet(getTransitionMap().nextStates(fromState));
    }

    public Set<String> getPreviousStateValues(String symbolValue, String stateValue) {
    	Set<String> previousStateValues = new HashSet<String>();
    	Set<State> previousStates = getTransitionMap().previousStates(Symbol.create(symbolValue),
    	                                                         getState(stateValue));
    	for (State state : previousStates)
    		previousStateValues.add(getStateValue(state));
        return previousStateValues;
    }

	@Override
    public boolean hasPreviousStateValues(String symbolValue, String toValue) {
        return hasPreviousStates(Symbol.create(symbolValue), getState(toValue));
    }

	public Set<State> getPreviousStates(Symbol symbol, State toState) {
    	return Collections.unmodifiableSet(getTransitionMap().previousStates(symbol,
    	                                                                   toState));
    }
    
    @Override
    public boolean hasPreviousStates(Symbol symbol, State toState) {
        return !getTransitionMap().previousStates(symbol, toState).isEmpty();
    }

	public Set<String> getPreviousStateValues(String stateValue) {
    	Set<String> previousStateValues = new HashSet<String>();
    	Set<State> previousStates = transitions.previousStates(getState(stateValue));
    	for (State state : previousStates)
    		previousStateValues.add(getStateValue(state));
    	return previousStateValues;
    }

	public Set<State> getPreviousStates(State toState) {
    	return Collections.unmodifiableSet(getTransitionMap().previousStates(toState));
    }
    
    public Set<String> getSymbolValuesFrom(String stateValue) {
    	Set<String> symbolValues = new HashSet<String>();
    	for (Transition transition : getTransitionMap().outgoingTransitions(getState(stateValue)))
    		symbolValues.add(transition.getSymbol().toString());
    	return symbolValues;
    }

	public Set<String> getSymbolValuesTo(String stateValue) {
    	Set<String> symbolValues = new HashSet<String>();
    	for (Transition transition : getTransitionMap().incomingTransitions(getState(stateValue)))
    		symbolValues.add(transition.getSymbol().toString());
    	return symbolValues;
    }

	/* (non-Javadoc)
     * @see gjb.flt.automata.NFA#numberOfChoicesFrom(gjb.flt.automata.State)
     */
    public int getNumberOfChoicesFrom(State state) {
        return getTransitionMap().numberOfChoices(state) +
            (isFinalState(state) ? 1 : 0);
    }

	/* (non-Javadoc)
     * @see gjb.flt.automata.ModifiableNFA#setInitialState(java.lang.String)
     */
    public void setInitialState(String stateValue) {
        if (!states.containsKey(stateValue)) {
            states.put(stateValue, new State());
        }
        initialState = states.get(stateValue);
    }

    /* (non-Javadoc)
     * @see gjb.flt.automata.ModifiableNFA#setInitialState(gjb.flt.automata.State)
     */
    public void setInitialState(State state) {
        initialState = state;
    }

    /* (non-Javadoc)
     * @see gjb.flt.automata.NFA#isInitialState(java.lang.String)
     */
    public boolean isInitialState(String stateValue) {
        return isInitialState(states.get(stateValue));
    }

    /* (non-Javadoc)
     * @see gjb.flt.automata.NFA#isInitialState(gjb.flt.automata.State)
     */
    public boolean isInitialState(State state) {
        return hasInitialState() && initialState.equals(state);
    }

	public boolean hasInitialState() {
    	return initialState != null;
    }

    public String getInitialStateValue() {
    	return getStateValue(getInitialState());
    }

    /* (non-Javadoc)
     * @see gjb.flt.automata.NFA#initialState()
     */
    public State getInitialState() {
        return initialState;
    }

	/* (non-Javadoc)
     * @see gjb.flt.automata.ModifiableNFA#clearInitialState()
     */
    public void clearInitialState() {
        initialState = null;
    }

    public Set<String> getFinalStateValues() {
    	Set<String> finalStateValues = new HashSet<String>();
    	for (State state : getFinalStates())
    		finalStateValues.add(getStateValue(state));
    	return Collections.unmodifiableSet(finalStateValues);
    }

	/* (non-Javadoc)
     * @see gjb.flt.automata.NFA#finalStates()
     */
    public Set<State> getFinalStates() {
        return Collections.unmodifiableSet(finalStates);
    }

	/* (non-Javadoc)
     * @see gjb.flt.automata.NFA#numberOfFinalStates()
     */
    public int getNumberOfFinalStates() {
        return finalStates.size();
    }

	/* (non-Javadoc)
     * @see gjb.flt.automata.NFA#hasFinalStates()
     */
    public boolean hasFinalStates() {
        return !finalStates.isEmpty();
    }

	/* (non-Javadoc)
     * @see gjb.flt.automata.NFA#isFinalState(java.lang.String)
     */
    public boolean isFinalState(String stateValue) {
        return states.containsKey(stateValue)
                && isFinalState(states.get(stateValue));
    }

	/* (non-Javadoc)
     * @see gjb.flt.automata.NFA#isFinalState(gjb.flt.automata.State)
     */
    public boolean isFinalState(State state) {
        return finalStates.contains(state);
    }

	/* (non-Javadoc)
     * @see gjb.flt.automata.ModifiableNFA#setFinalState(java.lang.String)
     */
    public void setFinalState(String stateValue) {
        if (!states.containsKey(stateValue)) {
            states.put(stateValue, new State());
        }
        clearFinalStates();
        finalStates.add(states.get(stateValue));
    }

    /* (non-Javadoc)
     * @see gjb.flt.automata.ModifiableNFA#setFinalState(gjb.flt.automata.State)
     */
    public void setFinalState(State state) {
        clearFinalStates();
        finalStates.add(state);
    }

    public void setFinalStateValues(Collection<String> stateValues) {
    	clearFinalStates();
    	for (String stateValue : stateValues)
    		addFinalState(stateValue);
    }

    public void setFinalStates(Collection<State> states) {
    	clearFinalStates();
        for (State state : states)
            this.addFinalState(state);
    }

	/* (non-Javadoc)
     * @see gjb.flt.automata.ModifiableNFA#addFinalState(java.lang.String)
     */
    public void addFinalState(String stateValue) {
        if (!states.containsKey(stateValue)) {
            states.put(stateValue, new State());
        }
        finalStates.add(states.get(stateValue));
    }

	/* (non-Javadoc)
     * @see gjb.flt.automata.ModifiableNFA#addFinalState(gjb.flt.automata.State)
     */
    public void addFinalState(State state) {
        finalStates.add(state);
    }

	public void addFinalStateValues(Collection<String> stateValues) {
    	for (String stateValue : stateValues)
    		addFinalState(stateValue);
    }

	public void addFinalStates(Collection<State> states) {
        finalStates.addAll(states);
    }

	/* (non-Javadoc)
     * @see gjb.flt.automata.ModifiableNFA#clearFinalStates()
     */
    public void clearFinalStates() {
        finalStates.clear();
    }

    public void clearFinalState(String stateValue) throws NoSuchStateException {
    	clearFinalState(getState(stateValue));
    }

	/* (non-Javadoc)
     * @see gjb.flt.automata.ModifiableNFA#clearFinalState(gjb.flt.automata.State)
     */
    public void clearFinalState(State state) throws NoSuchStateException {
    	if (!hasState(state))
    		throw new NoSuchStateException(state.toString());
    	if (this.isFinalState(state))
    		this.finalStates.remove(state);
    }

    /* (non-Javadoc)
     * @see gjb.flt.automata.NFA#isSinkState(gjb.flt.automata.State)
     */
	public boolean isSinkState(State state) {
        return !getTerminatingStates().contains(state);
    }

    /* (non-Javadoc)
     * @see gjb.flt.automata.NFA#isSourceState(gjb.flt.automata.State)
     */
    public boolean isSourceState(State state) {
        return !reachableStates().contains(state);
    }

    public boolean isDFA() {
        return transitions.isDeterministic();
    }

    /* (non-Javadoc)
     * @see gjb.flt.automata.NFA#reachableStates()
     */
    public Set<State> reachableStates() {
        Set<State> reachables = new HashSet<State>();
        Set<State> toDo = new HashSet<State>();
        toDo.add(getInitialState());
        while (!toDo.isEmpty()) {
            State state = gjb.util.Collections.takeOne(toDo);
            reachables.add(state);
            Set<State> nextStates = transitions.nextStates(state);
            nextStates.removeAll(reachables);
            toDo.addAll(nextStates);
        }
        return reachables;
    }

    /* (non-Javadoc)
     * @see gjb.flt.automata.NFA#terminatingStates()
     */
    public Set<State> getTerminatingStates() {
        Set<State> terminatingStates = new HashSet<State>();
        Set<State> toDo = new HashSet<State>();
        toDo.addAll(getFinalStates());
        while (!toDo.isEmpty()) {
            State state = gjb.util.Collections.takeOne(toDo);
            terminatingStates.add(state);
            Set<State> previousStates = transitions.previousStates(state);
            previousStates.removeAll(terminatingStates);
            toDo.addAll(previousStates);
        }
        return terminatingStates;
    }
    
    public String toString() {
        StringBuffer str = new StringBuffer();
        str.append("alphabet: ").append(getAlphabet().keySet().toString()).append("\n");
        str.append("states: ").append(getStateMap().keySet().toString()).append("\n");
        str.append("nr. of states: ").append(states.size()).append("\n");
        str.append("initial state: ").append(getStateValue(getInitialState())).append("\n");
        for (Transition t : getTransitionMap().getTransitions()) {
            String fromStateValue = getStateValue(t.getFromState());
            String symbolValue = t.getSymbol().toString();
            String toStateValue = getStateValue(t.getToState());
            str.append(fromStateValue).append(", ").append(symbolValue);
            str.append(" -> ").append(toStateValue).append("\n");
        }
        Set<String> finalStateStrings = new HashSet<String>();
        for (State state : getFinalStates())
            finalStateStrings.add(getStateValue(state));
        str.append("final states: ").append(finalStateStrings.toString()).append("\n");
        return str.toString();
    }

}
