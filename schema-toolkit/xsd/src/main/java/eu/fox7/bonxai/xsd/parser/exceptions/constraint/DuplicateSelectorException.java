package eu.fox7.bonxai.xsd.parser.exceptions.constraint;

import eu.fox7.bonxai.xsd.SimpleConstraint;

/**
 * @author Lars Schmidt, Dominik Wolff
 */
public class DuplicateSelectorException extends eu.fox7.bonxai.xsd.parser.exceptions.XSDParseException {

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