package eu.fox7.schematoolkit.xsd.parser.exceptions.type;

import eu.fox7.schematoolkit.common.QualifiedName;

/**
 * @author Lars Schmidt, Dominik Wolff
 */
public class ComplexTypeMultipleContentException extends eu.fox7.schematoolkit.xsd.parser.exceptions.XSDParseException {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ComplexTypeMultipleContentException(QualifiedName name) {
        super("There are multiple content-childs in a ComplexType:" + name.getQualifiedName());
    }
}