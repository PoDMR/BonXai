package eu.fox7.bonxai.xsd.parser.exceptions.content;

/**
 *
 * @author Lars Schmidt, Dominik Wolff
 */
public class IllegalObjectReturnedException extends Exception {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public IllegalObjectReturnedException(String objectName, String caller) {
        super("Illegal instance of " + objectName + " is not allowed to be used in " + caller);
    }
}
