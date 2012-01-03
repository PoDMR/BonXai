package eu.fox7.schematoolkit.xsd.parser.exceptions.content;

/**
 * Attributes have to be the last elements in the content model of the following
 * tags:
 * - complexTypes, 
 * - extension,
 * - restrictions
 * 
 * @author Lars Schmidt, Dominik Wolff
 */
public class AttributeIsNotLastInContentModelException extends eu.fox7.schematoolkit.xsd.parser.exceptions.XSDParseException {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public AttributeIsNotLastInContentModelException(String string) {
        super("There is already an attribute in the current contentModel, while adding:" + string);
    }
}