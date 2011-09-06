package eu.fox7.bonxai.xsd.parser.exceptions.type;

/**
 * @author Lars Schmidt, Dominik Wolff
 */
public class ComplexTypeMultipleContentException extends Exception {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ComplexTypeMultipleContentException(String name) {
        super("There are multiple content-childs in a ComplexType:" + name);
    }
}