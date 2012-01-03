package eu.fox7.bonxai.xsd.parser.exceptions.inheritance;

/**
 * @author Lars Schmidt, Dominik Wolff
 */
public class RestrictionWrongChildException extends eu.fox7.bonxai.xsd.parser.exceptions.XSDParseException {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public RestrictionWrongChildException(String parent, String particle) {
        super("There is a \"" + particle + "\" under a \"" + parent + "\", which is not allowed in this way!");
    }
}