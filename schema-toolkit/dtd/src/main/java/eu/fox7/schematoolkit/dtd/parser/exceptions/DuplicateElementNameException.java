package eu.fox7.schematoolkit.dtd.parser.exceptions;

/**
 * There are duplicate element-names in the DTD file
 * @author Lars Schmidt
 */
public class DuplicateElementNameException extends Exception {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public DuplicateElementNameException(String elementName) {
        super("There are duplicate element-names in the DTD file: \"" + elementName + "\"");
    }

}
