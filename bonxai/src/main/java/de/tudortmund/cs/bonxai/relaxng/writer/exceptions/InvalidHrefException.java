package de.tudortmund.cs.bonxai.relaxng.writer.exceptions;

/**
 * @author Lars Schmidt
 */
public class InvalidHrefException extends Exception {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
     * Called when a href of an externalRef is no null or empty
     * @param name         Invalid name of the caller
     */
    public InvalidHrefException(String name) {
        super("The following href-attribute of an externalRef is no null or empty: " + name);
    }
}
