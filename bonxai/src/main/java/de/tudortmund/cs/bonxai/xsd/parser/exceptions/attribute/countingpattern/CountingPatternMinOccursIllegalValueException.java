package de.tudortmund.cs.bonxai.xsd.parser.exceptions.attribute.countingpattern;

/**
 * This exception will be thrown, when a value is not correct corresponding to
 * the XSD-specification of the W3C. Example: minOccurs="-10"
 * 
 * @author Lars Schmidt, Dominik Wolff
 */
public class CountingPatternMinOccursIllegalValueException extends Exception {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public CountingPatternMinOccursIllegalValueException(String caller) {
        super("The value of the CountingPattern \"minoccurs\" is illegal: " + caller);
    }

}
