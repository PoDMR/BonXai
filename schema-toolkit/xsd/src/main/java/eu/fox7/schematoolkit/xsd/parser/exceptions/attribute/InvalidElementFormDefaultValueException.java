package eu.fox7.schematoolkit.xsd.parser.exceptions.attribute;

/**
 *
 * @author Lars Schmidt, Dominik Wolff
 */
public class InvalidElementFormDefaultValueException extends eu.fox7.schematoolkit.xsd.parser.exceptions.XSDParseException {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public InvalidElementFormDefaultValueException() {
        super("The value of a elementFormDefault-property in the schema object is invalid.");
    }
}
