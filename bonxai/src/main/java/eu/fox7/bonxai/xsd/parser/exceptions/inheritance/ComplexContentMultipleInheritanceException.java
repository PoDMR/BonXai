package eu.fox7.bonxai.xsd.parser.exceptions.inheritance;

/**
 * @author Lars Schmidt, Dominik Wolff
 */
public class ComplexContentMultipleInheritanceException extends Exception {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ComplexContentMultipleInheritanceException(String name) {
        super("There are multiple inheritance-childs in a ComplexContent:" + name);
    }
}