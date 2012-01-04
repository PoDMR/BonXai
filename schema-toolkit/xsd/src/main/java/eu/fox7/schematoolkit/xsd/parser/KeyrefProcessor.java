package eu.fox7.schematoolkit.xsd.parser;

import eu.fox7.schematoolkit.common.QualifiedName;
import eu.fox7.schematoolkit.xsd.om.*;
import eu.fox7.schematoolkit.xsd.parser.exceptions.attribute.*;
import eu.fox7.schematoolkit.xsd.parser.exceptions.constraint.*;
import eu.fox7.schematoolkit.xsd.parser.exceptions.content.*;

import org.w3c.dom.*;

/*******************************************************************************
 * Class processes a keyref constraint for the parser and returns a keyref object
 * of the XML-XSDSchema object model to the caller class.
 *******************************************************************************
 * @author Lars Schmidt, Dominik Wolff
 */
public class KeyrefProcessor extends Processor {

    // Keyref object with selector and fields created by this class
    private KeyRef keyRef;
    // Name of the above keyref object
    private QualifiedName keyRefName;
    // Name of the refered key
    private QualifiedName refer;

    /**
     * Constructor of the KeyrefProcessor, which receives only the schema.
     * @param schema    New schema created by the parser and its processors
     */
    public KeyrefProcessor(XSDSchema schema) {
        super(schema);
    }

    /**
     * Visits a child of the keyref node and processes it according to its name
     * @param childNode     Node in the Dom tree below the Node labeled with keyref
     * @throws java.lang.Exception
     */
    @Override
    protected void processChild(Node childNode) throws Exception {

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
                    if (!keyRef.getSelector().equals("")) {
                        throw new DuplicateSelectorException(keyRef);
                    }
                    // The selector of a keyref constraint is not allowed to be empty
                    attributes = childNode.getAttributes();
                    if (attributes != null && attributes.getNamedItem("xpath") != null && !attributes.getNamedItem("xpath").getNodeValue().equals("")) {
                        keyRef.setSelector(attributes.getNamedItem("xpath").getNodeValue());
                    } else {
                        throw new EmptySelectorException(keyRef);
                    }
                    break;
                case FIELD:
                    if (getDebug()) {
                        System.out.println("field");
                    }
                    // The field too is not allowed to be empty
                    attributes = childNode.getAttributes();
                    if (attributes != null && attributes.getNamedItem("xpath") != null && !attributes.getNamedItem("xpath").getNodeValue().equals("")) {
                        keyRef.addField(attributes.getNamedItem("xpath").getNodeValue());
                    } else {
                        throw new EmptyFieldException(keyRef);
                    }
                    break;
                case ANNOTATION:
                    if (getDebug()) {
                        System.out.println("annotation");
                    }
                    if (keyRef.getAnnotation() == null) {
                        AnnotationProcessor annotationProcessor = new AnnotationProcessor(schema);
                        keyRef.setAnnotation(annotationProcessor.processNode(childNode));
                    } else {
                        throw new MultipleAnnotationException("keyRef");
                    }
                    break;
                default:
                    throw new UnsupportedContentException(nodeName, "keyRef");
            }
        }
    }

    /**
     * Creates a keyref object corresponding to the keyref node in the Dom tree.
     * @param node      Node labeled with keyref in the Dom tree
     * @return keyref object representing a keyref constraint in XML XSDSchema
     * @throws java.lang.Exception
     */
    @Override
    protected KeyRef processNode(Node node) throws Exception {

        // Creates the unique object and visits all children to set selector and fields
        keyRefName = getName(node);

        // Checks if the refer attribute is set and not empty than adds it to the object
        NamedNodeMap attributes = node.getAttributes();
        if (attributes != null && attributes.getNamedItem("refer") != null) {
            if (!attributes.getNamedItem("refer").getNodeValue().equals("")) {
                refer = getName(((Attr) attributes.getNamedItem("refer")).getValue());
                if (!isQName(((Attr) attributes.getNamedItem("refer")).getValue())) {
                    throw new InvalidQNameException(((Attr) attributes.getNamedItem("refer")).getValue(), "keyRef");
                }
            } else {
                throw new EmptyReferException("keyRef: " + getName(node));
            }
        } else {
            throw new MissingReferException("keyRef: " + getName(node));
        }

        // Creates the keyref with a correct reference from the KeySymbolTable
        Key key = schema.getKey(refer);
        if (key == null) {
            key = new Key(refer, "");
            key.setDummy(true);
        }
        keyRef = new KeyRef(keyRefName, "", key);
        visitChildren(node);

        // For every keyref a selector has to be present. If not an exception is thrown.
        if (keyRef.getSelector().equals("")) {
            throw new MissingSelectorException(keyRef);
        }
        // A keyref object has to contain at least one field
        if (keyRef.getFields().isEmpty()) {
            throw new MissingFieldException(keyRef);
        }
//        if (!schema.addConstraintName(keyRefName)) {
//            throw new DuplicateConstraintException(keyRefName);
//        }
        if (node.getAttributes().getNamedItem("id") != null) {
            if (node.getAttributes().getNamedItem("id").getNodeValue().equals("")) {
                throw new EmptyIdException("KeyRef");
            }
            keyRef.setId(node.getAttributes().getNamedItem("id").getNodeValue());
        }
        return keyRef;
    }
}
