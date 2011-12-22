package eu.fox7.bonxai.xsd.parser.exceptions.type;

import eu.fox7.bonxai.common.QualifiedName;

/**
 * @author Lars Schmidt, Dominik Wolff
 */
public class ComplexTypeMultipleContentException extends eu.fox7.bonxai.xsd.parser.exceptions.XSDParseException {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ComplexTypeMultipleContentException(QualifiedName name) {
        super("There are multiple content-childs in a ComplexType:" + name.getQualifiedName());
    }
}