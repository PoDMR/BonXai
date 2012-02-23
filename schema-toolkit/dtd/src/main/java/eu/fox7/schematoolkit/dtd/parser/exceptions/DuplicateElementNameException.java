package eu.fox7.schematoolkit.dtd.parser.exceptions;

import eu.fox7.schematoolkit.dtd.common.exceptions.DTDException;

/**
 * There are duplicate element-names in the DTD file
 * @author Lars Schmidt
 */
public class DuplicateElementNameException extends DTDException {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public DuplicateElementNameException(String elementName) {
        super("There are duplicate element-names in the DTD file: \"" + elementName + "\"");
    }

}
