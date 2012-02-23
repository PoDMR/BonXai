package eu.fox7.schematoolkit.dtd.writer.exceptions;

import eu.fox7.schematoolkit.dtd.common.exceptions.DTDException;

/**
 * There is an unknown attributeDefaultPresence value defined for an attribute
 * @author Lars Schmidt
 */
public class DTDAttributeUnkownAttributeDefaultPresenceException extends DTDException {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public DTDAttributeUnkownAttributeDefaultPresenceException(String name) {
        super("There is an unknown attributeDefaultPresence value defined for the following attribute: " + name);
    }

}
