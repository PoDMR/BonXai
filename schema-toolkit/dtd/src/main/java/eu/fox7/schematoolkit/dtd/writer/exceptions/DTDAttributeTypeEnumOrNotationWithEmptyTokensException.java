package eu.fox7.schematoolkit.dtd.writer.exceptions;

import eu.fox7.schematoolkit.dtd.common.exceptions.DTDException;

/**
 * There are no tokens for a Enumeration or Notation type Attribute
 * @author Lars Schmidt
 */
public class DTDAttributeTypeEnumOrNotationWithEmptyTokensException extends DTDException {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public DTDAttributeTypeEnumOrNotationWithEmptyTokensException(String name, String type) {
        super("There are no tokens for a Enumeration or Notation type Attribute: " + name + " Type: " + type);
    }

}
