package eu.fox7.bonxai.xsd.parser.exceptions.attribute.countingpattern;

/**
 * This exception will be thrown, when a value is not as aspected in the current
 * context and therefore not allowed
 * Example: maxOccurs="unbounded" under an "all"-tag
 *
 * @author Lars Schmidt, Dominik Wolff
 */
public class CountingPatternNotAllowedValueException extends eu.fox7.bonxai.xsd.parser.exceptions.XSDParseException {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public CountingPatternNotAllowedValueException(String caller) {
        super("The value of the CountingPattern is not allowed in the current context: " + caller);
    }
}
