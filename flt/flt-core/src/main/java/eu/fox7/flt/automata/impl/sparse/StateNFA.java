package eu.fox7.flt.automata.impl.sparse;

import eu.fox7.flt.automata.NFA;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections15.BidiMap;

public interface StateNFA extends NFA {

	/**
     * method returning the NFA's alphabet as a Map that associates the symbol value
     * String with the Symbol
     * @return Map representing the NFA's alphabet; note that this is unmodifiable
     */
	@Deprecated
    public Map<String, Symbol> getAlphabet();

	public Collection<Symbol> getSymbols();

	public boolean hasSymbol(Symbol symbol);

	/**
     * method returning the NFA's states as a BidiMap that associates the state value
     * String with the States
     * @return BidiMap associating the state values to the NFA's states; this is
     *         an unmodifiable version
     */
	@Deprecated
    public BidiMap<String,State> getStateMap();

    public Collection<State> getStates();

	public boolean hasState(State state);

	/**
     * method that returns a State of the NFA given a state value String; if there's
     * no State associated with this state value, null is returned
     * @param stateValue
     *            String that represents the state value
     * @return State with the given value or null if that doesn't exists
     */
    public State getState(String stateValue);

	/**
     * method returning a state value String associated with the given State
     * @param state
     *            State to find the state value for
     * @return String representing the State's value, null if the State is not
     *         a member of the NFA's set of states
     */
    public String getStateValue(State state);

	public TransitionMap getTransitionMap();

    public Set<Transition> getIncomingTransitions(State toState);

    public Set<Transition> getOutgoingTransitions(State toState);
    
    public Set<State> getNextStates(Symbol symbol, State fromState);

    public boolean hasNextStates(Symbol symbol, State fromState);

    public Set<State> getNextStates(State fromState);
    
    public Set<State> getEpsilonClosure(State state);

	public Set<State> getPreviousStates(Symbol symbol, State toState);

	public boolean hasPreviousStates(Symbol symbol, State toState);

    public Set<State> getPreviousStates(State toState);

	public State getInitialState();

	public boolean isInitialState(State state);

	public Set<State> getFinalStates();

	public boolean isFinalState(State state);

	/**
	 * method that returns the number of edges that can be followed in the
	 * (hyper)graph representation of the NFA; this is the number of transitions
	 * starting from the given state, plus one if the latter is a final state
	 * of the NFA
	 * @param state to determine the number of choice from
	 * @return int number of transitions from the given state
	 */
	public int getNumberOfChoicesFrom(State fromState);

	public Set<State> reachableStates();

	public Set<State> getTerminatingStates();

	public boolean isSourceState(State state);

	public boolean isSinkState(State state);
	
}
