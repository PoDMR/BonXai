/**
 * This Exception is thrown when a namespace is used that
 * is not know in the schema's list of namespaces.
 */
package de.tudortmund.cs.bonxai.xsd.parser.exceptions;

public class UnknownNamespaceException extends Exception {

    private static final long serialVersionUID = -3146519691598393144L;

    public UnknownNamespaceException(){}

    public UnknownNamespaceException(String msg)
    {
        super(msg);
    }

}
