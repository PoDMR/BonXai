package eu.fox7.bonxai.converter.dtd2xsd.exceptions;

/**
 * There is an unsupported DTD particle used in a content model of an element
 * @author Lars Schmidt
 */
public class UnsupportedDTDParticleException extends Exception {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public UnsupportedDTDParticleException(String particleName) {
        super("There is an unsupported DTD particle used in a content model of an element: " + particleName);
    }

}
