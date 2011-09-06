package eu.fox7.bonxai.xsd.parser.exceptions.content;

 /**
 * @author Lars Schmidt, Dominik Wolff
 */
public class UnsupportedContentException extends Exception {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
     * Called when there are two keys with the same name.
     * @param content       Name of the unsupported content
     * @param caller        Node which does not support content
     */
    public UnsupportedContentException(String content, String caller) {
        super("Content element " +content +  " is not supported in " + caller);
    }
}