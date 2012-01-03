package eu.fox7.schematoolkit.xsd.parser.exceptions.inheritance;

import eu.fox7.schematoolkit.common.QualifiedName;

/**
 * @author Lars Schmidt, Dominik Wolff
 */
public class MissingInheritanceBaseException extends eu.fox7.schematoolkit.xsd.parser.exceptions.XSDParseException {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public MissingInheritanceBaseException(QualifiedName name) {
        super("There is no base-property in the following inheritance:" + name.getQualifiedName());
    }
}