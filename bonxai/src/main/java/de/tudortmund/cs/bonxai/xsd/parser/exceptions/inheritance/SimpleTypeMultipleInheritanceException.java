package de.tudortmund.cs.bonxai.xsd.parser.exceptions.inheritance;

/**
 * @author Lars Schmidt, Dominik Wolff
 */
public class SimpleTypeMultipleInheritanceException extends Exception {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public SimpleTypeMultipleInheritanceException(String name) {
        super("There are multiple inheritance-childs in a SimpleType:" + name);
    }
}