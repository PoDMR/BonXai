package eu.fox7.bonxai.relaxng.parser.exceptions;

/**
 * @author Lars Schmidt
 */
public class CombineMethodNotMatchingException extends Exception {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public CombineMethodNotMatchingException(String tagName) {
        super("There are two different combineMethods used for the same element: " + tagName);
    }

}
