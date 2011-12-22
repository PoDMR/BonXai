package eu.fox7.bonxai.relaxng.parser.exceptions;

/**
 * There is a nameClass content and a name-attribute set in the current XML element
 * @author Lars Schmidt
 */
public class MultipleNameException extends Exception {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public MultipleNameException(String tagName) {
        super("There is a nameClass content and a name-attribute set in the current tag: " + tagName);
    }

}
