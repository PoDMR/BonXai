package eu.fox7.bonxai.xsd.parser;

import eu.fox7.bonxai.common.*;
import eu.fox7.bonxai.xsd.*;
import eu.fox7.bonxai.xsd.parser.exceptions.attribute.*;
import eu.fox7.bonxai.xsd.parser.exceptions.content.*;

import org.w3c.dom.*;

/*******************************************************************************
 * Processor for the handling of an anyAttribute-tag in XSD
 *******************************************************************************
 * @author Lars Schmidt, Dominik Wolff
 */
class AnyAttributeProcessor extends Processor {

    // Value of the processcontent attribute
    private ProcessContentsInstruction processContentsInstruction = ProcessContentsInstruction.Strict;

    // Value of namespace and id attributes
    private String namespace, idString;

    // Annotation of this anyAttribute
    private Annotation annotation;

    /**
     * This is the constructor of the class AnyAttributeProcessor.
     * @param schema
     */
    public AnyAttributeProcessor(XSDSchema schema) {
        super(schema);
    }

    /**
     * The method processNode handles all attributes and properties of
     * the current "anyAttribute"-tag. The given properties are checked against the allowed
     * values defined in the XSD primer.
     *
     * <anyAttribute
     * id = ID
     * namespace = ((##any | ##other) | List of (anyURI | (##targetNamespace |
     *              ##local)) )  : ##any
     * processContents = (lax | skip | strict) : strict
     * {any attributes with non-schema namespace . . .}>
     * Content: (annotation?)
     * </anyAttribute>
     *
     * The content is managed by the processChild-method below.
     *
     * @param node
     * @return AnyAttribute
     * @throws Exception
     */
    @Override
    protected AnyAttribute processNode(Node node) throws Exception {
        // List of Attributes
        NamedNodeMap attributes = node.getAttributes();
        if (attributes != null) {
            // ID Attribute of AnyAttribute
            if (attributes.getNamedItem("id") != null) {
                if (node.getAttributes().getNamedItem("id").getNodeValue().equals("")) {
                    throw new EmptyIdException("anyAttribute");
                }
                idString = ((Attr) attributes.getNamedItem("id")).getValue();
            }
            // processContents Attribute of AnyAttribute
            if (attributes.getNamedItem("processContents") != null) {
                if (((Attr) attributes.getNamedItem("processContents")).getValue().equals("strict")) {
                    processContentsInstruction = ProcessContentsInstruction.Strict;
                } else if (((Attr) attributes.getNamedItem("processContents")).getValue().equals("lax")) {
                    processContentsInstruction = ProcessContentsInstruction.Lax;
                } else if (((Attr) attributes.getNamedItem("processContents")).getValue().equals("skip")) {
                    processContentsInstruction = ProcessContentsInstruction.Skip;
                } else {
                    throw new InvalidProcessContentsValueException("anyAttribute");
                }
            }
            // namespace Attribute of AnyAttribute
            if (attributes.getNamedItem("namespace") != null) {
                String namespaceValue = ((Attr) attributes.getNamedItem("namespace")).getValue();
                if (namespaceValue.equals("##any") || namespaceValue.equals("##other") || namespaceValue.equals("##local") || namespaceValue.equals("##targetNamespace")) {
                    namespace = namespaceValue;
                } else {
                    String[] namespaceValueArray = namespaceValue.split(" ");
                    for (String currentNamespace : namespaceValueArray) {
                        if (!currentNamespace.equals("##local") && !currentNamespace.equals("##targetNamespace") && !isAnyUri(currentNamespace)) {
                            throw new InvalidNamespaceValueException("anyAttribute");
                        }
                    }
                    namespace = namespaceValue;
                }
            }
        }
        
        visitChildren(node);
        AnyAttribute anyAttribute = new AnyAttribute(processContentsInstruction, namespace);
        if (idString != null) {
            anyAttribute.setId(idString);
        }
        anyAttribute.setAnnotation(annotation);
        return anyAttribute;
    }

    /**
     * This method manages the possible content af the current "anyAttribute"-tag.
     *
     * Content: (annotation?)
     *
     * @param childNode
     * @throws Exception
     */
    @Override
    protected void processChild(Node childNode) throws Exception {

        // Tests if the node name is a local name and filters nodes with names
        // #text, #comment and #document who are not in the enum
        String nodeName = childNode.getNodeName();
        if (nodeName.contains(":")) {
            nodeName = nodeName.split(":")[1];
        }
        if (childNode != null && !"#text".equals(nodeName) && !"#comment".equals(nodeName) && !"#document".equals(nodeName)) {
            switch (CASE.valueOf(nodeName.toUpperCase())) {
                case ANNOTATION:
                    if (annotation == null) {
                        AnnotationProcessor annotationProcessor = new AnnotationProcessor(schema);
                        annotation = annotationProcessor.processNode(childNode);
                    } else {
                        throw new MultipleAnnotationException("anyAttribute");
                    }
                    break;
                default:
                    throw new UnsupportedContentException(nodeName, "anyattribute");
            }
        }
    }
}
