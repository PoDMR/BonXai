package gjb.flt.automata;

import java.util.Set;


public interface NFA{

	public Set<String> getSymbolValues();

	public boolean hasSymbol(String symbolValue);

	/**
     * method that returns the number of symbols in the NFA's alphabet
     * @return the number of symbols in the alphabet
     */
    public int getNumberOfSymbols();

    public Set<String> getStateValues();

	/**
     * method to check whether the NFA has a State with the specified state value
     * String
     * @param stateValue
     *            state value String to check for
     * @return boolean true if the NFA has a State with the given value, false
     *         otherwise
     */
    public boolean hasState(String stateValue);

	/**
     * method that returns the NFA's number of States
     * @return the number of states of the NFA
     */
    public int getNumberOfStates();

	public boolean hasTransition(String symbolValue, String fromValue,
	                             String toValue);

	public boolean hasEpsilonTransitions();

	/**
     * method returning the <code>Set</code> of string representations of states
     * that is reached from the given state with the specified symbol  
     * 
     * @param symbolValue <code>String</code> representing the symbol
     * @param stateValue <code>String</code> representing the state
     * @return a set of states
     */
    public Set<String> getNextStateValues(String symbolValue, String fromValue);

    public boolean hasNextStateValues(String symbolValue, String fromValue);

	public Set<String> getNextStateValues(String stateValue);
	
	public Set<String> getSymbolValuesFrom(String stateValue);

	public Set<String> getPreviousStateValues(String symbolValue, String toValue);

	public boolean hasPreviousStateValues(String symbolValue, String toValue);

	public Set<String> getPreviousStateValues(String stateValue);

	public Set<String> getSymbolValuesTo(String stateValue);

	public int getNumberOfTransitions();

	public String getInitialStateValue();

	public boolean hasInitialState();

	public boolean isInitialState(String stateValue);

	public Set<String> getFinalStateValues();
	
	public Set<String> getEpsilonClosure(String stateValue);

	public boolean hasFinalStates();

	public boolean isFinalState(String stateValue);

	public int getNumberOfFinalStates();

	public boolean isDFA();

}