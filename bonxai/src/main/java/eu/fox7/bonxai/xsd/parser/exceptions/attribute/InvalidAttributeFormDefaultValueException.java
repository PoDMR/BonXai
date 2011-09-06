package eu.fox7.bonxai.xsd.parser.exceptions.attribute;

/**
 *
 * @author Lars Schmidt, Dominik Wolff
 */
public class InvalidAttributeFormDefaultValueException extends Exception {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public InvalidAttributeFormDefaultValueException() {
        super("The value of a attributeFormDefault-property in the schema object is invalid.");
    }
}
