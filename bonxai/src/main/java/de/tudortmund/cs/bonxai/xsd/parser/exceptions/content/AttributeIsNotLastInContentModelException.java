package de.tudortmund.cs.bonxai.xsd.parser.exceptions.content;

/**
 * Attributes have to be the last elements in the content model of the following
 * tags:
 * - complexTypes, 
 * - extension,
 * - restrictions
 * 
 * @author Lars Schmidt, Dominik Wolff
 */
public class AttributeIsNotLastInContentModelException extends Exception {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public AttributeIsNotLastInContentModelException(String name) {
        super("There is already an attribute in the current contentModel, while adding:" + name);
    }
}