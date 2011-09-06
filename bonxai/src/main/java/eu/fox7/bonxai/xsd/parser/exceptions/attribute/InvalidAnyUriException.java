package eu.fox7.bonxai.xsd.parser.exceptions.attribute;

/**
 * @author Lars Schmidt, Dominik Wolff
 */
public class InvalidAnyUriException extends Exception {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
     * Called when no anyUri.
     * @param name         Invalid uri of the caller
     * @param element      Element type of the caller
     */
    public InvalidAnyUriException(String uri, String element) {
        super("Following is not a valid anyUri " + uri + " for a " + element);
    }
}
