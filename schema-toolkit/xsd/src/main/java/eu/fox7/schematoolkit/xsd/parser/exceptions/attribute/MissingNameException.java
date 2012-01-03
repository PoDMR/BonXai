package eu.fox7.schematoolkit.xsd.parser.exceptions.attribute;

/**
 *
 * @author Lars Schmidt, Dominik Wolff
 */
public class MissingNameException extends eu.fox7.schematoolkit.xsd.parser.exceptions.XSDParseException {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public MissingNameException() {
        super("An element is missing a name and has no ref-attribute");
    }
}
