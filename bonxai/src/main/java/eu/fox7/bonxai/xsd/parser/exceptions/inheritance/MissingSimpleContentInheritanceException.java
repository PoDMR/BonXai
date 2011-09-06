package eu.fox7.bonxai.xsd.parser.exceptions.inheritance;

/**
 *
 * @author Lars Schmidt, Dominik Wolff
 */
public class MissingSimpleContentInheritanceException extends Exception {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public MissingSimpleContentInheritanceException(String name) {
        super("Following SimpleContent has no inheritance: " + name);
    }

}
