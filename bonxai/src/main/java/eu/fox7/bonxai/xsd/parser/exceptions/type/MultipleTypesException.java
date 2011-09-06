package eu.fox7.bonxai.xsd.parser.exceptions.type;

/**
 *
 * @author Lars Schmidt, Dominik Wolff
 */
public class MultipleTypesException extends Exception {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
     * Called when a tag has multiple type-childs.
     * @param tygName        Name of the tag which possesses multiple types.
     */
    public MultipleTypesException(String tagName, String name) {
        super("Following tag: " + tagName + "with name " + name + " possesses multiple types." );
    }

}
