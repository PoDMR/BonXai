package eu.fox7.bonxai.xsd.parser.exceptions.attribute;

/**
 *
 * @author Lars Schmidt, Dominik Wolff
 */
public class InvalidBlockDefaultValueException extends Exception {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public InvalidBlockDefaultValueException() {
        super("The value of a blockDefault-property in the schema object is invalid.");
    }
}
