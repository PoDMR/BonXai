package eu.fox7.bonxai.xsd.parser;

import eu.fox7.bonxai.common.*;
import eu.fox7.bonxai.xsd.*;
import eu.fox7.bonxai.xsd.parser.exceptions.attribute.*;
import eu.fox7.bonxai.xsd.parser.exceptions.content.*;

import org.w3c.dom.*;

/*******************************************************************************
 * Processor for the handling of an attributeGroup-tag in XSD
 *******************************************************************************
 * @author Lars Schmidt, Dominik Wolff
 */
public class AttributeGroupProcessor extends Processor {

    // Created AttributeGroup
    private AttributeGroup attributeGroup;
    // Id of this AttributeGroup
    private String idString;

    /**
     * This is the constructor of the class AttributeGroupProcessor.
     * @param schema
     */
    public AttributeGroupProcessor(XSDSchema schema) {
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
    protected AttributeParticle processNode(Node node) throws Exception {
        String attributeGroupName = getName(node);
        attributeGroup = new AttributeGroup(attributeGroupName);
        visitChildren(node);
        // List of Attributes
        NamedNodeMap attributes = node.getAttributes();

        if (attributes != null && attributes.getNamedItem("ref") != null) {
            String refValue = ((Attr) attributes.getNamedItem("ref")).getValue();
            if (!isQName(refValue)) {
                throw new InvalidQNameException(refValue, "attributeGroup");
            }

            if (attributes.getNamedItem("name") != null) {
                throw new ExclusiveAttributesException("name and ref", attributeGroupName);
            }
            // ref Attribute of AttributeGroup
            if (attributes.getNamedItem("id") != null) {
                if (attributes.getNamedItem("id").getNodeValue().equals("")) {
                    throw new EmptyIdException("attributeGroup: " + attributeGroup.getName());
                }
                idString = ((Attr) attributes.getNamedItem("id")).getValue();
            }

            String refName = getName(refValue);

            AttributeGroupRef attributeGroupRef = null;
            if (schema.getAttributeGroupSymbolTable().hasReference(refName)) {
                attributeGroupRef = new AttributeGroupRef(schema.getAttributeGroupSymbolTable().getReference(refName));
            } else {
                AttributeGroup attributeGroupRefElement = new AttributeGroup(refName);
                attributeGroupRefElement.setDummy(true);
                attributeGroupRef = new AttributeGroupRef(schema.getAttributeGroupSymbolTable().updateOrCreateReference(refName, attributeGroupRefElement));
            }
            attributeGroupRef.setId(idString);
            return attributeGroupRef;
        }

        if (!isNCName(getLocalName(node))) {
            throw new InvalidNCNameException(getLocalName(node), "attributeGroup");
        }

        // The following part sets the ID of a group.
        // This is necessary at this point, because the setting of the ID above is only for attributeGroupRefs.
        if (node.getAttributes().getNamedItem("id") != null) {
            if (node.getAttributes().getNamedItem("id").getNodeValue().equals("")) {
                throw new EmptyIdException("attributeGroup: " + attributeGroup.getName());
            }
            attributeGroup.setId(node.getAttributes().getNamedItem("id").getNodeValue());
        }
        schema.getAttributeGroupSymbolTable().updateOrCreateReference(attributeGroup.getName(), attributeGroup);
        attributeGroup.setDummy(false);
        return attributeGroup;
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
                case ATTRIBUTE:
                    if (getDebug()) {
                        System.out.println("attribute");
                    }
                    // An AnyAttribute is only allowed to be the last element in an attributeGroup.
                    // If an attribute is found after an anyAttribute, this is a failure.
                    if (!attributeGroup.getAttributeParticles().isEmpty() && attributeGroup.getAttributeParticles().getLast() instanceof AnyAttribute) {
                        throw new AnyAttributeIsNotLastException("attribute to " + attributeGroup.getName());
                    }
                    AttributeProcessor attributeProcessor = new AttributeProcessor(schema);
                    attributeGroup.addAttributeParticle(attributeProcessor.processNode(childNode));
                    break;
                case ANYATTRIBUTE:
                    if (getDebug()) {
                        System.out.println("anyAttribute");
                    }
                    // An AnyAttribute is only allowed to be the last element in an attributeGroup.
                    // If a second anyAttribute is found after an anyAttribute, this is a failure.
                    if (!attributeGroup.getAttributeParticles().isEmpty() && attributeGroup.getAttributeParticles().getLast() instanceof AnyAttribute) {
                        throw new AnyAttributeIsNotLastException("anyAttribute to " + attributeGroup.getName());
                    }
                    AnyAttributeProcessor anyAttributeProcessor = new AnyAttributeProcessor(schema);
                    attributeGroup.addAttributeParticle(anyAttributeProcessor.processNode(childNode));
                    break;
                case ATTRIBUTEGROUP:
                    if (getDebug()) {
                        System.out.println("attributeGroup");
                    }
                    // An AnyAttribute is only allowed to be the last element in an attributeGroup.
                    // If an attributeGroup is found after an anyAttribute, this is a failure.
                    if (!attributeGroup.getAttributeParticles().isEmpty() && attributeGroup.getAttributeParticles().getLast() instanceof AnyAttribute) {
                        throw new AnyAttributeIsNotLastException("attributeGroup to " + attributeGroup.getName());
                    }
                    AttributeGroupProcessor attributeGroupProcessor = new AttributeGroupProcessor(schema);
                    attributeGroup.addAttributeParticle(attributeGroupProcessor.processNode(childNode));
                    break;
                case ANNOTATION:
                    // There can only be one annotation in an attributeGroup
                    if (attributeGroup.getAnnotation() == null) {
                        AnnotationProcessor annotationProcessor = new AnnotationProcessor(schema);
                        attributeGroup.setAnnotation(annotationProcessor.processNode(childNode));
                    } else {
                        throw new MultipleAnnotationException("attributeGroup: " + attributeGroup.getName());
                    }
                    break;
                default:
                    throw new UnsupportedContentException(nodeName, "attributeGroup");
            }
        }
    }
}
