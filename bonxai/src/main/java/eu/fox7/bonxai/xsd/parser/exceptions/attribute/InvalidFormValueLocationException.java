package eu.fox7.bonxai.xsd.parser.exceptions.attribute;

/**
 *
 * @author Lars Schmidt, Dominik Wolff
 */
public class InvalidFormValueLocationException extends Exception {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public InvalidFormValueLocationException(String caller) {
        super("The value of a form-property is invalid in:" + caller + " because it is a global element");
    }
}
