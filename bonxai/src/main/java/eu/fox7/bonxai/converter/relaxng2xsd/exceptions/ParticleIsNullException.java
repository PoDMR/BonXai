package eu.fox7.bonxai.converter.relaxng2xsd.exceptions;

/**
 * A generated particle is null
 * @author Lars Schmidt
 */
public class ParticleIsNullException extends Exception {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ParticleIsNullException(String particle, String info) {
        super("The following generated particle is null: \"" + particle + "\" (" + info + ")");
    }

}