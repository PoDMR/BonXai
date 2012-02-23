package eu.fox7.schematoolkit.dtd.writer.exceptions;

import eu.fox7.schematoolkit.dtd.common.exceptions.DTDException;

/**
 * There is an empty content model for an element
 * @author Lars Schmidt
 */
public class DTDElementContentModelEmptyException extends DTDException {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public DTDElementContentModelEmptyException(String name) {
        super("There is an empty content model for the following element: " + name);
    }

}
