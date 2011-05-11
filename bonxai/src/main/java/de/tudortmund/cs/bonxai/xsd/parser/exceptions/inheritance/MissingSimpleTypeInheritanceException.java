package de.tudortmund.cs.bonxai.xsd.parser.exceptions.inheritance;

/**
 *
 * @author Lars Schmidt, Dominik Wolff
 */
public class MissingSimpleTypeInheritanceException extends Exception {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public MissingSimpleTypeInheritanceException(String name) {
        super("Following SimpleType has no inheritance: " + name);
    }

}
