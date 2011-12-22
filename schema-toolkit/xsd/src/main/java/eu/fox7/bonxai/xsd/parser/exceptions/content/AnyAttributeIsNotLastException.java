package eu.fox7.bonxai.xsd.parser.exceptions.content;

/**
 * @author Lars Schmidt, Dominik Wolff
 */
public class AnyAttributeIsNotLastException extends eu.fox7.bonxai.xsd.parser.exceptions.XSDParseException {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public AnyAttributeIsNotLastException(String string) {
        super("There is already an anyAttribute in the current attributeGroup, while adding:" + string);
    }
}