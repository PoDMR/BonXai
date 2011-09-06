package eu.fox7.bonxai.xsd.parser.exceptions.attribute;

/**
 *
 * @author Lars Schmidt, Dominik Wolff
 */
public class InvalidFinalDefaultValueException extends Exception {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public InvalidFinalDefaultValueException() {
        super("The value of a finalDefault-property in the schema object is invalid.");
    }
}
