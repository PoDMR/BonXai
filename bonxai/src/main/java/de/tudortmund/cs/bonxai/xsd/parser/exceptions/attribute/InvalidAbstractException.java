package de.tudortmund.cs.bonxai.xsd.parser.exceptions.attribute;

/**
 * @author Lars Schmidt, Dominik Wolff
 */
public class InvalidAbstractException extends Exception {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
     * Called when a tag has an invalid abstract.
     * @param name
     */
    public InvalidAbstractException(String name) {
        super("There is an invalid abstract-property in the following object:" + name);
    }
}