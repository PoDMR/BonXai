package eu.fox7.bonxai.xsd.parser.exceptions.attribute;

import eu.fox7.bonxai.common.QualifiedName;

/**
 * @author Lars Schmidt, Dominik Wolff
 */
public class ExclusiveAttributesException extends eu.fox7.bonxai.xsd.parser.exceptions.XSDParseException {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
     * Called when an object has two exlusive attributes.
     * @param exclusiveAttributes   Attributes which exclude each other.
     * @param qualifiedName                Name of calling object.
     */
    public ExclusiveAttributesException(String exclusiveAttributes, QualifiedName qualifiedName) {
        super("Following attributes exclude each other: " + exclusiveAttributes + " in object " + qualifiedName.getFullyQualifiedName());
    }
}