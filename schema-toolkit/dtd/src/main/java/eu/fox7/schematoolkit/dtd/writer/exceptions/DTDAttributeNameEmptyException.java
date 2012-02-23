package eu.fox7.schematoolkit.dtd.writer.exceptions;

import eu.fox7.schematoolkit.dtd.common.exceptions.DTDException;

/**
 * There is no name defined for an attribute in the current DTD
 * @author Lars Schmidt
 */
public class DTDAttributeNameEmptyException extends DTDException {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public DTDAttributeNameEmptyException(String name) {
        super("There is no name defined for an attribute in the current DTD in element: " + name);
    }

}
