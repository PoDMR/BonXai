package eu.fox7.bonxai.xsd.parser.exceptions.inheritance;

/**
 * @author Lars Schmidt, Dominik Wolff
 */
public class RestrictionMultipleParticleException extends Exception {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public RestrictionMultipleParticleException(String name) {
        super("There are multiple childs (group | all | choice | sequence)? in an extension:" + name);
    }
}