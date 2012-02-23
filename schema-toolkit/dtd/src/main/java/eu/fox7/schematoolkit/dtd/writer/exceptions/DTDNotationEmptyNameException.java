package eu.fox7.schematoolkit.dtd.writer.exceptions;

import eu.fox7.schematoolkit.dtd.common.exceptions.DTDException;

/**
 * There is no name defined for a notation in the current DTD
 * @author Lars Schmidt
 */
public class DTDNotationEmptyNameException extends DTDException {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public DTDNotationEmptyNameException(String name) {
        super("There is no name defined for a notation in the current DTD");
    }

}
