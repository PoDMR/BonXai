package eu.fox7.schematoolkit.xsd.parser.exceptions.content;

/**
 *
 * @author Lars Schmidt, Dominik Wolff
 */
public class WrongComplexContentMixedValueException extends eu.fox7.schematoolkit.xsd.parser.exceptions.XSDParseException {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public WrongComplexContentMixedValueException(String complexContentString) {
        super("Following ComplexContent has an illegal mixed-property: " + complexContentString);
    }
}
