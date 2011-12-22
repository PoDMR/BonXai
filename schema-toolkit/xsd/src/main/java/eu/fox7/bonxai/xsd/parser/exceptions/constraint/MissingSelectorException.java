package eu.fox7.bonxai.xsd.parser.exceptions.constraint;

import eu.fox7.bonxai.xsd.SimpleConstraint;

/**
 * @author Lars Schmidt, Dominik Wolff
 */
public class MissingSelectorException extends eu.fox7.bonxai.xsd.parser.exceptions.XSDParseException {

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