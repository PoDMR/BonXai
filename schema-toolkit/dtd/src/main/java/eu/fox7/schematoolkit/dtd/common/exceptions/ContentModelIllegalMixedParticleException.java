package eu.fox7.schematoolkit.dtd.common.exceptions;

/**
 * The content model of an element with \"mixed\" content is illegal
 * @author Lars Schmidt
 */
public class ContentModelIllegalMixedParticleException extends Exception {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ContentModelIllegalMixedParticleException(String message) {
        super("The content model of an element with \"mixed\" content is illegal: " + message);
    }

}
