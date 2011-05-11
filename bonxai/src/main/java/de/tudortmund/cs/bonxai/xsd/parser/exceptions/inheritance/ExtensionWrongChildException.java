package de.tudortmund.cs.bonxai.xsd.parser.exceptions.inheritance;

/**
 * @author Lars Schmidt, Dominik Wolff
 */
public class ExtensionWrongChildException extends Exception {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ExtensionWrongChildException(String parent, String particle) {
        super("There is a \"" + particle + "\" under a \"" + parent + "\", which is not allowed in this way!");
    }
}