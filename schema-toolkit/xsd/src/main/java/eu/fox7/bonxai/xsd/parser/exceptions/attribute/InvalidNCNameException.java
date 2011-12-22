package eu.fox7.bonxai.xsd.parser.exceptions.attribute;

/**
 * @author Lars Schmidt, Dominik Wolff
 */
public class InvalidNCNameException extends eu.fox7.bonxai.xsd.parser.exceptions.XSDParseException {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
     * Called when a name is no NCName.
     * @param string         Invalid name of the caller
     * @param element      Element type of the caller
     */
    public InvalidNCNameException(String string, String element) {
        super("Following name is not a valid NCName " + string + " for a " + element);
    }
}
