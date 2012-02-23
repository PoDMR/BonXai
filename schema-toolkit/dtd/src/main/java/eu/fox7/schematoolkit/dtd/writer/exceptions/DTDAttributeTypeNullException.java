package eu.fox7.schematoolkit.dtd.writer.exceptions;

import eu.fox7.schematoolkit.dtd.common.exceptions.DTDException;

/**
 * The type of an attribute is null
 * @author Lars Schmidt
 */
public class DTDAttributeTypeNullException extends DTDException {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public DTDAttributeTypeNullException(String name) {
        super("The type of the following attribute is null: " + name);
    }

}
