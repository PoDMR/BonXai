package eu.fox7.bonxai.xsd.parser.exceptions.attribute;

/**
 *
 * @author Lars Schmidt, Dominik Wolff
 */
public class InvalidFinalLocationException extends Exception {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public InvalidFinalLocationException(String caller) {
        super(caller + "has a final-attribute and is not global");
    }
}
