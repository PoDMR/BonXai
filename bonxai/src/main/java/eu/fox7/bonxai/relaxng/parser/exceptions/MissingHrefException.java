package eu.fox7.bonxai.relaxng.parser.exceptions;

/**
 * @author Lars Schmidt
 */
public class MissingHrefException extends Exception {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
     * Called when a tag has no href attribute.
     * @param name
     */
    public MissingHrefException(String name) {
        super("There is no href-attribute in the following tag:" + name);
    }
}
