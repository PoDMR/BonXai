package eu.fox7.bonxai.xsd.parser.exceptions.attribute;

import eu.fox7.bonxai.common.QualifiedName;

/**
 * @author Lars Schmidt, Dominik Wolff
 */
public class InvalidAbstractException extends eu.fox7.bonxai.xsd.parser.exceptions.XSDParseException {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
     * Called when a tag has an invalid abstract.
     * @param name
     */
    public InvalidAbstractException(QualifiedName name) {
        super("There is an invalid abstract-property in the following object:" + name.getQualifiedName());
    }
}