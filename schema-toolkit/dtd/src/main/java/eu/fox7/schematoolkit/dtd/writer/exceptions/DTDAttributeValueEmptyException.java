package eu.fox7.schematoolkit.dtd.writer.exceptions;

import eu.fox7.schematoolkit.dtd.common.exceptions.DTDException;

/**
 * There is no value defined for an attribute in the current DTD
 * @author Lars Schmidt
 */
public class DTDAttributeValueEmptyException extends DTDException {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public DTDAttributeValueEmptyException(String name) {
        super("There is no value defined for an attribute in the current DTD in element: " + name);
    }

}
