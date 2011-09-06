package eu.fox7.bonxai.xsd.automaton.ParticleAutomatons.exceptions;

import eu.fox7.bonxai.xsd.automaton.ParticleAutomatons.ParticleAutomaton;

/**
 * The substiton particle automaton construction requires destination states for
 * transitions for the non accepting automaton to the accepting automaton. If
 * such a state does not exists this exception is thrown.
 *
 * It is not allowed to used a non deterministic finite automaton instead of a
 * deterministic finite automaton for the specified method. This exception is
 * thrown if such a non deterministic finite automaton is used.
 *
 * @author Dominik Wolff
 */
public class NoDestinationStateFoundException extends Exception {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
     * Constructor for the <tt>NoDestinationStateFoundException</tt> class which
     * is called if no destination state can be found in the specified particle
     * automaton.
     *
     * @param particleAutomaton Automaton which should contain a valid
     * destination state.
     * @param methodName Name of the method in which the conflict arises.
     */
    public NoDestinationStateFoundException(ParticleAutomaton particleAutomaton, String methodName) {
        super("No destination state was found in the following automaton " + particleAutomaton + " for the " + methodName + " method.");
    }
}