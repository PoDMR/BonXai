package de.tudortmund.cs.bonxai.relaxng.parser.exceptions;

/**
 * @author Lars Schmidt
 */
public class EmptyNameClassException extends Exception {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
     * Called when a tag has no NameClass.
     * @param name
     */
    public EmptyNameClassException(String name) {
        super("There is no nameclass defined in the following object:" + name);
    }
}
