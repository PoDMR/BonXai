package de.tudortmund.cs.bonxai.xsd.parser.exceptions.content;

/**
 *
 * @author Lars Schmidt, Dominik Wolff
 */
public class SameElementUnderAllException extends Exception {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public SameElementUnderAllException(String elementName) {
        super("There are two elements with the same name under an \"all\"-tag: " + elementName);
    }

}
