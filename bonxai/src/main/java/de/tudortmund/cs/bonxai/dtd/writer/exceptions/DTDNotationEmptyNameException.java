package de.tudortmund.cs.bonxai.dtd.writer.exceptions;

/**
 * There is no name defined for a notation in the current DTD
 * @author Lars Schmidt
 */
public class DTDNotationEmptyNameException extends Exception {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public DTDNotationEmptyNameException(String name) {
        super("There is no name defined for a notation in the current DTD");
    }

}
