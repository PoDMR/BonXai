package eu.fox7.schematoolkit.xsd.parser.exceptions.constraint;

import eu.fox7.schematoolkit.xsd.om.SimpleConstraint;

/**
 * @author Lars Schmidt, Dominik Wolff
 */
public class EmptySelectorException extends eu.fox7.schematoolkit.xsd.parser.exceptions.XSDParseException {

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