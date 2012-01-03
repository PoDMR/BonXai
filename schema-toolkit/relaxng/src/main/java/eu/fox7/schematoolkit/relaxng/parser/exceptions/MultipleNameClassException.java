package eu.fox7.schematoolkit.relaxng.parser.exceptions;

/**
 * @author Lars Schmidt
 */
public class MultipleNameClassException extends Exception {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public MultipleNameClassException(String tagName) {
        super("There are multiple nameClass-childs under the following tag: " + tagName);
    }

}
