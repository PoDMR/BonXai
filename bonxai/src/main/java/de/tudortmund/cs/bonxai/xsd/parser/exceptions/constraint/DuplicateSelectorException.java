package de.tudortmund.cs.bonxai.xsd.parser.exceptions.constraint;

import de.tudortmund.cs.bonxai.xsd.SimpleConstraint;

/**
 * @author Lars Schmidt, Dominik Wolff
 */
public class DuplicateSelectorException extends Exception {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
     * Called when a constraint has more than one selector.
     * @param caller        Calling constraint.
     */
    public DuplicateSelectorException(SimpleConstraint caller) {
        super("Following constraint has more than one selector: " + caller.getName());
    }
}