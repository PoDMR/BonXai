/**
 * This Exception is thrown when a namespace is used that
 * is not know in the schema's list of namespaces.
 */
package eu.fox7.schematoolkit.relaxng.parser.exceptions;

public class UnknownNamespaceException extends Exception {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public UnknownNamespaceException(){}

    public UnknownNamespaceException(String msg)
    {
        super(msg);
    }

}
