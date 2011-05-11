package de.tudortmund.cs.bonxai.xsd.automaton.ParticleAutomatons.exceptions;

import de.tudortmund.cs.bonxai.xsd.automaton.Transition;

/**
 * Certain methods only support certain transitions classes. If the currently
 * used transition has the wrong class this exception is thrown.
 *
 * @author Dominik Wolff
 */
public class NotSupportedTransitionException extends Exception {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
     * Constructor for the <tt>NotSupportedTransitionException</tt> class which
     * is called if the current transition is not supported by the specified
     * method.
     *
     * @param transition Transition which is not supported.
     * @param methodName Name of the method in which the conflict arises.
     * @param supportedTransition Name of the supported transition class.
     */
    public NotSupportedTransitionException(Transition transition, String methodName, String supportedTransition) {
        super("Following transition " + transition + " is not supported by the " + methodName + " method. The transition should be a " + supportedTransition + ".");
    }
}