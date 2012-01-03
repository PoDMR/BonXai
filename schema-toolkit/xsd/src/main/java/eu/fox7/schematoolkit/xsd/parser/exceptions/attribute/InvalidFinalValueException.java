package eu.fox7.schematoolkit.xsd.parser.exceptions.attribute;

import eu.fox7.schematoolkit.common.QualifiedName;

/**
 *
 * @author Lars Schmidt, Dominik Wolff
 */
public class InvalidFinalValueException extends eu.fox7.schematoolkit.xsd.parser.exceptions.XSDParseException {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public InvalidFinalValueException(QualifiedName qualifiedName) {
        super("The value of a final-property is invalid in:" + qualifiedName.getQualifiedName());
    }
}
