package eu.fox7.bonxai.xsd.parser.exceptions.content;

/**
 *
 * @author Lars Schmidt, Dominik Wolff
 */
public class InvalidForeignSchemaLocationException extends eu.fox7.bonxai.xsd.parser.exceptions.XSDParseException {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public InvalidForeignSchemaLocationException() {
        super("A ForeignSchema is located at a invalid location in the schema");
    }
}
