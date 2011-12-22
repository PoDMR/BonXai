package eu.fox7.bonxai.xsd.parser.exceptions.content;

/**
 * @author Lars Schmidt, Dominik Wolff
 */
public class GroupMultipleParticleContainerException extends eu.fox7.bonxai.xsd.parser.exceptions.XSDParseException {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public GroupMultipleParticleContainerException(String string) {
        super("There are multiple particleContainer in a group:" + string);
    }
}