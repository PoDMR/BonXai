package eu.fox7.bonxai.xsd.parser.exceptions.content;

import eu.fox7.bonxai.common.QualifiedName;

/**
 * @author Lars Schmidt, Dominik Wolff
 */
public class DuplicateAttributeException extends eu.fox7.bonxai.xsd.parser.exceptions.XSDParseException {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
     * Called when there are two keys with the same name.
     * @param name       Name of the calling element.
     */
    public DuplicateAttributeException(QualifiedName name) {
        super("Following duplicate attribute name was found in the XSD: " + name.getQualifiedName());
    }
}