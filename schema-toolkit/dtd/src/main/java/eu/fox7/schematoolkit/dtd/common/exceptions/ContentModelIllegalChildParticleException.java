package eu.fox7.schematoolkit.dtd.common.exceptions;

/**
 * A child particle is illegal in a DTD element content model
 * @author Lars Schmidt
 */
public class ContentModelIllegalChildParticleException extends Exception {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ContentModelIllegalChildParticleException(String value) {
        super("The following child particle is illegal in a DTD element content model: " + value);
    }

}
