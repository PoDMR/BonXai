package de.tudortmund.cs.bonxai.relaxng.parser.exceptions;

/**
 * @author Lars Schmidt
 */
public class NodeIsNullException extends Exception {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
     * Called when a DOM node is null
     * @param patternName
     */
    public NodeIsNullException() {
        super("A DOM node is null");
    }
}
