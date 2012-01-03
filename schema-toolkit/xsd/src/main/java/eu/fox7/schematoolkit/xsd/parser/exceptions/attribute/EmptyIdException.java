package eu.fox7.schematoolkit.xsd.parser.exceptions.attribute;

/**
 * @author Lars Schmidt, Dominik Wolff
 */
public class EmptyIdException extends eu.fox7.schematoolkit.xsd.parser.exceptions.XSDParseException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
     * Called when a tag has an empty ID.
     * @param string
     */
    public EmptyIdException(String string) {
        super("There is an empty ID-property in the following object:" + string);
    }
}