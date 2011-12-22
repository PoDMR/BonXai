package eu.fox7.bonxai.xsd.parser.exceptions.content;

import eu.fox7.bonxai.common.QualifiedName;

/**
 * @author Lars Schmidt, Dominik Wolff
 */
public class ExclusiveContentException extends eu.fox7.bonxai.xsd.parser.exceptions.XSDParseException {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
     * Called when an object has two exlusive attributes.
     * @param exclusiveAttributes   Attributes which exclude each other.
     * @param qualifiedName                Name of calling object. 
     */
    public ExclusiveContentException(String exclusiveAttributes, QualifiedName qualifiedName) {
        super("Following attributes and/or content objects  exclude each other: " + exclusiveAttributes + " in object " + qualifiedName.getQualifiedName());
    }
}