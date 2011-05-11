package de.tudortmund.cs.bonxai.xsd.automaton.TypeAutomatons.exceptions;

import de.tudortmund.cs.bonxai.xsd.automaton.TypeAutomatons.ProductTypeState;

/**
 * It is not allowed to construct a product particle state with an empty type
 * states field. This exception is thrown if such a <tt>ProductTypeState</tt> is
 * initialized.
 *
 * @author Dominik Wolff
 */
public class EmptyProductTypeStateFieldException extends Exception {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
     * Constructor for the <tt>EmptyProductTypeStateFieldException</tt> class
     * which gets the invalid <tt>ProductTypeState</tt> as parameter.
     *
     * @param productTypeState State which contains null as value of the type
     * states field.
     */
    public EmptyProductTypeStateFieldException(ProductTypeState productTypeState) {
        super("Following product type state " + productTypeState + " was initialized with an empty type states field, this is not allowed.");
    }
}
