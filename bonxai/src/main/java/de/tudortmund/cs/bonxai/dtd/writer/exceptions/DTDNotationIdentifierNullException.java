package de.tudortmund.cs.bonxai.dtd.writer.exceptions;

/**
 * There is no SystemID and no publicID defined in a DTD notation
 * @author Lars Schmidt
 */
public class DTDNotationIdentifierNullException extends Exception {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public DTDNotationIdentifierNullException(String name) {
        super("There is no SystemID and no publicID defined in the following DTD notation: " + name);
    }

}
