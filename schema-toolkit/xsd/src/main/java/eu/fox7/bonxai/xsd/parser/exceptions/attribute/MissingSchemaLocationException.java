package eu.fox7.bonxai.xsd.parser.exceptions.attribute;

/**
 * @author Lars Schmidt, Dominik Wolff
 */
public class MissingSchemaLocationException extends eu.fox7.bonxai.xsd.parser.exceptions.XSDParseException {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
     * Called when a ForeignSchema has no schemaLocation.
     */
    public MissingSchemaLocationException() {
        super("An import,include or redefine element exists, which has no schemaLocation Attribute");
    }
}