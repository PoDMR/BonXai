package de.tudortmund.cs.bonxai.dtd.common.exceptions;

/**
 * A regular expression string for an element content model is empty or null
 * @author Lars Schmidt
 */
public class ContentModelStringEmptyException extends Exception {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ContentModelStringEmptyException() {
        super("The regular expression string for an element content model is empty or null.");
    }

}