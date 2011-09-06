package eu.fox7.bonxai.xsd.parser.exceptions.type;

/**
 *
 * @author Lars Schmidt, Dominik Wolff
 */
public class EmptySimpleContentListTypeException extends Exception {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public EmptySimpleContentListTypeException(String simpleContentListString) {
        super("Following SimpleContentList has an itemType of content-type: " + simpleContentListString);
    }
}
