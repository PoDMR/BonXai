package eu.fox7.bonxai.relaxng.parser.exceptions;

 /**
 * @author Lars Schmidt
 */
public class UnsupportedContentException extends Exception {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
     * Called when there is a not supported or invalid content under the given "caller"-tag
     * @param content       Name of the unsupported content
     * @param caller        Node which does not support the given content
     */
    public UnsupportedContentException(String content, String caller) {
        super("Content element \"" +content +  "\" is not supported in " + caller);
    }
}