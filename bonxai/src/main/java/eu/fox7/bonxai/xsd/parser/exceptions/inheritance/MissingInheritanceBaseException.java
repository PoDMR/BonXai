package eu.fox7.bonxai.xsd.parser.exceptions.inheritance;

/**
 * @author Lars Schmidt, Dominik Wolff
 */
public class MissingInheritanceBaseException extends Exception {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public MissingInheritanceBaseException(String name) {
        super("There is no base-property in the following inheritance:" + name);
    }
}