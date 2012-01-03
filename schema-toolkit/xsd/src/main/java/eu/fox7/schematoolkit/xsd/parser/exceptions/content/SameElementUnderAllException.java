package eu.fox7.schematoolkit.xsd.parser.exceptions.content;

/**
 *
 * @author Lars Schmidt, Dominik Wolff
 */
public class SameElementUnderAllException extends eu.fox7.schematoolkit.xsd.parser.exceptions.XSDParseException {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public SameElementUnderAllException(String elementName) {
        super("There are two elements with the same name under an \"all\"-tag: " + elementName);
    }

}
