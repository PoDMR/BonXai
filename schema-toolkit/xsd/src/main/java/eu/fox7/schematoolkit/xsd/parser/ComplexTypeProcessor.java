package eu.fox7.schematoolkit.xsd.parser;

import eu.fox7.schematoolkit.common.*;
import eu.fox7.schematoolkit.xsd.om.*;
import eu.fox7.schematoolkit.xsd.parser.exceptions.XSDParseException;
import eu.fox7.schematoolkit.xsd.parser.exceptions.attribute.*;
import eu.fox7.schematoolkit.xsd.parser.exceptions.content.*;
import eu.fox7.schematoolkit.xsd.parser.exceptions.type.*;

import java.util.HashSet;
import org.w3c.dom.*;

/*******************************************************************************
 * Processor for the complextype-tag in XSD
 *******************************************************************************
 * @author Lars Schmidt, Dominik Wolff
 */
public class ComplexTypeProcessor extends Processor {

    // Created complexType
    private ComplexType complexType;
    // Name of the new complexType
    private QualifiedName complexTypeName = null;
    // Annotation of the complexType
    private Annotation annotation;
    // True if an anyAttribute was already in the content
    private boolean alreadyAnAttributeAdded = false;
    
    private boolean mixed = false;

    /**
     * This is the constructor of the class ComplexTypeProcessor.
     * @param schema
     */
    public ComplexTypeProcessor(XSDSchema schema) {
        super(schema);
    }

    /**
     * The method processNode handles all attributes and properties of
     * the current complextype-tag. The given properties are checked against the
     * allowed values defined in the XSD primer.
     *
     * <complexType
     * abstract = boolean : false
     * block = (#all | List of (extension | restriction))
     * final = (#all | List of (extension | restriction))
     * id = ID
     * mixed = boolean : false
     * name = NCName
     * {any attributes with non-schema namespace . . .}>
     * Content: (annotation?, (simpleContent | complexContent | ((group | all |
     *          choice | sequence)?, ((attribute | attributeGroup)*,
     *          anyAttribute?))))
     * </complexType>
     *
     * The content is managed by the processChild-method.
     *
     * @param node
     * @return ComplexContentType
     * @throws Exception
     */
    @Override
    protected QualifiedName processNode(Node node) throws XSDParseException {
        this.complexTypeName = this.getName(node);

        // Generate XSD complexType object
        if (node.getAttributes().getNamedItem("name") != null && !node.getAttributes().getNamedItem("name").getNodeValue().equals("")) {
            this.complexType = new ComplexType(this.complexTypeName, null);
        } else {
            // Type is an anonymous type
        	this.complexTypeName = this.getUniqueName();
            this.complexType = new ComplexType(this.complexTypeName, null, true);
        }

        // Handle the "mixed"-property
        if (node.getAttributes().getNamedItem("mixed") != null && node.getAttributes().getNamedItem("mixed").getNodeValue().equals("true")) {
            this.mixed = true;
        } else if (node.getAttributes().getNamedItem("mixed") != null && !node.getAttributes().getNamedItem("mixed").getNodeValue().equals("false")) {
            // There are only the boolean values "false" and "true" allowed for the mixed-property
            throw new InvalidMixedValueException(this.complexType);
        }
        // If there is an ID, we can add it to the corresponding object.
        if (node.getAttributes().getNamedItem("id") != null) {
            if (node.getAttributes().getNamedItem("id").getNodeValue().equals("")) {
                throw new EmptyIdException("ComplexType: " + complexType.getName());
            }
            this.complexType.setId(node.getAttributes().getNamedItem("id").getNodeValue());
        }

        // Call the visitChildren method to handle children and find necessary details for the current complexType
        this.visitChildren(node);
        complexType.setAnnotation(annotation);

        // Handle properties
        // Final-modifiers: This is an option for the inheritance of this complexType.
        // It is possible to make the corresponding inheritance final for a
        // restriction or an extension or both of them.)

        if (node.getAttributes().getNamedItem("final") != null) {
            String finalModifierString = node.getAttributes().getNamedItem("final").getNodeValue();
            if (!finalModifierString.equals("")) {
                String[] finalModifierArray = finalModifierString.split(" ");
                for (String currentModifier : finalModifierArray) {
                    if (currentModifier.toUpperCase().equals(ComplexTypeInheritanceModifier.Restriction.name().toUpperCase())) {
                        this.complexType.addFinalModifier(ComplexTypeInheritanceModifier.Restriction);
                    } else {
                        if (currentModifier.toUpperCase().equals(ComplexTypeInheritanceModifier.Extension.name().toUpperCase())) {
                            this.complexType.addFinalModifier(ComplexTypeInheritanceModifier.Extension);
                        } else {
                            if (currentModifier.toUpperCase().equals("#all".toUpperCase())) {
                                this.complexType.addFinalModifier(ComplexTypeInheritanceModifier.Restriction);
                                this.complexType.addFinalModifier(ComplexTypeInheritanceModifier.Extension);
                            } else {
                                throw new InvalidFinalValueException(complexType.getName());
                            }
                        }
                    }
                }
            } else {
                this.complexType.setFinalModifiers(new HashSet<ComplexTypeInheritanceModifier>());
            }
        }
        // block-modifiers: This is an option for the inheritance of this complexType.
        // It is possible to block the corresponding inheritance for a
        // restriction or an extension or both of them.)

        if (node.getAttributes().getNamedItem("block") != null) {
            String blockModifierString = node.getAttributes().getNamedItem("block").getNodeValue();
            if (!blockModifierString.equals("")) {
                String[] blockModifierArray = blockModifierString.split(" ");
                for (String currentModifier : blockModifierArray) {
                    if (currentModifier.toUpperCase().equals(ComplexTypeInheritanceModifier.Restriction.name().toUpperCase())) {
                        this.complexType.addBlockModifier(ComplexTypeInheritanceModifier.Restriction);
                    } else {
                        if (currentModifier.toUpperCase().equals(ComplexTypeInheritanceModifier.Extension.name().toUpperCase())) {
                            this.complexType.addBlockModifier(ComplexTypeInheritanceModifier.Extension);
                        } else {
                            if (currentModifier.toUpperCase().equals("#all".toUpperCase())) {
                                this.complexType.addBlockModifier(ComplexTypeInheritanceModifier.Restriction);
                                this.complexType.addBlockModifier(ComplexTypeInheritanceModifier.Extension);
                            } else {
                                throw new InvalidBlockValueException(complexType.getName());
                            }
                        }
                    }
                }
            } else {
                this.complexType.setBlockModifiers(new HashSet<ComplexTypeInheritanceModifier>());
            }
        }
        // Is the complexType declared as an abstract-type?
        String abstractString;
        if (node.getAttributes().getNamedItem("abstract") != null) {
            abstractString = node.getAttributes().getNamedItem("abstract").getNodeValue();
            if (abstractString.toLowerCase().equals("true")) {
                complexType.setAbstract(true);
            } else if (!abstractString.toLowerCase().equals("false")) {
                throw new InvalidAbstractException(this.complexType.getName());
            }
        }
        schema.addType(complexType);
        return this.complexType.getName();
    }

