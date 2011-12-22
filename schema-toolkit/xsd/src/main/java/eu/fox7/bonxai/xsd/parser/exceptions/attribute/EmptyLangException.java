package eu.fox7.bonxai.xsd.parser.exceptions.attribute;

/**
 * @author Lars Schmidt, Dominik Wolff
 */
public class EmptyLangException extends eu.fox7.bonxai.xsd.parser.exceptions.XSDParseException {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
     * Called when a tag has an empty source.
     * @param string Name of the caller object
     */
    public EmptyLangException(String string) {
        super("There is an empty xml:lang-property in the following object:" + string);
    }
}