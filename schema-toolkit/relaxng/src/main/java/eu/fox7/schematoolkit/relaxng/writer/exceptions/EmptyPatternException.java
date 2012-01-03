package eu.fox7.schematoolkit.relaxng.writer.exceptions;

/**
 * @author Lars Schmidt
 */
public class EmptyPatternException extends Exception {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public EmptyPatternException(String tagName) {
        super("There is no pattern defined under the following tag: " + tagName);
    }

}
