package eu.fox7.bonxai.xsd.parser.exceptions.type;

import eu.fox7.bonxai.common.QualifiedName;

/**
 * @author Lars Schmidt, Dominik Wolff
 */
public class ComplexTypeAnyAttributeException extends eu.fox7.bonxai.xsd.parser.exceptions.XSDParseException {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ComplexTypeAnyAttributeException(QualifiedName name) {
        super("There are additional Attributes to an AnyAttribute in a ComplexType:" + name.getQualifiedName());
    }
}