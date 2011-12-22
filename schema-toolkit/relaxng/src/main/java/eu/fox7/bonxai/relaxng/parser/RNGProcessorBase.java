package eu.fox7.bonxai.relaxng.parser;

import eu.fox7.bonxai.common.IdentifiedNamespace;
import eu.fox7.bonxai.relaxng.Grammar;
import eu.fox7.bonxai.relaxng.Pattern;
import eu.fox7.bonxai.relaxng.RelaxNGSchema;
import eu.fox7.bonxai.relaxng.parser.exceptions.InvalidAnyUriException;
import eu.fox7.bonxai.xsd.tools.NameChecker;

import org.w3c.dom.*;

/*******************************************************************************
 * Abstract class, which models the basic funktions of every <rngEelement> processor
 *******************************************************************************
 * @author Lars Schmidt
 */
public abstract class RNGProcessorBase {

    /**
     * XSDSchema processed by the processor
     */
    protected RelaxNGSchema rngSchema;
    /**
     * Current Grammar which holds the references of all ref-elements to define-elements
     */
    protected Grammar grammar;
    /**
     * Activates debug mode, in which debug information is written to the console
     */
    static boolean debug = false;

    /**
     * Different cases for possible content model <element>s
     */
    protected enum CASE {

        element,
        attribute,
        group,
        interleave,
        choice,
        optional,
        zeroOrMore,
        oneOrMore,
        list,
        mixed,
        ref,
        parentRef,
        empty,
        text,
        value,
        data,
        notAllowed,
        externalRef,
        grammar,
        param,
        except,
        div,
        start,
        define,
        name,
        anyName,
        nsName,
        include;
//        documentation;
    }

    /**
     * Constructor of the processor, which receives the schema and a grammar object.
     * @param rngSchema
     * @param grammar
     */
    public RNGProcessorBase(RelaxNGSchema rngSchema, Grammar grammar) {
        this.rngSchema = rngSchema;
        this.grammar = grammar;
    }

    /**
     * Constructor of the processor, which receives only the schema.
     * @param rngSchema
     */
    public RNGProcessorBase(RelaxNGSchema rngSchema) {
        this.rngSchema = rngSchema;
        this.grammar = new Grammar();
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
        RNGProcessorBase.debug = debug;
    }

    /**
     * Return true when debug mode is active else false.
     * @return current debug flag
     */
    public static boolean getDebug() {
        return debug;
    }



    /**
     * Check if a given value is valid for the enumeration of XML tags
     * @param value     String for the check
     * @return boolean
     */
    protected boolean valueIsValid(String value) {
        String test = null;
        try {
            test = CASE.valueOf(value).toString();
        } catch (IllegalArgumentException e) {
        }
        return (test != null);
    }

    /**
     * Set pattern attributes to a given pattern extracted from a node
     * @param pattern
     * @param node
     * @throws InvalidAnyUriException
     */
    protected void setPatternAttributes(Pattern pattern, Node node) throws InvalidAnyUriException {
        NamedNodeMap attributes = node.getAttributes();
        if (attributes != null) {
            for (int i = 0; i < attributes.getLength(); i++) {
                if ((attributes.item(i)).getNodeName().startsWith("xmlns")) {
                    Node currentNode = attributes.item(i);
                    if (!NameChecker.isAnyUri(currentNode.getNodeValue())) {
                        throw new InvalidAnyUriException(currentNode.getNodeValue(), "namespace of pattern");
                    }
                    if (currentNode.getNodeName().equals("xmlns")) {
                        pattern.setDefaultNamespace(currentNode.getNodeValue());
                    } else {
                        pattern.getNamespaceList().addNamespace(new IdentifiedNamespace(currentNode.getLocalName(), currentNode.getNodeValue()));
                    }
                }
            }

            if (attributes.getNamedItem("datatypeLibrary") != null) {
                String datatypeLibrary = ((Attr) attributes.getNamedItem("datatypeLibrary")).getValue();
                if (!NameChecker.isAnyUri(datatypeLibrary)) {
                    throw new InvalidAnyUriException(datatypeLibrary, "pattern: datatypeLibrary attribute");
                }
                pattern.setAttributeDatatypeLibrary(datatypeLibrary);
            }
            if (attributes.getNamedItem("ns") != null) {
                String nsString = ((Attr) attributes.getNamedItem("ns")).getValue();
                if (!NameChecker.isAnyUri(nsString)) {
                    throw new InvalidAnyUriException(nsString, "pattern: ns attribute");
                }
                pattern.setAttributeNamespace(nsString);
            }
        }
    }
    
    /**
     * Processes a single child node.
     * @param childNode     Child node which is processed for the parent node
     * @throws java.lang.Exception
     */
    protected abstract void processChild(Node childNode) throws Exception;

}
