package eu.fox7.bonxai.relaxng.writer.exceptions;

/**
 * Exception: A pattern is invalid or not known in this context.
 * @author Lars Schmidt
 */
public class UnknownPatternException extends Exception {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public UnknownPatternException(String patternName) {
        super ("The following pattern is invalid or not known: " + patternName);
    }

}
