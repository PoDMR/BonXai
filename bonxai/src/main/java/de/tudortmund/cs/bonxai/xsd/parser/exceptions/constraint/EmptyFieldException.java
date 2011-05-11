package de.tudortmund.cs.bonxai.xsd.parser.exceptions.constraint;

import de.tudortmund.cs.bonxai.xsd.SimpleConstraint;

/**
 * @author Lars Schmidt, Dominik Wolff
 */
public class EmptyFieldException extends Exception {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
     * Called when a constraint has an empty field.
     * @param caller        Calling constraint.
     */
    public EmptyFieldException(SimpleConstraint caller) {
        super("Following constraint uses an empty field: " + caller.getName());
    }
}