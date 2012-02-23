package eu.fox7.schematoolkit.dtd.writer.exceptions;

import eu.fox7.schematoolkit.dtd.common.exceptions.DTDException;

/**
 * There is an illegal attributetype for an attribute
 * @author Lars Schmidt
 */
public class DTDIllegalAttributeTypeException extends DTDException {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public DTDIllegalAttributeTypeException(String name) {
        super("There is an illegal AttributeType in the following attribute: " + name);
    }

}
