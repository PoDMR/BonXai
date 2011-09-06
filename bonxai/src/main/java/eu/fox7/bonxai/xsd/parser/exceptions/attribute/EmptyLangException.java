package eu.fox7.bonxai.xsd.parser.exceptions.attribute;

/**
 * @author Lars Schmidt, Dominik Wolff
 */
public class EmptyLangException extends Exception {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
     * Called when a tag has an empty source.
     * @param name Name of the caller object
     */
    public EmptyLangException(String name) {
        super("There is an empty xml:lang-property in the following object:" + name);
    }
}