package eu.fox7.bonxai.dtd.writer.exceptions;

/**
 * There is no value defined for an attribute in the current DTD
 * @author Lars Schmidt
 */
public class DTDAttributeValueEmptyException extends Exception {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public DTDAttributeValueEmptyException(String name) {
        super("There is no value defined for an attribute in the current DTD in element: " + name);
    }

}
