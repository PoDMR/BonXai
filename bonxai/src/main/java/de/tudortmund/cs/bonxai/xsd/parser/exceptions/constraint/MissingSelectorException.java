package de.tudortmund.cs.bonxai.xsd.parser.exceptions.constraint;

import de.tudortmund.cs.bonxai.xsd.SimpleConstraint;

/**
 * @author Lars Schmidt, Dominik Wolff
 */
public class MissingSelectorException extends Exception {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
     * Called when a constraint has no selector.
     * @param caller        Calling constraint.
     */
    public MissingSelectorException(SimpleConstraint caller) {
        super("Following constraint has no selector: " + caller.getName());
    }
}