package eu.fox7.schematoolkit.relaxng.writer;

import eu.fox7.schematoolkit.common.IdentifiedNamespace;
import eu.fox7.schematoolkit.common.NamespaceList;
import eu.fox7.schematoolkit.relaxng.om.Pattern;
import eu.fox7.schematoolkit.xsd.tools.NameChecker;

import org.w3c.dom.Document;

/**
 * Base class for all Relax NG Simple XML Syntax writer classes.
 * @author Lars Schmidt
 */
public abstract class RNGWriterBase extends NameChecker{

    /**
     * Variable for holding the RELAX NG document object
     */
    protected org.w3c.dom.Document rngDocument;
    /**
     * List of identified namespaces from the given root pattern
     */
    protected NamespaceList rootElementNamespaceList;

    /**
     * Constructor of the class RNGWriterBase
     * @param rngDocument
     * @param rootElementNamespaceList
     */
    protected RNGWriterBase(Document rngDocument, NamespaceList rootElementNamespaceList) {
        this.rngDocument = rngDocument;
        this.rootElementNamespaceList = rootElementNamespaceList;
    }

    /**
     * Get a prefix for the RELAX NG namespace
     * @return String       prefix for the RELAX NG namespace
     */
    protected String getPrefixForRNGNamespace() {
    	//TODO
//    	String returnPrefix = "";
//        if (this.rootElementNamespaceList != null) {
//            if (this.rootElementNamespaceList.getDefaultNamespace() != null) {
//                IdentifiedNamespace identifiedNamespace = this.rootElementNamespaceList.getNamespaceByUri(this.rootElementNamespaceList.getDefaultNamespace().getUri());
//                if (identifiedNamespace != null && identifiedNamespace.getIdentifier() != null) {
//                    returnPrefix = identifiedNamespace.getIdentifier() + ":";
//                }
//            }
//        }
        return "";
    }

    /**
     * Create an element node for the XML tree.
     * @param name      Name of the node
     * @return org.w3c.dom.Element      The generated Element node
     */
    protected org.w3c.dom.Element createElementNode(String name) {
        if (this.rootElementNamespaceList != null) {
            return this.rngDocument.createElementNS(this.rootElementNamespaceList.getDefaultNamespace().getUri(), getPrefixForRNGNamespace() + name);
        } else {
            return this.rngDocument.createElement(name);
        }
    }

    /**
     * Set the values from pattern attributes from a given pattern to an element node
     * @param elementNode       target
     * @param pattern           source
     */
    protected void setPatternAttributes(org.w3c.dom.Element elementNode, Pattern pattern) {
        if (pattern.getAttributeNamespace() != null) {
            elementNode.setAttribute("ns", pattern.getAttributeNamespace().getUri());
        }

        if (pattern.getAttributeDatatypeLibrary() != null) {
            elementNode.setAttribute("datatypeLibrary", pattern.getAttributeDatatypeLibrary());
        }

        if (pattern.getDefaultNamespace() != null) {
            elementNode.setAttribute("xmlns", pattern.getDefaultNamespace());
        }

        if (pattern.getNamespaceList() != null) {
        	for (IdentifiedNamespace identifiedNamespace: pattern.getNamespaceList().getNamespaces())
        		elementNode.setAttribute("xmlns:" + identifiedNamespace.getIdentifier(), identifiedNamespace.getUri());
        }
    }
}
