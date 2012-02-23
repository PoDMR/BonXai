package eu.fox7.schematoolkit.dtd.writer.exceptions;

import eu.fox7.schematoolkit.dtd.common.exceptions.DTDException;

/**
 * The current Attribute is null in the given element
 * @author Lars Schmidt
 */
public class DTDAttributeNullException extends DTDException {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public DTDAttributeNullException(String name) {
        super("The current Attribute is null in the following element: " + name);
    }

}
