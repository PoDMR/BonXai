package de.tudortmund.cs.bonxai.xsd.parser.exceptions.constraint;

import de.tudortmund.cs.bonxai.xsd.SimpleConstraint;

/**
 * @author Lars Schmidt, Dominik Wolff
 */
public class EmptySelectorException extends Exception {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
     * Called when a constraint has an empty selector.
     * @param caller        Calling constraint.
     */
    public EmptySelectorException(SimpleConstraint caller) {
        super("Following constraint uses an empty selector: " + caller.getName());
    }
}