package de.tudortmund.cs.bonxai.dtd.writer.exceptions;

/**
 * The current Attribute is null in the given element
 * @author Lars Schmidt
 */
public class DTDAttributeNullException extends Exception {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public DTDAttributeNullException(String name) {
        super("The current Attribute is null in the following element: " + name);
    }

}
