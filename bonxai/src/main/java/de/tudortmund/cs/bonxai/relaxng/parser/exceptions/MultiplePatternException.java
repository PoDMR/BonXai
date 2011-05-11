package de.tudortmund.cs.bonxai.relaxng.parser.exceptions;

/**
 * @author Lars Schmidt
 */
public class MultiplePatternException extends Exception {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public MultiplePatternException(String tagName) {
        super("There are multiple pattern-childs under the following tag: " + tagName);
    }

}
