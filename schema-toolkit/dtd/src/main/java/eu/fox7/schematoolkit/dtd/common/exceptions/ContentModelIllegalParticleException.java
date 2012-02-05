package eu.fox7.schematoolkit.dtd.common.exceptions;

/**
 * A particle is illegal in a DTD element content model
 * @author Lars Schmidt
 */
public class ContentModelIllegalParticleException extends Exception {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ContentModelIllegalParticleException(String elementName, String name) {
        super("The following particle is illegal in a DTD element content model: " + name + " in " + elementName);
    }

}
