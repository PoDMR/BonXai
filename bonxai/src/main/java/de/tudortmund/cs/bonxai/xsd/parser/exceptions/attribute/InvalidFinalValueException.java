package de.tudortmund.cs.bonxai.xsd.parser.exceptions.attribute;

/**
 *
 * @author Lars Schmidt, Dominik Wolff
 */
public class InvalidFinalValueException extends Exception {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public InvalidFinalValueException(String caller) {
        super("The value of a final-property is invalid in:" + caller);
    }
}
