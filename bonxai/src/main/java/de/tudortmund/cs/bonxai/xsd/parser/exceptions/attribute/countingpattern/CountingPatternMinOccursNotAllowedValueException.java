package de.tudortmund.cs.bonxai.xsd.parser.exceptions.attribute.countingpattern;

/**
 * This exception will be thrown, when a value is not as aspected in the current
 * context and therefore not allowed
 * Example: minOccurs="unbounded" under an "all"-tag
 *
 * @author Lars Schmidt, Dominik Wolff
 */
public class CountingPatternMinOccursNotAllowedValueException extends Exception {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public CountingPatternMinOccursNotAllowedValueException(String caller) {
        super("The value of the CountingPattern \"minoccurs\" is not allowed in the current context: " + caller);
    }

}
