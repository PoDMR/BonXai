package eu.fox7.bonxai.xsd.parser.exceptions.inheritance;


/**
 * @author Lars Schmidt, Dominik Wolff
 */
public class RestrictionMultipleParticleException extends eu.fox7.bonxai.xsd.parser.exceptions.XSDParseException {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public RestrictionMultipleParticleException(String string) {
        super("There are multiple childs (group | all | choice | sequence)? in an extension:" + string);
    }
}