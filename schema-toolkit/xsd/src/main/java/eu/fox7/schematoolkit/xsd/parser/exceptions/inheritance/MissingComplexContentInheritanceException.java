package eu.fox7.schematoolkit.xsd.parser.exceptions.inheritance;

/**
 *
 * @author Lars Schmidt, Dominik Wolff
 */
public class MissingComplexContentInheritanceException extends eu.fox7.schematoolkit.xsd.parser.exceptions.XSDParseException {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public MissingComplexContentInheritanceException(String string) {
        super("Following ComplexContent has no inheritance: " + string);
    }

}
