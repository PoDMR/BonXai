package eu.fox7.bonxai.converter.relaxng2xsd.exceptions;

/**
 * There is no combineMethod defined for the following pattern.
 * @author Lars Schmidt
 */
public class NoCombineMethodException extends Exception {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public NoCombineMethodException(String pattern) {
        super("There is no combineMethod defined for the following pattern: \"" + pattern + "\"");
    }

}
