package eu.fox7.schematoolkit.xsd.parser.exceptions.constraint;

import eu.fox7.schematoolkit.xsd.om.SimpleConstraint;

/**
 * @author Lars Schmidt, Dominik Wolff
 */
public class MissingFieldException extends eu.fox7.schematoolkit.xsd.parser.exceptions.XSDParseException {

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