package eu.fox7.schematoolkit.xsd.parser;

import eu.fox7.schematoolkit.common.*;
import eu.fox7.schematoolkit.xsd.om.*;
import eu.fox7.schematoolkit.xsd.parser.exceptions.attribute.*;
import eu.fox7.schematoolkit.xsd.parser.exceptions.content.*;
import eu.fox7.schematoolkit.xsd.parser.exceptions.type.*;

import org.w3c.dom.*;

/*******************************************************************************
 * The AttributeProcessor processes an attribute labeled node into an attribute
 * object or attributeRef object of the XSD object model.
 *******************************************************************************
 * @author Lars Schmidt, Dominik Wolff
 */
public class AttributeProcessor extends Processor {

    // Name of the referenced SimpleType
    private QualifiedName simpleTypeName;

    // Strings for default, fixed , id  attributes of attribute
    private String defaultString, fixedString, idString;
    
    private QualifiedName attributeName;

    // Variable for the form attribute
    private XSDSchema.Qualification form;

    // Variable for the use attribute
    private AttributeUse attributeUse;

    // The annotation of the attribute element
    private Annotation annotation;

    // Attributes of the attribute node
    private NamedNodeMap attributes;

    /**
     * Constructor of the AttributeProcessor, which receives only the schema.
     * @param schema    XSDSchema processed by this processor
     */
    public AttributeProcessor(XSDSchema schema) {
        super(schema);
    }

    /**
     * Creates an attribute corresponding to the attribute node in the dom tree.
     * @param node      Node labeled with attribute in the dom tree
     * @return Either an Attribute or an AttributeRef
     * @throws java.lang.Exception
     */
    @Override
    protected AttributeParticle processNode(Node node) throws Exception {
        if (!isNCName(getLocalName(node))) {
            throw new InvalidNCNameException(getLocalName(node), "attribute");
        }
        attributeName = getName(node);       
        attributes = node.getAttributes();
        visitChildren(node);
        if (attributes != null) {

            // id, form and use properties are all optional
            if (attributes.getNamedItem("id") != null) {
                if (attributes.getNamedItem("id").getNodeValue().equals("")) {
                    throw new EmptyIdException("Attribute: " + attributeName);
                }
                idString = ((Attr) attributes.getNamedItem("id")).getValue();
            }
            if (attributes.getNamedItem("form") != null) {
                // Global-Elements must not have the form definition!
                if (node.getParentNode().getNodeType() == 1 && node.getParentNode().getNodeName().endsWith("schema")) {
                    throw new InvalidFormValueLocationException(attributeName);
                }
                String formValue = ((Attr) attributes.getNamedItem("form")).getValue();
                if (formValue.equals("qualified")) {
                    form = XSDSchema.Qualification.qualified;
                } else {
                    if (formValue.equals("unqualified")) {
                        form = XSDSchema.Qualification.unqualified;
                    } else {
                        throw new InvalidFormValueException(attributeName);
                    }
                }
            }
            if (attributes.getNamedItem("use") != null) {
                String useValue = ((Attr) attributes.getNamedItem("use")).getValue();
                if (useValue.equals("optional")) {
                    attributeUse = AttributeUse.Optional;
                } else {
                    if (useValue.equals("prohibited")) {
                        attributeUse = AttributeUse.Prohibited;
                    } else {
                        if (useValue.equals("required")) {
                            attributeUse = AttributeUse.Required;
                        } else {
                            throw new InvalidUseValueException(attributeName);
                        }
                    }
                }
            }
            // Either fixed or default are allowed to appear in an element not both.
            if (attributes.getNamedItem("default") != null && attributes.getNamedItem("fixed") != null) {
                throw new ExclusiveAttributesException("fixed and default", attributeName);
            }
            if (attributes.getNamedItem("fixed") != null && attributes.getNamedItem("default") == null) {
                fixedString = ((Attr) attributes.getNamedItem("fixed")).getValue().trim();
            }
            if (attributes.getNamedItem("default") != null && attributes.getNamedItem("fixed") == null) {
                defaultString = ((Attr) attributes.getNamedItem("default")).getValue().trim();
            }

            if (attributes.getNamedItem("type") != null) {
                if (attributes.getNamedItem("ref") != null) {
                    throw new ExclusiveAttributesException("type and ref", attributeName);
                }
                if (attributes.getNamedItem("name") == null) {
                    throw new MissingNameException();
                }
                if (simpleTypeName != null) {
                        throw new MultipleTypesException("attribute", attributeName.getQualifiedName());
                }
                if (!isQName(((Attr) attributes.getNamedItem("type")).getValue())) {
                    throw new InvalidQNameException(((Attr) attributes.getNamedItem("type")).getValue(), "type");
                }
                simpleTypeName = getName(((Attr) attributes.getNamedItem("type")).getValue());
            }
            if (attributes.getNamedItem("ref") != null) {
                if (attributes.getNamedItem("name") != null) {
                    throw new ExclusiveAttributesException("name and ref", attributeName);
                }
                if (attributes.getNamedItem("type") != null) {
                    throw new ExclusiveAttributesException("type and ref", attributeName);
                }
                if (attributes.getNamedItem("form") != null) {
                    throw new ExclusiveAttributesException("form and ref", attributeName);
                }
                if (!isQName(((Attr) attributes.getNamedItem("ref")).getValue())) {
                    throw new InvalidQNameException(((Attr) attributes.getNamedItem("ref")).getValue(), "ref");
                }
                QualifiedName refName = getName(((Attr) attributes.getNamedItem("ref")).getValue());


                AttributeRef attributeRef = new AttributeRef(refName, defaultString, fixedString, attributeUse, annotation);
                if (attributes.getNamedItem("id") != null) {
                    if (node.getAttributes().getNamedItem("id").getNodeValue().equals("")) {
                        throw new EmptyIdException("Attribute: " + attributeName);
                    }
                    attributeRef.setId(((Attr) attributes.getNamedItem("id")).getValue());
                }
                return attributeRef;
            }
        }
        Attribute attribute =  new Attribute(getName(node), simpleTypeName, defaultString, fixedString, attributeUse, form, annotation);

        if (idString != null) {
            attribute.setId(idString);
        }

        return attribute;
    }