    /**
     * This method manages the possible child-content af the current complextype
     *
     * Content: (annotation?, (simpleContent | complexContent | ((group | all |
     *          choice | sequence)?, ((attribute | attributeGroup)*,
     *          anyAttribute?))))
     *
     * @param childNode
     * @throws Exception
     */
    @Override
    protected void processChild(Node childNode) throws XSDParseException {
        String nodeName = childNode.getNodeName();
        if (nodeName.contains(":")) {
            nodeName = nodeName.split(":")[1];
        }
        Particle particle;
        Content complexTypeContent;
        AttributeParticle attributeParticle;

        if (childNode != null && !"#text".equals(nodeName) && !"#comment".equals(nodeName) && !"#document".equals(nodeName)) {
            switch (CASE.valueOf(nodeName.toUpperCase())) {
                case SIMPLECONTENT:
                    if (getDebug()) {
                        System.out.println("simpleContent");
                    }
                    SimpleContentProcessor simpleContentProcessor = new SimpleContentProcessor(schema);
                    complexTypeContent = simpleContentProcessor.processNode(childNode);
                    // The content can only be set once for a complexType!
                    if (this.complexType.getContent() != null) {
                        throw new ComplexTypeMultipleContentException(this.complexType.getName());
                    }
                    this.complexType.setContent(complexTypeContent);
                    break;
                case COMPLEXCONTENT:
                    if (getDebug()) {
                        System.out.println("complexContent");
                    }
                    ComplexContentProcessor complexContentProcessor = new ComplexContentProcessor(schema);
                    complexTypeContent = complexContentProcessor.processNode(childNode);
                    // The content can only be set once for a complexType!
                    if (this.complexType.getContent() != null) {
                        throw new ComplexTypeMultipleContentException(this.complexType.getName());
                    }
                    this.complexType.setContent(complexTypeContent);
                    break;
                case GROUP:
                    if (getDebug()) {
                        System.out.println("group");
                    }
                    // Attributes have to be the last elements in the content model
                    if (alreadyAnAttributeAdded) {
                        throw new AttributeIsNotLastInContentModelException(this.complexType.getName().getFullyQualifiedName());
                    }
                    GroupProcessor groupProcessor = new GroupProcessor(schema);
                    Object object = groupProcessor.processNode(childNode);
                    if (object instanceof Particle) {
                        particle = (Particle) object;
                        complexTypeContent = new ComplexContentType(particle, this.mixed);
                        // The content can only be set once for a complexType!
                        if (this.complexType.getContent() != null) {
                            throw new ComplexTypeMultipleContentException(this.complexType.getName());
                        }
                        this.complexType.setContent(complexTypeContent);
                    } else {
                        throw new IllegalObjectReturnedException(object.getClass().getName(), "complexType");
                    }
                    break;

                case ALL:
                    if (getDebug()) {
                        System.out.println("all");
                    }
                    // Attributes have to be the last elements in the content model
                    if (alreadyAnAttributeAdded) {
                        throw new AttributeIsNotLastInContentModelException(this.complexType.getName().getFullyQualifiedName());
                    }
                    // The content can only be set once for a complexType!
                    if (this.complexType.getContent() != null) {
                        throw new ComplexTypeMultipleContentException(this.complexType.getName());
                    }
                    AllProcessor allProcessor = new AllProcessor(schema);
                    particle = allProcessor.processNode(childNode);
                    complexTypeContent = new ComplexContentType(particle, this.mixed);
                    this.complexType.setContent(complexTypeContent);
                    break;
                case CHOICE:
                    if (getDebug()) {
                        System.out.println("choice");
                    }
                    // Attributes have to be the last elements in the content model
                    if (alreadyAnAttributeAdded) {
                        throw new AttributeIsNotLastInContentModelException(this.complexType.getName().getFullyQualifiedName());
                    }
                    // The content can only be set once for a complexType!
                    if (this.complexType.getContent() != null) {
                        throw new ComplexTypeMultipleContentException(this.complexType.getName());
                    }
                    ChoiceProcessor choiceProcessor = new ChoiceProcessor(schema);
                    particle = choiceProcessor.processNode(childNode);
                    complexTypeContent = new ComplexContentType(particle, this.mixed);
                    this.complexType.setContent(complexTypeContent);
                    break;
                case SEQUENCE:
                    if (getDebug()) {
                        System.out.println("sequence");
                    }
                    // Attributes have to be the last elements in the content model
                    if (alreadyAnAttributeAdded) {
                        throw new AttributeIsNotLastInContentModelException(this.complexType.getName().getFullyQualifiedName());
                    }
                    // The content can only be set once for a complexType!
                    if (this.complexType.getContent() != null) {
                        throw new ComplexTypeMultipleContentException(this.complexType.getName());
                    }
                    SequenceProcessor sequenceProcessor = new SequenceProcessor(schema);
                    particle = sequenceProcessor.processNode(childNode);
                    complexTypeContent = new ComplexContentType(particle, this.mixed);
                    this.complexType.setContent(complexTypeContent);
                    break;
                case ATTRIBUTE:
                    if (getDebug()) {
                        System.out.println("attribute");
                    }
                    // An AnyAttribute is only allowed to be the last element in an attributeGroup.
                    // If a second anyAttribute is found after an anyAttribute, this is a failure.
                    if (!complexType.getAttributes().isEmpty() && complexType.getAttributes().getLast() instanceof AnyAttribute) {
                        throw new AnyAttributeIsNotLastException(complexType.getName().getFullyQualifiedName());
                    }
                    AttributeProcessor attributeProcessor = new AttributeProcessor(schema);
                    attributeParticle = attributeProcessor.processNode(childNode);
                    this.complexType.addAttribute(attributeParticle);
                    this.alreadyAnAttributeAdded = true;
                    break;
                case ATTRIBUTEGROUP:
                    if (getDebug()) {
                        System.out.println("attributeGroup");
                    }
                    // An AnyAttribute is only allowed to be the last element in an attributeGroup.
                    // If a second anyAttribute is found after an anyAttribute, this is a failure.
                    if (!complexType.getAttributes().isEmpty() && complexType.getAttributes().getLast() instanceof AnyAttribute) {
                        throw new AnyAttributeIsNotLastException(complexType.getName().getFullyQualifiedName());
                    }
                    AttributeGroupReferenceProcessor attributeGroupReferenceProcessor = new AttributeGroupReferenceProcessor(schema);
                    attributeParticle = attributeGroupReferenceProcessor.processNode(childNode);
                    this.complexType.addAttribute(attributeParticle);
                    this.alreadyAnAttributeAdded = true;
                    break;
                case ANYATTRIBUTE:
                    if (getDebug()) {
                        System.out.println("anyAttribute");
                    }
                    // An AnyAttribute is only allowed to be the last element in an attributeGroup.
                    // If a second anyAttribute is found after an anyAttribute, this is a failure.
                    if (!complexType.getAttributes().isEmpty() && complexType.getAttributes().getLast() instanceof AnyAttribute) {
                        throw new AnyAttributeIsNotLastException(complexType.getName().getFullyQualifiedName());
                    }
                    AnyAttributeProcessor anyAttributeProcessor = new AnyAttributeProcessor(schema);
                    AnyAttribute anyAttribute = anyAttributeProcessor.processNode(childNode);
                    this.complexType.addAttribute(anyAttribute);
                    this.alreadyAnAttributeAdded = true;
                    break;
                case ANNOTATION:
                    if (annotation == null) {
                        AnnotationProcessor annotationProcessor = new AnnotationProcessor(schema);
                        annotation = annotationProcessor.processNode(childNode);
                    } else {
                        throw new MultipleAnnotationException("complexType");
                    }
                    break;
                default:
                    throw new UnsupportedContentException(nodeName, "complexType");
            }
        }
    }
}
