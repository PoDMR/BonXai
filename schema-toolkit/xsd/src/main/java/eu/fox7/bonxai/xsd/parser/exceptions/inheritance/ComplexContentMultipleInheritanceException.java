package eu.fox7.bonxai.xsd.parser.exceptions.inheritance;

/**
 * @author Lars Schmidt, Dominik Wolff
 */
public class ComplexContentMultipleInheritanceException extends eu.fox7.bonxai.xsd.parser.exceptions.XSDParseException {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ComplexContentMultipleInheritanceException(String string) {
        super("There are multiple inheritance-childs in a ComplexContent:" + string);
    }
}