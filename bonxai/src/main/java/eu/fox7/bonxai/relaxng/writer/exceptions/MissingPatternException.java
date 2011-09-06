package eu.fox7.bonxai.relaxng.writer.exceptions;

/**
 * Exception: A pattern is missing in this context.
 * @author Lars Schmidt
 */
public class MissingPatternException extends Exception {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public MissingPatternException(String patternName) {
        super ("There is no pattern given for: " + patternName);
    }
}
