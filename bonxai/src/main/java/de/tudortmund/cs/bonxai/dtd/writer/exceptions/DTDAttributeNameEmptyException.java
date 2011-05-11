package de.tudortmund.cs.bonxai.dtd.writer.exceptions;

/**
 * There is no name defined for an attribute in the current DTD
 * @author Lars Schmidt
 */
public class DTDAttributeNameEmptyException extends Exception {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public DTDAttributeNameEmptyException(String name) {
        super("There is no name defined for an attribute in the current DTD in element: " + name);
    }

}
