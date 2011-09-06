package eu.fox7.bonxai.relaxng.parser.exceptions;

/**
 * @author Lars Schmidt
 */
public class InvalidCombineMethodException extends Exception {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
     * Called if there is an invalid value for a combine attribute
     * @param element      Element type of the caller
     */
    public InvalidCombineMethodException(String element) {
        super("There is an invalid value for a combine attribute in: " + element);
    }
}
