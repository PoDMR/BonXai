package de.tudortmund.cs.bonxai.converter.relaxng2xsd.exceptions;

import de.tudortmund.cs.bonxai.relaxng.Pattern;

/**
 * There is more than one simpleTypePattern (data/value) under the given element
 * @author Lars Schmidt
 */
public class TooManyDataOrValuePatternsUnderAnElementException extends Exception {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public TooManyDataOrValuePatternsUnderAnElementException(Pattern element, String info) {
        super("There is more than one simpleTypePattern (data/value) under the given element: \"" + element + "\" (" + info + ")");
    }

}
