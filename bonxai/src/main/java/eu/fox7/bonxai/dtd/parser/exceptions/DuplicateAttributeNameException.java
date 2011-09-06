package eu.fox7.bonxai.dtd.parser.exceptions;

/**
 * There are duplicate attribute-names in the DTD file
 * @author Lars Schmidt
 */
public class DuplicateAttributeNameException extends Exception {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public DuplicateAttributeNameException(String attributeName, String elementName) {
        super("There are duplicate attribute-names: \"" + attributeName + "\" under the following element: \"" + elementName + "\"");
    }

}
