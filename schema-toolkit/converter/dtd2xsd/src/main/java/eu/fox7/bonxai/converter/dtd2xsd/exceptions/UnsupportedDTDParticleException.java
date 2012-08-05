package eu.fox7.bonxai.converter.dtd2xsd.exceptions;

import eu.fox7.schematoolkit.exceptions.ConversionFailedException;

/**
 * There is an unsupported DTD particle used in a content model of an element
 * @author Lars Schmidt
 */
public class UnsupportedDTDParticleException extends ConversionFailedException {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public UnsupportedDTDParticleException(String particleName) {
        super("There is an unsupported DTD particle used in a content model of an element: " + particleName);
    }

}
