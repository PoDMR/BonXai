package de.tudortmund.cs.bonxai.xsd.parser.exceptions.constraint;

import de.tudortmund.cs.bonxai.xsd.SimpleConstraint;

/**
 * @author Lars Schmidt, Dominik Wolff
 */
public class MissingFieldException extends Exception {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
     * Called when a constraint has no field.
     * @param caller        Calling constraint.
     */
    public MissingFieldException(SimpleConstraint caller) {
        super("Following constraint has no field: " + caller.getName());
    }
}