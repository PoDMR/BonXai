package eu.fox7.schematoolkit.xsd.parser.exceptions.attribute;

/**
 *
 * @author Lars Schmidt, Dominik Wolff
 */
public class InvalidProcessContentsValueException extends eu.fox7.schematoolkit.xsd.parser.exceptions.XSDParseException {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public InvalidProcessContentsValueException(String caller) {
        super("The value of a processContents-property is invalid in:" + caller);
    }
}
