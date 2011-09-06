package eu.fox7.bonxai.xsd.automaton.ParticleAutomatons.exceptions;

import eu.fox7.bonxai.xsd.automaton.ParticleAutomatons.ParticleAutomaton;

/**
 * A particle has to be valid with respect to the unique particle attribution 
 * constraint of XML XSDSchema. This exception is thrown if a particle automaton
 * violates this constraint.
 *
 * @author Dominik Wolff
 */
public class UniqueParticleAttributionViolationException extends Exception {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
     * Constructor for the <tt>UniqueParticleAttributionViolationException</tt>
     * class which is called if a particle repesented by the specified particle
     * automaton violates the unique particle attribution constraint of XML
     * XSDSchema.
     *
     * @param particleAutomaton Automaton whose particle is not valid with
     * respect to the UPA constraint.
     * @param methodName Name of the method that uses the automaton.
     */
    public UniqueParticleAttributionViolationException(ParticleAutomaton particleAutomaton, String methodName) {
        super("The particle represented by the following automaton " + particleAutomaton + " in the " + methodName + " method violates the unique particle attribution constraint of XML XSDSchema.");
    }
}