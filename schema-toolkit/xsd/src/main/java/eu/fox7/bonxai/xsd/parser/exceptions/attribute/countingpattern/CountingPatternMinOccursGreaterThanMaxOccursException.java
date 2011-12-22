package eu.fox7.bonxai.xsd.parser.exceptions.attribute.countingpattern;

/**
 * This exception will be thrown, when a minOccurs-value is greater than a
 * maxOccurs-value. This is not allowed per definition.
 * Example: minOccurs="5" maxOccurs="2"
 *
 * minOccurs > maxOccurs
 *
 * @author Lars Schmidt, Dominik Wolff
 */
public class CountingPatternMinOccursGreaterThanMaxOccursException extends eu.fox7.bonxai.xsd.parser.exceptions.XSDParseException {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public CountingPatternMinOccursGreaterThanMaxOccursException(String caller) {
        super("The value of minOccurs is greater than (>) the value of maxOccurs in the following CountingPattern: " + caller);
    }
}
