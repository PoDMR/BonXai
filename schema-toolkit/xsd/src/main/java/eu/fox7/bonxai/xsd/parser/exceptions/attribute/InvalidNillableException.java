package eu.fox7.bonxai.xsd.parser.exceptions.attribute;

import eu.fox7.bonxai.common.QualifiedName;

/**
 * @author Lars Schmidt, Dominik Wolff
 */
public class InvalidNillableException extends eu.fox7.bonxai.xsd.parser.exceptions.XSDParseException {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
     * Called when a tag has an invalid nillable.
     * @param name
     */
    public InvalidNillableException(QualifiedName name) {
        super("There is an invalid nillable-property in the following object:" + name.getQualifiedName());
    }
}