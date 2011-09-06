package eu.fox7.bonxai.xsd.automaton.ParticleAutomatons.exceptions;

import eu.fox7.bonxai.xsd.automaton.ParticleAutomatons.ParticleAutomaton;

/**
 * It is not allowed for a normal particle automaton to have two states with the 
 * same state number. This exception is thrown if such particle automaton exists
 * in the specified method.
 *
 * @author Dominik Wolff
 */
public class NoUniqueStateNumbersException extends Exception {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
     * Constructor for the <tt>NoUniqueStateNumbersException</tt> class which is
     * called if a particle automaton has more than one particle state with the
     * same unique state number.
     *
     * @param particleAutomaton Automaton which contains states with same state
     * numbers.
     * @param methodName Name of the method that uses the automaton.
     */
    public NoUniqueStateNumbersException(ParticleAutomaton particleAutomaton, String methodName) {
        super("The following particle automaton" + particleAutomaton + " in the " + methodName + " method contains more than one state with the same state number.");
    }
}