package eu.fox7.schematoolkit.dtd.writer.exceptions;

import eu.fox7.schematoolkit.dtd.common.exceptions.DTDException;

/**
 * There is no SystemID and no publicID defined in a DTD notation
 * @author Lars Schmidt
 */
public class DTDNotationIdentifierNullException extends DTDException {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public DTDNotationIdentifierNullException(String name) {
        super("There is no SystemID and no publicID defined in the following DTD notation: " + name);
    }

}
