package de.tudortmund.cs.bonxai.xsd.parser.exceptions.attribute;

/**
 *
 * @author Lars Schmidt, Dominik Wolff
 */
public class InvalidUseValueException extends Exception {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public InvalidUseValueException(String caller) {
        super("The value of a use-property is invalid in:" + caller);
    }
}
