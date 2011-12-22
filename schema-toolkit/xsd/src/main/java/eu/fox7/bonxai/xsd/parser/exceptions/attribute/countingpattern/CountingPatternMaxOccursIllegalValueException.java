package eu.fox7.bonxai.xsd.parser.exceptions.attribute.countingpattern;

/**
 * This exception will be thrown, when a value is not correct corresponding to
 * the XSD-specification of the W3C. Example: maxOccurs="-5"
 *
 * @author Lars Schmidt, Dominik Wolff
 */
public class CountingPatternMaxOccursIllegalValueException extends eu.fox7.bonxai.xsd.parser.exceptions.XSDParseException {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public CountingPatternMaxOccursIllegalValueException(String caller) {
        super("The value of the CountingPattern \"maxoccurs\" is illegal: " + caller);
    }

}
