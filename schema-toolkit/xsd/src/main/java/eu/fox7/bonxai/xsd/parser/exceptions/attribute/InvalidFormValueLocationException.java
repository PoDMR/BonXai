package eu.fox7.bonxai.xsd.parser.exceptions.attribute;

import eu.fox7.bonxai.common.QualifiedName;

/**
 *
 * @author Lars Schmidt, Dominik Wolff
 */
public class InvalidFormValueLocationException extends eu.fox7.bonxai.xsd.parser.exceptions.XSDParseException {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public InvalidFormValueLocationException(QualifiedName qualifiedName) {
        super("The value of a form-property is invalid in:" + qualifiedName.getQualifiedName() + " because it is a global element");
    }
}
