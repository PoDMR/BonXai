package eu.fox7.bonxai.relaxng.writer.exceptions;

/**
 * @author Lars Schmidt
 */
public class PatternReferencedButNotDefinedException extends Exception {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public PatternReferencedButNotDefinedException(String patternName) {
        super("The following pattern is referenced, but not defined in the grammar: \"" + patternName + "\"");
    }

}
