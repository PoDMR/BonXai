package eu.fox7.schematoolkit.xsd.parser;

import java.util.LinkedHashSet;
import java.util.UUID;

import eu.fox7.schematoolkit.common.*;
import eu.fox7.schematoolkit.xsd.om.*;
import eu.fox7.schematoolkit.xsd.parser.exceptions.*;
import eu.fox7.schematoolkit.xsd.tools.NameChecker;

import org.w3c.dom.*;

/*******************************************************************************
 * Abstract class, which models the basic functions of every <element> processor
 *******************************************************************************
 * @author Lars Schmidt, Dominik Wolff
 */
public abstract class Processor extends NameChecker {

    // XSDSchema processed by the processor
    protected XSDSchema schema;

    // Activates debug mode, in which debug information is written to the console
    static boolean debug = false;

    // Different cases for possible content model <element>s
    protected enum CASE {

        ELEMENT, SCHEMA, COMPLEXTYPE, SIMPLETYPE, LIST, UNION, GROUP, ALL, ANY, CHOICE, SEQUENCE, INCLUDE, IMPORT, REDEFINE, FIELD, ENUMERATION,
        KEY, KEYREF, SELECTOR, UNIQUE, ANYATTRIBUTE, ATTRIBUTE, ATTRIBUTEGROUP, COMPLEXCONTENT, SIMPLECONTENT, EXTENSION, RESTRICTION, MINEXCLUSIVE,
        MININCLUSIVE, MAXEXCLUSIVE, MAXINCLUSIVE, TOTALDIGITS, FRACTIONDIGITS, LENGTH, MINLENGTH, MAXLENGTH, WHITESPACE, PATTERN, ANNOTATION, NOTATION,
        APPINFO, DOCUMENTATION;
    }

    /**
     * Constructor of the processor, which receives only the schema.
     * @param schema    New schema created by the parser and its processors
     */
    public Processor(XSDSchema schema) {
        this.schema = schema;
    }

    /**
     * Processes a node corresponding to the type of the node
     * @param node  Node in the dom tree, which is processed
     * @return Object of the XSD object model representing the node type
     * @throws java.lang.Exception
     */
    // This method must not be used directly.
    protected abstract Object processNode(Node node) throws XSDParseException;

    /**
     * Processes a single child node.
     * @param childNode     Child node which is processed for the parent node
     * @throws java.lang.Exception
     */
    protected abstract void processChild(Node childNode) throws XSDParseException;

    /**
     * Visits all children of the parent node and calls the processChild methode
     * for each of them.
     * @param node  Parent node, which defines the type of the processor
     * @throws java.lang.Exception
     */
    protected void visitChildren(Node node) throws XSDParseException {
        NodeList nl = node.getChildNodes();
        for (int i = 0, cnt = nl.getLength(); i < cnt; i++) {
            processChild(nl.item(i));
        }
    }

    /**
     * Activates or deactivates debug mode
     * @param debug     If true console output is enabled else it is disabled
     */
    public static void setDebug(boolean debug) {
        Processor.debug = debug;
    }

    /**
     * Return true when debug mode is active else false.
     * @return current debug flag
     */
    public static boolean getDebug() {
        return debug;
    }

    /**
     * Returns the fullqualified name of a node
     * @param node      Node which name needs to be known
     * @return Fullqualified node name
     * @throws DOMException 
     * @throws UnknownNamespaceException 
     */
    protected QualifiedName getName(Node node) throws UnknownNamespaceException, DOMException {
    	return getName(node, true);
    }
    
    protected QualifiedName getAttributeName(Node node) throws UnknownNamespaceException, DOMException {
    	return getName(node, false);
    }
    
    protected QualifiedName getName(Node node, boolean useDefaultNamespace) throws UnknownNamespaceException, DOMException {
        Node nameNode = node.getAttributes().getNamedItem("name");
        if (nameNode != null && !nameNode.getNodeValue().equals(""))
        	return this.getName(nameNode.getNodeValue(), useDefaultNamespace);
        else {
        	String returnName = this.generateUniqueName(node);
        	Namespace namespace = this.schema.getTargetNamespace();
        	return new QualifiedName(namespace, returnName);
        }
    }



    /**
     * Returns a name for a node which has no name, because it represents an
     * anonymous <element>
     * @param node  Node which has no name
     * @return  Unique name for the specified node
     */
    protected String generateUniqueName(Node node) {
        String returnName = node.getNodeName();
        String uniqueRandID = java.util.UUID.randomUUID().toString();
        if (returnName.contains(":")) {
            returnName = returnName.split(":")[1];
        }
        if (node.getAttributes().getNamedItem("name") != null && !node.getAttributes().getNamedItem("name").getNodeValue().equals("")) {
            returnName = "_" + node.getAttributes().getNamedItem("name").getNodeValue();
        }
        return returnName + "-" + node.hashCode() + uniqueRandID;
    }

    /**
     * Transforms a xmlNameReference, which is like a name for XSD <element>s,
     * into a fullqualified name.
     * @param xmlNameReference      Name of the from: namespace(prefix):elementname
     * @return  Fullqualified element name corresponding to the xmlNameReference
     * @throws eu.fox7.schematoolkit.xsd.om.parser.exceptions.UnknownNamespaceException
     */
    protected QualifiedName getName(String xmlNameReference) throws UnknownNamespaceException {
    	return this.getName(xmlNameReference, true);
    }

    
    protected QualifiedName getName(String xmlNameReference, boolean useDefaultNamespace) throws UnknownNamespaceException {
        String returnName = "";
        Namespace namespace = null;
        
        // The name is prefixed with a namespace-abbreviation,
        // ==> look up the right namespace in the namespace list
        if (xmlNameReference.indexOf(":") > -1) {
            String[] xmlNameReferenceArray = xmlNameReference.split(":");
            namespace = schema.getNamespaceByIdentifier(xmlNameReferenceArray[0]);
            if (namespace != null && namespace.getUri() != null) {
                returnName = xmlNameReferenceArray[1];
            } else {
                throw new UnknownNamespaceException(xmlNameReferenceArray[0]);
            }
        }
        // The name is unqualified and therefore not prefixed with a namespace
        // abbreviation ==> look up the default namespace
        else {
            if (useDefaultNamespace)
            	namespace = schema.getDefaultNamespace();
            else
            	namespace = Namespace.EMPTY_NAMESPACE;
            returnName = xmlNameReference;
        }
        return new QualifiedName(namespace, returnName);
    }
    
    protected QualifiedName getUniqueName() {
    	return new QualifiedName(schema.getDefaultNamespace(),UUID.randomUUID().toString());
    }

}
