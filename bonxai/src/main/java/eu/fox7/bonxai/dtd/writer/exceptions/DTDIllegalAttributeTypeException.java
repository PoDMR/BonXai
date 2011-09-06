package eu.fox7.bonxai.dtd.writer.exceptions;

/**
 * There is an illegal attributetype for an attribute
 * @author Lars Schmidt
 */
public class DTDIllegalAttributeTypeException extends Exception {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public DTDIllegalAttributeTypeException(String name) {
        super("There is an illegal AttributeType in the following attribute: " + name);
    }

}
