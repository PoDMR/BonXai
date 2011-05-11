package de.tudortmund.cs.bonxai.xsd.automaton.ParticleAutomatons.exceptions;

import de.tudortmund.cs.bonxai.xsd.automaton.ParticleAutomatons.ParticleAutomaton;

/**
 * If a method does not support a particle automaton an exception has to be
 * thrown. This class is this exception.
 *
 * @author Dominik Wolff
 */
public class NotSupportedParticleAutomatonException extends Exception {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
     * Constructor for the <tt>NotSupportedParticleAutomatonException</tt> class
     * which is called if a particle automaton is not supported by a specified
     * method.
     *
     * @param particleAutomaton Automaton which is not supported by the 
     * specified method.
     * @param methodName Name of the method does not support the given particle
     * automaton.
     */
    public NotSupportedParticleAutomatonException(ParticleAutomaton particleAutomaton, String methodName) {
        super("The following particle automaton" + particleAutomaton + " is not supported by the " + methodName + "  method.");
    }
}