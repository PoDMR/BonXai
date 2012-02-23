package eu.fox7.schematoolkit.dtd.parser.exceptions;

import eu.fox7.schematoolkit.dtd.common.exceptions.DTDException;

/**
 * There are duplicate attribute-names in the DTD file
 * @author Lars Schmidt
 */
public class DuplicateAttributeNameException extends DTDException {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public DuplicateAttributeNameException(String attributeName, String elementName) {
        super("There are duplicate attribute-names: \"" + attributeName + "\" under the following element: \"" + elementName + "\"");
    }

}
