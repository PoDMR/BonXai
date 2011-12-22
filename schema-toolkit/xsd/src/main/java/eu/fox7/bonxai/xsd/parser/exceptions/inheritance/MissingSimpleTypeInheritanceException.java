package eu.fox7.bonxai.xsd.parser.exceptions.inheritance;

import eu.fox7.bonxai.common.QualifiedName;

/**
 *
 * @author Lars Schmidt, Dominik Wolff
 */
public class MissingSimpleTypeInheritanceException extends eu.fox7.bonxai.xsd.parser.exceptions.XSDParseException {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public MissingSimpleTypeInheritanceException(QualifiedName name) {
        super("Following SimpleType has no inheritance: " + name.getQualifiedName());
    }

}
