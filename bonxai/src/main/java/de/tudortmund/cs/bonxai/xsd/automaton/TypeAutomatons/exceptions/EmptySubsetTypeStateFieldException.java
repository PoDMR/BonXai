package de.tudortmund.cs.bonxai.xsd.automaton.TypeAutomatons.exceptions;

import de.tudortmund.cs.bonxai.xsd.automaton.TypeAutomatons.SubsetTypeState;

/**
 * It is not allowed to construct a subset type state with an empty type states
 * field. This exception is thrown if such a <tt>SubsetTypeState</tt> is
 * initialized.
 *
 * @author Dominik Wolff
 */

public class EmptySubsetTypeStateFieldException extends Exception {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
     * Constructor for the <tt>EmptySubsetTypeStateFieldException</tt> class
     * which gets the invalid <tt>SubsetTypeState</tt> as parameter.
     *
     * @param subsetTypeState State which contains null as value of the type
     * states field.
     */
    public EmptySubsetTypeStateFieldException(SubsetTypeState subsetTypeState) {
        super("Following subset type state " + subsetTypeState + " was initialized with an empty particle states field, this is not allowed.");
    }
}

