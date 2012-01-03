package eu.fox7.schematoolkit.xsd.parser.exceptions.inheritance;

/**
 * @author Lars Schmidt, Dominik Wolff
 */
public class ExtensionWrongChildException extends eu.fox7.schematoolkit.xsd.parser.exceptions.XSDParseException {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ExtensionWrongChildException(String parent, String particle) {
        super("There is a \"" + particle + "\" under a \"" + parent + "\", which is not allowed in this way!");
    }
}