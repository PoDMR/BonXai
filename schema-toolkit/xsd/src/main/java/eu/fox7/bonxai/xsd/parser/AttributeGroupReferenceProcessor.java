package eu.fox7.bonxai.xsd.parser;

import eu.fox7.bonxai.common.*;
import eu.fox7.bonxai.xsd.*;
import eu.fox7.bonxai.xsd.parser.exceptions.XSDParseException;
import eu.fox7.bonxai.xsd.parser.exceptions.attribute.*;
import eu.fox7.bonxai.xsd.parser.exceptions.content.*;

import org.w3c.dom.*;

/*******************************************************************************
 * Processor for the handling of an attributeGroup-tag in XSD
 *******************************************************************************
 * @author Lars Schmidt, Dominik Wolff
 */
public class AttributeGroupReferenceProcessor extends Processor {

    // Created AttributeGroupReference
    private AttributeGroupReference attributeGroupReference;
    // Id of this AttributeGroup
    private String idString;

    /**
     * This is the constructor of the class AttributeGroupProcessor.
     * @param schema
     */
    public AttributeGroupReferenceProcessor(XSDSchema schema) {
        super(schema);
    }

    /**
     * The method processNode handles all attributes and properties of
     * the current attributeGroup-tag. The given properties are checked against the allowed
     * values defined in the XSD primer.
     *
     * <attributeGroup
     * id = ID
     * name = NCName
     * ref = QName
     * {any attributes with non-schema namespace . . .}>
     * Content: (annotation?, ((attribute | attributeGroup)*, anyAttribute?))
     * </attributeGroup>
     *
     * The content is managed by the processChild-method below.
     *
     * @param node
     * @return Object
     * @throws Exception
     */
    @Override
    protected AttributeGroupReference processNode(Node node) throws Exception {
        visitChildren(node);
        // List of Attributes
        NamedNodeMap attributes = node.getAttributes();

        if (attributes != null && attributes.getNamedItem("ref") != null) {
            String refValue = ((Attr) attributes.getNamedItem("ref")).getValue();
            if (!isQName(refValue)) {
                throw new InvalidQNameException(refValue, "attributeGroup");
            }

            if (attributes.getNamedItem("name") != null) {
                throw new ExclusiveAttributesException("name and ref", getName(node));
            }
            // ref Attribute of AttributeGroup
            QualifiedName refName = getName(refValue);

            return new AttributeGroupReference(refName);
        } else 
        	throw new XSDParseException("AttributeGroupReference without a reference");
    }

    /**
     * This method manages the possible content af the current attributeGroup-tag.
     *
     * Content: (annotation?, ((attribute | attributeGroup)*, anyAttribute?))
     *
     * @param childNode
     * @throws Exception
     */
    @Override
    protected void processChild(Node childNode) throws Exception {
        String nodeName = childNode.getNodeName();
        if (nodeName.contains(":")) {
            nodeName = nodeName.split(":")[1];
        }
        if (childNode != null && !"#text".equals(nodeName) && !"#comment".equals(nodeName) && !"#document".equals(nodeName)) {
            switch (CASE.valueOf(nodeName.toUpperCase())) {
                case ANNOTATION:
                    // There can only be one annotation in an attributeGroup
                    if (attributeGroupReference.getAnnotation() == null) {
                        AnnotationProcessor annotationProcessor = new AnnotationProcessor(schema);
                        attributeGroupReference.setAnnotation(annotationProcessor.processNode(childNode));
                    } else {
                        throw new MultipleAnnotationException("attributeGroupReference");
                    }
                    break;
                default:
                    throw new UnsupportedContentException(nodeName, "attributeGroup");
            }
        }
    }
}
