package eu.fox7.bonxai.xsd.parser.exceptions.attribute;

import eu.fox7.bonxai.common.QualifiedName;

/**
 *
 * @author Lars Schmidt, Dominik Wolff
 */
public class InvalidBlockValueException extends eu.fox7.bonxai.xsd.parser.exceptions.XSDParseException {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public InvalidBlockValueException(QualifiedName qualifiedName) {
        super("The value of a block-property is invalid in:" + qualifiedName.getQualifiedName());
    }
}
