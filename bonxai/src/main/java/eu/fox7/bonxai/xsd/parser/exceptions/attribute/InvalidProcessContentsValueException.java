package eu.fox7.bonxai.xsd.parser.exceptions.attribute;

/**
 *
 * @author Lars Schmidt, Dominik Wolff
 */
public class InvalidProcessContentsValueException extends Exception {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public InvalidProcessContentsValueException(String caller) {
        super("The value of a processContents-property is invalid in:" + caller);
    }
}
