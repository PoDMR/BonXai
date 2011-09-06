package eu.fox7.bonxai.dtd.writer.exceptions;

/**
 * The type of an attribute is null
 * @author Lars Schmidt
 */
public class DTDAttributeTypeNullException extends Exception {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public DTDAttributeTypeNullException(String name) {
        super("The type of the following attribute is null: " + name);
    }

}
