package eu.fox7.schematoolkit.dtd.writer.exceptions;

import eu.fox7.schematoolkit.dtd.common.exceptions.DTDException;

/**
 * There is no value defined in a DTD internal entity
 * @author Lars Schmidt
 */
public class DTDInternalEntityValueNullException extends DTDException {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public DTDInternalEntityValueNullException(String name) {
        super("There is value defined in the following DTD internal entity: " + name);
    }

}
