package eu.fox7.schematoolkit.xsd.parser.exceptions.inheritance;

/**
 *
 * @author Lars Schmidt, Dominik Wolff
 */
public class MissingSimpleContentInheritanceException extends eu.fox7.schematoolkit.xsd.parser.exceptions.XSDParseException {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public MissingSimpleContentInheritanceException(String string) {
        super("Following SimpleContent has no inheritance: " + string);
    }

}
