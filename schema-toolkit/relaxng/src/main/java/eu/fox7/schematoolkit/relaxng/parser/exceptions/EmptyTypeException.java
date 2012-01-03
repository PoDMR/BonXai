package eu.fox7.schematoolkit.relaxng.parser.exceptions;

/**
 * @author Lars Schmidt
 */
public class EmptyTypeException extends Exception {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
     * Called when a tag has no type.
     * @param name
     */
    public EmptyTypeException(String name) {
        super("There is no type-attribute defined in the following object:" + name);
    }
}
