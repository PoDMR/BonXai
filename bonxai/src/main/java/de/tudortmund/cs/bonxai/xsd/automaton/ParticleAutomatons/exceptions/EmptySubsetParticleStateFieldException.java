package de.tudortmund.cs.bonxai.xsd.automaton.ParticleAutomatons.exceptions;

import de.tudortmund.cs.bonxai.xsd.automaton.ParticleAutomatons.SubsetParticleState;

/**
 * It is not allowed to construct a subset particle state with an empty particle
 * states field. This exception is thrown if such a <tt>SubsetParticleState</tt>
 * is initialized.
 *
 * @author Dominik Wolff
 */
public class EmptySubsetParticleStateFieldException extends Exception {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
     * Constructor for the <tt>EmptySubsetParticleStateFieldException</tt>
     * class which gets the invalid <tt>SubsetParticleState</tt> as parameter.
     * 
     * @param subsetParticleState State which contains null as value of the
     * particle states field.
     */
    public EmptySubsetParticleStateFieldException(SubsetParticleState subsetParticleState) {
        super("Following subset particle state " + subsetParticleState + " was initialized with an empty particle states field, this is not allowed.");
    }
}