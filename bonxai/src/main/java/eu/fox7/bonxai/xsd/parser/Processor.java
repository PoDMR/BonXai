package eu.fox7.bonxai.xsd.parser;

import eu.fox7.bonxai.common.*;
import eu.fox7.bonxai.xsd.*;
import eu.fox7.bonxai.xsd.parser.exceptions.*;
import eu.fox7.bonxai.xsd.tools.NameChecker;

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
    protected abstract Object processNode(Node node) throws Exception;

    /**
     * Processes a single child node.
     * @param childNode     Child node which is processed for the parent node
     * @throws java.lang.Exception
     */
    protected abstract void processChild(Node childNode) throws Exception;

    /**
     * Visits all children of the parent node and calls the processChild methode
     * for each of them.
     * @param node  Parent node, which defines the type of the processor
     * @throws java.lang.Exception
     */
    protected void visitChildren(Node node) throws Exception {
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
     */
    protected String getName(Node node) {
        String returnName = this.getLocalName(node);
        String namespace = this.getNamespace(node);
        return "{" + namespace + "}" + returnName;
    }

    /**
     * Returns the name of a node without namespace
     * @param node      Node which local name needs to be known
     * @return  Local name of this node without namespace
     */
    protected String getLocalName(Node node) {
        String returnName = "";
        if (node.getAttributes().getNamedItem("name") != null && !node.getAttributes().getNamedItem("name").getNodeValue().equals("")) {
            returnName = node.getAttributes().getNamedItem("name").getNodeValue();
        } else {
            // Generate new unique name
            returnName = this.generateUniqueName(node);
        }
        return returnName;
    }

    /**
     *  Returns a correct namespace following this rules: All
     *  Elements/Types/Attributes defined in this schema are defined in
     *  the targetNamespace, if there is no targetNamespace they are defined
     *  in the defaultNamespace (which is in the current implementation equal
     *  to the targeNamespace). If there is no targetNamespace or
     *  defaultNamespace we use the namespace of the node or its parent. In
     *  this case we assume the namespace as the XML schema namespace.
     * @param node     Node for which the namespace is computed
     * @return namespace of the specified node
     */
    protected String getNamespace(Node node) {
        String namespaceResult = "";
         if (schema.getTargetNamespace() != null) {
            namespaceResult = schema.getTargetNamespace();
        } else {
            if (node != null && node.getNamespaceURI() != null) {
                namespaceResult = node.getNamespaceURI();
            } else {
                if (node != null) {
                    namespaceResult = getNamespace(node.getParentNode());
                }
            }
        }
        return namespaceResult;
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
     * @throws eu.fox7.bonxai.xsd.parser.exceptions.UnknownNamespaceException
     */
    protected String getName(String xmlNameReference) throws UnknownNamespaceException {
        String returnName = "";
        String returnNamespace = "";
        NamespaceList namespaceList = this.schema.getNamespaceList();

        // The name is prefixed with a namespace-abbreviation,
        // ==> look up the right namespace in the namespace list
        if (xmlNameReference.indexOf(":") > -1) {
            String[] xmlNameReferenceArray = xmlNameReference.split(":");
            IdentifiedNamespace namespace = namespaceList.getNamespaceByIdentifier(xmlNameReferenceArray[0]);
            if (namespace != null && namespace.getUri() != null) {
                returnNamespace = namespace.getUri();
                returnName = xmlNameReferenceArray[1];
            } else {
                throw new UnknownNamespaceException(xmlNameReferenceArray[0]);
            }
        }
        // The name is unqualified and therefore not prefixed with a namespace
        // abbreviation ==> look up the default namespace
        else if (namespaceList.getDefaultNamespace() != null) {
            returnNamespace = namespaceList.getDefaultNamespace().getUri();
            returnName = xmlNameReference;
        }
        // If default namespace is null
        if (returnName.equals("")) {
            System.err.print("No default namespace");
            returnName = xmlNameReference;
        }
        return "{" + returnNamespace + "}" + returnName;
    }

}
