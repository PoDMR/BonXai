package eu.fox7.bonxai.xsd.parser.exceptions.constraint;

/**
 * @author Lars Schmidt, Dominik Wolff
 */
public class DuplicateConstraintException extends Exception {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
     * Called when there are two constraints with the same name.
     * @param constraintName       Name of the calling constraint.
     */
    public DuplicateConstraintException(String constraintName) {
        super("Following duplicate constraint name was found in the XSD: " + constraintName);
    }
}