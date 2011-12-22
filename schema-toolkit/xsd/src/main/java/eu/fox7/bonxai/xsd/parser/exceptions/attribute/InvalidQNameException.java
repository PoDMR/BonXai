package eu.fox7.bonxai.xsd.parser.exceptions.attribute;

import eu.fox7.bonxai.common.QualifiedName;

/**
 * @author Lars Schmidt, Dominik Wolff
 */
public class InvalidQNameException extends eu.fox7.bonxai.xsd.parser.exceptions.XSDParseException {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
     * Called when a name is no NCName.
     * @param string         Invalid name of the caller
     * @param element      Element type of the caller
     */
    public InvalidQNameException(String string, String element) {
        super("Following name is not a valid QName " + string + " for a " + element);
    }
}
