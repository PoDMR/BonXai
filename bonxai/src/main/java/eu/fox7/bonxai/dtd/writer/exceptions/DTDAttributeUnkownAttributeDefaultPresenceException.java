package eu.fox7.bonxai.dtd.writer.exceptions;

/**
 * There is an unknown attributeDefaultPresence value defined for an attribute
 * @author Lars Schmidt
 */
public class DTDAttributeUnkownAttributeDefaultPresenceException extends Exception {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public DTDAttributeUnkownAttributeDefaultPresenceException(String name) {
        super("There is an unknown attributeDefaultPresence value defined for the following attribute: " + name);
    }

}
