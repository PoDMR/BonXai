package eu.fox7.schematoolkit.relaxng.parser.exceptions;

/**
 * @author Lars Schmidt
 */
public class PatternNotInitializedException extends Exception {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
     * Called when a pattern has not been initialized before returning it to the preceding
     * processor.
     * @param patternName
     * @param parent 
     */
    public PatternNotInitializedException(String patternName, String parent) {
        super("Following pattern has not been initialized: " + patternName + " under " + parent);
    }
}
