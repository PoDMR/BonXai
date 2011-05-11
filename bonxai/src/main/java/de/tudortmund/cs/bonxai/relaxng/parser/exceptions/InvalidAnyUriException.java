package de.tudortmund.cs.bonxai.relaxng.parser.exceptions;

/**
 * @author Lars Schmidt
 */
public class InvalidAnyUriException extends Exception {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
     * Called when no anyUri.
     * @param uri 
     * @param element      Element type of the caller
     */
    public InvalidAnyUriException(String uri, String element) {
        super("Following is not a valid anyUri: \"" + uri + "\" for a " + element);
    }
}
