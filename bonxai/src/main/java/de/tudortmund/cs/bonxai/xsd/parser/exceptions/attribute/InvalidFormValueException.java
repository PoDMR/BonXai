package de.tudortmund.cs.bonxai.xsd.parser.exceptions.attribute;

/**
 *
 * @author Lars Schmidt, Dominik Wolff
 */
public class InvalidFormValueException extends Exception {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public InvalidFormValueException(String caller) {
        super("The value of a form-property is invalid in:" + caller);
    }
}
