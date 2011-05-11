package de.tudortmund.cs.bonxai.converter.relaxng2xsd.exceptions;

/**
 * The following pattern is not allowed in the current context.
 * @author Lars Schmidt
 */
public class PatternNotAllowedException extends Exception {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public PatternNotAllowedException(String pattern, String context) {
        super("The following pattern is not allowed in the current context: pattern(\"" + pattern + "\") context(\"" + context + "\")");
    }

}
