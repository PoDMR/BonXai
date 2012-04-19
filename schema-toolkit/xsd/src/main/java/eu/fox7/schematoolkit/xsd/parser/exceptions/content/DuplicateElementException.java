package eu.fox7.schematoolkit.xsd.parser.exceptions.content;

import eu.fox7.schematoolkit.common.QualifiedName;

/**
 * @author Lars Schmidt, Dominik Wolff
 */
public class DuplicateElementException extends eu.fox7.schematoolkit.xsd.parser.exceptions.XSDParseException {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
     * Called when there are two keys with the same name.
     * @param name       Name of the calling element.
     */
    public DuplicateElementException(QualifiedName name) {
        super("Following duplicate element name was found in the XSD: " + name.getFullyQualifiedName());
    }
}