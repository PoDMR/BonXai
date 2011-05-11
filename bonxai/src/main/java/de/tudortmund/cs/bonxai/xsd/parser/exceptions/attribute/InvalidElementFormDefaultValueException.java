package de.tudortmund.cs.bonxai.xsd.parser.exceptions.attribute;

/**
 *
 * @author Lars Schmidt, Dominik Wolff
 */
public class InvalidElementFormDefaultValueException extends Exception {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public InvalidElementFormDefaultValueException() {
        super("The value of a elementFormDefault-property in the schema object is invalid.");
    }
}
