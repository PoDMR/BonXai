package eu.fox7.bonxai.relaxng.parser.exceptions;

/**
 * @author Lars Schmidt
 */
public class ReturnObjectIsNullException extends Exception {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ReturnObjectIsNullException(String objectName) {
        super("Return object is null: " + objectName);
    }
}
