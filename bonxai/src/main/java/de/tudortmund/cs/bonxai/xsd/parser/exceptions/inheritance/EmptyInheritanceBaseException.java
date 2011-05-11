package de.tudortmund.cs.bonxai.xsd.parser.exceptions.inheritance;

/**
 * @author Lars Schmidt, Dominik Wolff
 */
public class EmptyInheritanceBaseException extends Exception {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public EmptyInheritanceBaseException(String name) {
        super("There is an empty base-property in the following inheritance:" + name);
    }
}