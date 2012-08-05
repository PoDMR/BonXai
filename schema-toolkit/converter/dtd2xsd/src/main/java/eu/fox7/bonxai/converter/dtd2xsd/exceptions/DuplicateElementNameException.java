package eu.fox7.bonxai.converter.dtd2xsd.exceptions;

import eu.fox7.schematoolkit.exceptions.ConversionFailedException;

/**
 * There are duplicate element-names in the resulting XML XSDSchema file
 * @author Lars Schmidt
 */
public class DuplicateElementNameException extends ConversionFailedException {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public DuplicateElementNameException(String elementName) {
        super("There are duplicate element-names in the resulting XML XSDSchema file: \"" + elementName + "\"");
    }

}
