package eu.fox7.schematoolkit.xsd.parser.exceptions.attribute;

import eu.fox7.schematoolkit.common.QualifiedName;

/**
 *
 * @author Lars Schmidt, Dominik Wolff
 */
public class InvalidUseValueException extends eu.fox7.schematoolkit.xsd.parser.exceptions.XSDParseException {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public InvalidUseValueException(QualifiedName attributeName) {
        super("The value of a use-property is invalid in:" + attributeName.getFullyQualifiedName());
    }
}
