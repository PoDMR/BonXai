package de.tudortmund.cs.bonxai.xsd.parser.exceptions.attribute;

/**
 * @author Lars Schmidt, Dominik Wolff
 */
public class InvalidNCNameException extends Exception {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
     * Called when a name is no NCName.
     * @param name         Invalid name of the caller
     * @param element      Element type of the caller
     */
    public InvalidNCNameException(String name, String element) {
        super("Following name is not a valid NCName " + name + " for a " + element);
    }
}