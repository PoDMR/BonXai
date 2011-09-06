package eu.fox7.bonxai.relaxng.parser.exceptions;

/**
 * @author Lars Schmidt
 */
public class ParentGrammarIsNullException extends Exception {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
     * Called when a parent grammar is null
     * @param patternName
     */
    public ParentGrammarIsNullException(String patternName) {
        super("The parent grammar is null: " + patternName);
    }
}
