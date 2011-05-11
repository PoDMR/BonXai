package de.tudortmund.cs.bonxai.converter.dtd2xsd.exceptions;

/**
 * There are duplicate element-names in the resulting XML XSDSchema file
 * @author Lars Schmidt
 */
public class DuplicateElementNameException extends Exception {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public DuplicateElementNameException(String elementName) {
        super("There are duplicate element-names in the resulting XML XSDSchema file: \"" + elementName + "\"");
    }

}
