package eu.fox7.schematoolkit.xsd.parser.exceptions.attribute;

/**
 *
 * @author Lars Schmidt, Dominik Wolff
 */
public class InvalidNamespaceValueException extends eu.fox7.schematoolkit.xsd.parser.exceptions.XSDParseException {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public InvalidNamespaceValueException(String caller) {
        super("The value of a namespace-property is invalid in:" + caller);
    }
}
