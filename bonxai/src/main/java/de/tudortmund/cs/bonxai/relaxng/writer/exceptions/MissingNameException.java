package de.tudortmund.cs.bonxai.relaxng.writer.exceptions;

/**
 * @author Lars Schmidt
 */
public class MissingNameException extends Exception {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
     * Called when a tag has no name attribute.
     * @param name
     */
    public MissingNameException(String name) {
        super("There is no name-attribute or NameClass child under the following tag:" + name);
    }
}
