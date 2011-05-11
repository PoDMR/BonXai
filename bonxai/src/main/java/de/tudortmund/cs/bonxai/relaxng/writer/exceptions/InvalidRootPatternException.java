package de.tudortmund.cs.bonxai.relaxng.writer.exceptions;

/**
 * Exception: The pattern in position of the document root is not allowed in Relax NG XSDSchema.
 * @author Lars Schmidt
 */
public class InvalidRootPatternException extends Exception {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public InvalidRootPatternException(String rootClass) {
        super ("The pattern in position of the document root is not allowed in Relax NG XSDSchema: " + rootClass);
    }

}
