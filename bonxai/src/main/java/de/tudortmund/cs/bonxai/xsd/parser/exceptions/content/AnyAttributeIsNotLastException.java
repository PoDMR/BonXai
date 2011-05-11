package de.tudortmund.cs.bonxai.xsd.parser.exceptions.content;

/**
 * @author Lars Schmidt, Dominik Wolff
 */
public class AnyAttributeIsNotLastException extends Exception {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public AnyAttributeIsNotLastException(String name) {
        super("There is already an anyAttribute in the current attributeGroup, while adding:" + name);
    }
}