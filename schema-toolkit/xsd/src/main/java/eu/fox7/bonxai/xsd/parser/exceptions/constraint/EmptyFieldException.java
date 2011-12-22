package eu.fox7.bonxai.xsd.parser.exceptions.constraint;

import eu.fox7.bonxai.xsd.SimpleConstraint;

/**
 * @author Lars Schmidt, Dominik Wolff
 */
public class EmptyFieldException extends eu.fox7.bonxai.xsd.parser.exceptions.XSDParseException {

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