package eu.fox7.bonxai.dtd.common.exceptions;

/**
 * A particle has an empty child particle list
 * @author Lars Schmidt
 */
public class ContentModelEmptyChildParticleListException extends Exception {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ContentModelEmptyChildParticleListException(String value) {
        super("The following particle has an empty child particle list: " + value);
    }

}
