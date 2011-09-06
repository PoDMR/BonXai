package eu.fox7.bonxai.xsd.automaton.exceptions;

import eu.fox7.bonxai.xsd.automaton.Automaton;

/**
 * It is not allowed to used a non deterministic finite automaton instead of a
 * deterministic finite automaton for the specified method. This exception is
 * thrown if such a non deterministic finite automaton is used.
 *
 * @author Dominik Wolff
 */
public class NonDeterministicFiniteAutomataException extends Exception {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
     * Constructor for the <tt>NonDeterministicFiniteAutomataException</tt>
     * class which is called if a supposedly deterministic finite automaton is
     * indeed non deterministic.
     *
     * @param automaton Automaton which is non deterministic.
     * @param methodName Name of the method that uses the automaton.
     */
    @SuppressWarnings("rawtypes")
	public NonDeterministicFiniteAutomataException(Automaton automaton, String methodName) {
        super("Following automaton is non deterministic " + automaton + " but the " + methodName + " method needs a deterministic automaton. Use subset construction to converting a non deterministic finite automaton into a deterministic finite automaton.");
    }
}