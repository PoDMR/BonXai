package eu.fox7.schematoolkit.xsd.parser.exceptions.constraint;

/**
 * @author Lars Schmidt, Dominik Wolff
 */
public class MissingReferException extends eu.fox7.schematoolkit.xsd.parser.exceptions.XSDParseException {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
     * Called when a constraint has no refer.
     * @param caller        Calling constraint.
     */
    public MissingReferException(String caller) {
        super("Following constraint has no refer: " + caller);
    }
}