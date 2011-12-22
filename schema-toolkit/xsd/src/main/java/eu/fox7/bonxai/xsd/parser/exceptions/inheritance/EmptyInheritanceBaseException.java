package eu.fox7.bonxai.xsd.parser.exceptions.inheritance;

/**
 * @author Lars Schmidt, Dominik Wolff
 */
public class EmptyInheritanceBaseException extends eu.fox7.bonxai.xsd.parser.exceptions.XSDParseException {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public EmptyInheritanceBaseException(String string) {
        super("There is an empty base-property in the following inheritance:" + string);
    }
}