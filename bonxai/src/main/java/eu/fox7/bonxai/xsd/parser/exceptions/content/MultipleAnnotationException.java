package eu.fox7.bonxai.xsd.parser.exceptions.content;

/**
 *
 * @author Lars Schmidt, Dominik Wolff
 */
public class MultipleAnnotationException extends Exception {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public MultipleAnnotationException(String tagName) {
        super("There are multiple annotation-childs under the following tag: " + tagName);
    }

}
