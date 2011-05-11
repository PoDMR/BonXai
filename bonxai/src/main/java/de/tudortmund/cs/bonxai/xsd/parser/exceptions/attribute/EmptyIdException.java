package de.tudortmund.cs.bonxai.xsd.parser.exceptions.attribute;

/**
 * @author Lars Schmidt, Dominik Wolff
 */
public class EmptyIdException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
     * Called when a tag has an empty ID.
     * @param name
     */
    public EmptyIdException(String name) {
        super("There is an empty ID-property in the following object:" + name);
    }
}