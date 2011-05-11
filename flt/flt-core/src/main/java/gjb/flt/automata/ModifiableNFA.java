package gjb.flt.automata;

import java.util.Collection;

public interface ModifiableNFA {

	public void addSymbol(String symbolValue);

	public void addSymbolValues(Collection<String> symbolValues); 

	public void removeSymbol(String symbolValue) throws NoSuchSymbolException;

	public void addStateValues(Collection<String> stateValues);

	public void addState(String stateValue);

	public void removeState(String stateValue) throws NoSuchStateException;

	/**
	 * method to add a transition to the NFA from a state to another with the
	 * specified symbol
	 * @param symbolValue
	 *            symbol value String for the transition
	 * @param fromStateValue
	 *            state value String the transition starts from
	 * @param toStateValue
	 *            state value String the transition ends in
	 */
	public void addTransition(String symbolValue, String fromStateValue,
	                          String toStateValue);

	public void removeTransition(String symbolValue, String fromStateValue,
	                             String toStateValue)
	        throws NoSuchTransitionException;

	public void setInitialState(String stateValue);

	public void clearInitialState();

	public void setFinalState(String stateValue);

	public void setFinalStateValues(Collection<String> stateValues);

	public void addFinalState(String stateValue);

	public void addFinalStateValues(Collection<String> stateValues);

	public void clearFinalState(String stateValue) throws NoSuchStateException;

	public void clearFinalStates();

}