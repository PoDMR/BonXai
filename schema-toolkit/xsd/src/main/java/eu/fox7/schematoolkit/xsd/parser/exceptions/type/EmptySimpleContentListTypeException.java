package eu.fox7.schematoolkit.xsd.parser.exceptions.type;

/**
 *
 * @author Lars Schmidt, Dominik Wolff
 */
public class EmptySimpleContentListTypeException extends eu.fox7.schematoolkit.xsd.parser.exceptions.XSDParseException {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public EmptySimpleContentListTypeException(String simpleContentListString) {
        super("Following SimpleContentList has an itemType of content-type: " + simpleContentListString);
    }
}
