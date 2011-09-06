package eu.fox7.bonxai.xsd.automaton.ParticleAutomatons.exceptions;

import eu.fox7.bonxai.xsd.automaton.ParticleAutomatons.ProductParticleState;

/**
 * It is not allowed to construct a subset particle state with an empty particle
 * states field. This exception is thrown if such a 
 * <tt>ProductParticleState</tt> is initialized.
 *
 * @author Dominik Wolff
 */
public class EmptyProductParticleStateFieldException extends Exception {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
     * Constructor for the <tt>EmptyProductParticleStateFieldException</tt>
     * class which gets the invalid <tt>ProductParticleState</tt> as parameter.
     *
     * @param productParticleState State which contains null as value of the
     * particle states field.
     */
    public EmptyProductParticleStateFieldException(ProductParticleState productParticleState) {
        super("Following product particle state " + productParticleState + " was initialized with an empty particle states field, this is not allowed.");
    }
}