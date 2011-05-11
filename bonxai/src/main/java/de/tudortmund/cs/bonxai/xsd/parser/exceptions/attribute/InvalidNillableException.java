package de.tudortmund.cs.bonxai.xsd.parser.exceptions.attribute;

/**
 * @author Lars Schmidt, Dominik Wolff
 */
public class InvalidNillableException extends Exception {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
     * Called when a tag has an invalid nillable.
     * @param name
     */
    public InvalidNillableException(String name) {
        super("There is an invalid nillable-property in the following object:" + name);
    }
}