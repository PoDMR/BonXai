package eu.fox7.bonxai.xsd.parser.exceptions.constraint;

/**
 * @author Lars Schmidt, Dominik Wolff
 */
public class EmptyReferException extends eu.fox7.bonxai.xsd.parser.exceptions.XSDParseException {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
     * Called when a constraint has an empty refer.
     * @param caller        Calling constraint.
     */
    public EmptyReferException(String caller) {
        super("Following constraint has an empty refer: " + caller);
    }
}