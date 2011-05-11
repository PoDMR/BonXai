package de.tudortmund.cs.bonxai.relaxng.writer.exceptions;

/**
 * Exception: A nameClass element is invalid or not known in this context.
 * @author Lars Schmidt
 */
public class UnknownNameClassException extends Exception {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public UnknownNameClassException(String patternName) {
        super ("The nameClass element is invalid or not known: " + patternName);
    }

}
