package eu.fox7.schematoolkit.xsd.parser;

import eu.fox7.schematoolkit.common.QualifiedName;
import eu.fox7.schematoolkit.xsd.om.*;
import eu.fox7.schematoolkit.xsd.parser.exceptions.XSDParseException;
import eu.fox7.schematoolkit.xsd.parser.exceptions.attribute.*;
import eu.fox7.schematoolkit.xsd.parser.exceptions.constraint.*;
import eu.fox7.schematoolkit.xsd.parser.exceptions.content.*;

import org.w3c.dom.*;

/*******************************************************************************
 * Class processes a key constraint for the parser and returns a key object of
 * the XML-XSDSchema object model to the caller class.
 *******************************************************************************
 * @author Lars Schmidt, Dominik Wolff
 */
public class KeyProcessor extends Processor {

    // Key with selector and fields created by this class
    private Key key;

    // Name of the above key
    private QualifiedName keyName;

    /**
     * Constructor of the KeyProcessor, which receives only the schema. This is
     * necessary to update the KeySymbolTable
     * @param schema    New schema created by the parser and its processors
     */
    public KeyProcessor(XSDSchema schema) {
        super(schema);
    }

    /**
     * Visits a child of the key node and processes it according to its name
     * @param childNode     Node in the Dom tree below the Node labeled with key
     * @throws java.lang.Exception
     */
    @Override
    protected void processChild(Node childNode) throws XSDParseException {

        // Tests if the node name is a local name and filters nodes with names #text, #comment and #document who are not in the enum
        String nodeName = childNode.getNodeName();
        if (nodeName.contains(":")) {
            nodeName = nodeName.split(":")[1];
        }
        NamedNodeMap attributes;
        if (childNode != null && !"#text".equals(nodeName) && !"#comment".equals(nodeName) && !"#document".equals(nodeName)) {
            switch (CASE.valueOf(nodeName.toUpperCase())) {
                case SELECTOR:
                    if (getDebug()) {
                        System.out.println("selector");
                    }
                    // If the selector is already set an exception is thrown
                    if (!key.getSelector().equals("")) {
                        throw new DuplicateSelectorException(key);
                    }
                    // The selector of a key is not allowed to be empty
                    attributes = childNode.getAttributes();
                    if (attributes != null && attributes.getNamedItem("xpath") != null && !attributes.getNamedItem("xpath").getNodeValue().equals("")) {
                        key.setSelector(attributes.getNamedItem("xpath").getNodeValue());
                    } else {
                        throw new EmptySelectorException(key);
                    }
                    break;
                case FIELD:
                    if (getDebug()) {
                        System.out.println("field");
                    }
                    // The field too is not allowed to be empty
                    attributes = childNode.getAttributes();
                    if (attributes != null && attributes.getNamedItem("xpath") != null && !attributes.getNamedItem("xpath").getNodeValue().equals("")) {
                        key.addField(attributes.getNamedItem("xpath").getNodeValue());
                    } else {
                        throw new EmptyFieldException(key);
                    }
                    break;
                case ANNOTATION:
                    if (key.getAnnotation() == null) {
                        AnnotationProcessor annotationProcessor = new AnnotationProcessor(schema);
                        key.setAnnotation(annotationProcessor.processNode(childNode));
                    } else {
                        throw new MultipleAnnotationException("key");
                    }
                    break;
                default:
                    throw new UnsupportedContentException(nodeName, "key");
            }
        }
    }

    /**
     * Creates a key corresponding to the key node in the dom tree.
     * @param node      Node labeled with key in the dom tree
     * @return key object representing a key in XML XSDSchema
     * @throws java.lang.Exception
     */
    @Override
    protected Key processNode(Node node) throws XSDParseException {

        // Creates the key and visits all children to set selector and fields
        keyName = getName(node);
        key = new Key(keyName, "");

        visitChildren(node);

        // For every key a selector has to be present. If not an exception is thrown.
        if (key.getSelector().equals("")) {
            throw new MissingSelectorException(key);
        }
        // A key has to contain at least one field
        if (key.getFields().isEmpty()) {
            throw new MissingFieldException(key);
        }
        // Registers the key in the KeyAndUniqueSymbolTable
        if (node.getAttributes().getNamedItem("id") != null) {
            if (node.getAttributes().getNamedItem("id").getNodeValue().equals("")) {
                throw new EmptyIdException("Key");
            }
            key.setId(node.getAttributes().getNamedItem("id").getNodeValue());
        }
        key.setDummy(false);
        return key;
    }
}