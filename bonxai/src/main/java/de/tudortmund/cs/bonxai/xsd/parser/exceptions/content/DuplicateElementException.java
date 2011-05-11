package de.tudortmund.cs.bonxai.xsd.parser.exceptions.content;

/**
 * @author Lars Schmidt, Dominik Wolff
 */
public class DuplicateElementException extends Exception {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
     * Called when there are two keys with the same name.
     * @param name       Name of the calling element.
     */
    public DuplicateElementException(String name) {
        super("Following duplicate element name was found in the XSD: " + name);
    }
}