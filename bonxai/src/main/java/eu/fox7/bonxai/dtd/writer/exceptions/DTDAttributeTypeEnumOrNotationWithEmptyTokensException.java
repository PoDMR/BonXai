package eu.fox7.bonxai.dtd.writer.exceptions;

/**
 * There are no tokens for a Enumeration or Notation type Attribute
 * @author Lars Schmidt
 */
public class DTDAttributeTypeEnumOrNotationWithEmptyTokensException extends Exception {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public DTDAttributeTypeEnumOrNotationWithEmptyTokensException(String name, String type) {
        super("There are no tokens for a Enumeration or Notation type Attribute: " + name + " Type: " + type);
    }

}
