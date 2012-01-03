package eu.fox7.schematoolkit.xsd.parser.exceptions.attribute;

/**
 *
 * @author Lars Schmidt, Dominik Wolff
 */
public class InvalidAttributeFormDefaultValueException extends eu.fox7.schematoolkit.xsd.parser.exceptions.XSDParseException {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public InvalidAttributeFormDefaultValueException() {
        super("The value of a attributeFormDefault-property in the schema object is invalid.");
    }
}
