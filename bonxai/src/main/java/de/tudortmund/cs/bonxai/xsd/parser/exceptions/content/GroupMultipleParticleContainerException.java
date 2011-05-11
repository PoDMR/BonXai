package de.tudortmund.cs.bonxai.xsd.parser.exceptions.content;

/**
 * @author Lars Schmidt, Dominik Wolff
 */
public class GroupMultipleParticleContainerException extends Exception {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public GroupMultipleParticleContainerException(String name) {
        super("There are multiple particleContainer in a group:" + name);
    }
}