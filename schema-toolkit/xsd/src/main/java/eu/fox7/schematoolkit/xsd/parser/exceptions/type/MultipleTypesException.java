package eu.fox7.schematoolkit.xsd.parser.exceptions.type;

/**
 *
 * @author Lars Schmidt, Dominik Wolff
 */
public class MultipleTypesException extends eu.fox7.schematoolkit.xsd.parser.exceptions.XSDParseException {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
     * Called when a tag has multiple type-childs.
     * @param tygName        Name of the tag which possesses multiple types.
     */
    public MultipleTypesException(String tagName, String string) {
        super("Following tag: " + tagName + "with name " + string + " possesses multiple types." );
    }

}
