package eu.fox7.bonxai.xsd.parser.exceptions.inheritance;

/**
 *
 * @author Lars Schmidt, Dominik Wolff
 */
public class MissingComplexContentInheritanceException extends Exception {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public MissingComplexContentInheritanceException(String name) {
        super("Following ComplexContent has no inheritance: " + name);
    }

}
