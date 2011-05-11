package de.tudortmund.cs.bonxai.relaxng.parser.exceptions;

/**
 * @author Lars Schmidt
 */
public class MultipleExceptException extends Exception {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public MultipleExceptException(String tagName) {
        super("There are multiple except-childs under the following tag: " + tagName);
    }

}
