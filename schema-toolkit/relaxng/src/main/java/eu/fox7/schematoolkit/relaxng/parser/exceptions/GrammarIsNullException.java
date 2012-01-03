package eu.fox7.schematoolkit.relaxng.parser.exceptions;

/**
 * @author Lars Schmidt
 */
public class GrammarIsNullException extends Exception {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
     * Called when a grammar is null
     * @param patternName
     */
    public GrammarIsNullException(String patternName) {
        super("A grammar is null: " + patternName);
    }
}
