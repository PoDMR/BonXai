package eu.fox7.schematoolkit.relaxng.parser.exceptions;

/**
 * @author Lars Schmidt
 */
public class InvalidQNameException extends Exception {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
     * Called when a name is no NCName.
     * @param name         Invalid name of the caller
     * @param element      Element type of the caller
     */
    public InvalidQNameException(String name, String element) {
        super("Following name is not a valid QName: \"" + name + "\" for a " + element);
    }
}
