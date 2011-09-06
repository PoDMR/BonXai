package eu.fox7.bonxai.xsd.parser.exceptions.constraint;

/**
 * @author Lars Schmidt, Dominik Wolff
 */
public class MissingReferException extends Exception {

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