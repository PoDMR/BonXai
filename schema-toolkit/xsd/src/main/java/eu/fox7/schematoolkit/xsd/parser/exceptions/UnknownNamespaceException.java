/**
 * This Exception is thrown when a namespace is used that
 * is not know in the schema's list of namespaces.
 */
package eu.fox7.schematoolkit.xsd.parser.exceptions;

public class UnknownNamespaceException extends eu.fox7.schematoolkit.xsd.parser.exceptions.XSDParseException {

    private static final long serialVersionUID = -3146519691598393144L;

    public UnknownNamespaceException(){}

    public UnknownNamespaceException(String msg)
    {
        super(msg);
    }

}
