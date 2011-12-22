package eu.fox7.bonxai.xsd.parser.exceptions.attribute;

import eu.fox7.bonxai.common.QualifiedName;

/**
 *
 * @author Lars Schmidt, Dominik Wolff
 */
public class InvalidFinalLocationException extends eu.fox7.bonxai.xsd.parser.exceptions.XSDParseException {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public InvalidFinalLocationException(QualifiedName qualifiedName) {
        super(qualifiedName.getQualifiedName() + "has a final-attribute and is not global");
    }
}
