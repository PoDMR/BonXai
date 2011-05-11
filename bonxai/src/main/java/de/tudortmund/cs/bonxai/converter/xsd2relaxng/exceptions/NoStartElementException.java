package de.tudortmund.cs.bonxai.converter.xsd2relaxng.exceptions;

/**
 * There is no start element in a given XML XSDSchema
 * @author Lars Schmidt
 */
public class NoStartElementException extends Exception {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public NoStartElementException() {
        super("There is no start element in a given XML XSDSchema");
    }

}
