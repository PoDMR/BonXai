package eu.fox7.schematoolkit.xsd.parser.exceptions.attribute;

import eu.fox7.schematoolkit.common.QualifiedName;

/**
 * @author Lars Schmidt, Dominik Wolff
 */
public class NonGlobalSubstitutionGroupException extends eu.fox7.schematoolkit.xsd.parser.exceptions.XSDParseException {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
     * Called when an element is not global and has a substitutionGroup-attribute
     * @param name
     */
    public NonGlobalSubstitutionGroupException(QualifiedName name) {
        super("There is an empty substitutionGroup-property in the following element:" + name.getQualifiedName() + " which is not declared globally.");
    }
}