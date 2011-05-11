package de.tudortmund.cs.bonxai.xsd.parser.exceptions.inheritance;

/**
 * @author Lars Schmidt, Dominik Wolff
 */
public class SimpleContentMultipleInheritanceException extends Exception {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public SimpleContentMultipleInheritanceException(String name) {
        super("There are multiple inheritance-childs in a SimpleContent:" + name);
    }
}