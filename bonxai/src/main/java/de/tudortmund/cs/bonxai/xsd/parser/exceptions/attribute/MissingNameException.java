package de.tudortmund.cs.bonxai.xsd.parser.exceptions.attribute;

/**
 *
 * @author Lars Schmidt, Dominik Wolff
 */
public class MissingNameException extends Exception {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public MissingNameException() {
        super("An element is missing a name and has no ref-attribute");
    }
}
