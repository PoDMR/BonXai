package eu.fox7.bonxai.converter.relaxng2xsd.exceptions;

import eu.fox7.bonxai.relaxng.Pattern;

/**
 * There are multiple simpleType patterns
 * @author Lars Schmidt
 */
public class MultipleSimpleTypelException extends Exception {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public MultipleSimpleTypelException(Pattern element, String info) {
        super("There are multiple simpleType patterns: \"" + element + "\" (" + info + ")");
    }

}