    /**
     * Visits a child of the attribute node and processes it according to its name
     * @param childNode     Node in the dom tree below the attribute Node
     * @throws java.lang.Exception
     */
    @Override
    protected void processChild(Node childNode) throws Exception {
        
        // Tests if the node name is a local name and filters nodes with names #text, #comment and #document who are not in the enum
        String nodeName = childNode.getNodeName();
        if (nodeName.contains(":")) {
            nodeName = nodeName.split(":")[1];
        }
        if (childNode != null && !"#text".equals(nodeName) && !"#comment".equals(nodeName) && !"#document".equals(nodeName)) {
            switch (CASE.valueOf(nodeName.toUpperCase())) {
                case SIMPLETYPE:
                    if (getDebug()) {
                        System.out.println("simpleType");
                    }
                    if (attributes.getNamedItem("ref") != null) {
                        throw new ExclusiveContentException("simpleType and ref", attributeName);
                    }
                    if (simpleTypeName != null) {
                        throw new MultipleTypesException("attribute", attributeName.getQualifiedName());
                    } else {
                        SimpleTypeProcessor simpleTypeProcessor = new SimpleTypeProcessor(schema);
                        simpleTypeName = simpleTypeProcessor.processNode(childNode);
                    }
                    break;
                case ANNOTATION:
                    if (annotation == null) {
                        AnnotationProcessor annotationProcessor = new AnnotationProcessor(schema);
                        annotation = annotationProcessor.processNode(childNode);
                    } else {
                        throw new MultipleAnnotationException("attribute");
                    }
                    break;
                default:
                    throw new UnsupportedContentException(nodeName, "attribute");
            }
        }
    }
}
