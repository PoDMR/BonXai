package de.tudortmund.cs.bonxai.relaxng.writer;

import de.tudortmund.cs.bonxai.common.IdentifiedNamespace;
import de.tudortmund.cs.bonxai.common.NamespaceList;
import de.tudortmund.cs.bonxai.relaxng.Pattern;
import de.tudortmund.cs.bonxai.xsd.tools.NameChecker;

import java.util.Iterator;
import java.util.LinkedHashSet;
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
        String returnPrefix = "";
        if (this.rootElementNamespaceList != null) {
            if (this.rootElementNamespaceList.getDefaultNamespace() != null) {
                IdentifiedNamespace identifiedNamespace = this.rootElementNamespaceList.getNamespaceByUri(this.rootElementNamespaceList.getDefaultNamespace().getUri());
                if (identifiedNamespace != null && identifiedNamespace.getIdentifier() != null) {
                    returnPrefix = identifiedNamespace.getIdentifier() + ":";
                }
            }
        }
        return returnPrefix;
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
            elementNode.setAttribute("ns", pattern.getAttributeNamespace());
        }

        if (pattern.getAttributeDatatypeLibrary() != null) {
            elementNode.setAttribute("datatypeLibrary", pattern.getAttributeDatatypeLibrary());
        }

//        if (elementNode.getPrefix() == null && pattern.getDefaultNamespace() != null) {
        if (pattern.getDefaultNamespace() != null) {
            elementNode.setAttribute("xmlns", pattern.getDefaultNamespace());
        }

        if (pattern.getNamespaceList() != null) {
//            if (!pattern.getNamespaceList().getDefaultNamespace().getUri().equals(pattern.getDefaultNamespace())) {
//                elementNode.setAttribute("xmlns", pattern.getNamespaceList().getDefaultNamespace().getUri());
//            }
            if (!pattern.getNamespaceList().getIdentifiedNamespaces().isEmpty()) {
                LinkedHashSet<IdentifiedNamespace> idNamespaces = pattern.getNamespaceList().getIdentifiedNamespaces();
                for (Iterator<IdentifiedNamespace> it = idNamespaces.iterator(); it.hasNext();) {
                    IdentifiedNamespace identifiedNamespace = it.next();
                    elementNode.setAttribute("xmlns:" + identifiedNamespace.getIdentifier(), identifiedNamespace.getUri());
                }
            }
        }
    }
}
