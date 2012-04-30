package eu.fox7.schematoolkit.xsd.parser;

import eu.fox7.schematoolkit.common.*;
import eu.fox7.schematoolkit.xsd.om.*;
import eu.fox7.schematoolkit.xsd.om.Element;
import eu.fox7.schematoolkit.xsd.parser.exceptions.XSDParseException;
import eu.fox7.schematoolkit.xsd.parser.exceptions.attribute.*;
import eu.fox7.schematoolkit.xsd.parser.exceptions.attribute.countingpattern.*;
import eu.fox7.schematoolkit.xsd.parser.exceptions.content.*;
import eu.fox7.schematoolkit.xsd.parser.exceptions.type.*;

import java.util.HashSet;
import org.w3c.dom.*;

/*******************************************************************************
 * The ElementProcessor is given a node of the dom tree representing an element
 * in the XSD document and transforms this into an element object of the XSD
 * object model.
 *******************************************************************************
 * @author Lars Schmidt, Dominik Wolff
 */
public class ElementProcessor extends Processor {

    // New element object which is formed by this processor
    private Element element;
    // Attributes of the element node
    NamedNodeMap attributes;
    // Values for minOccurs and maxOccurs for given element
    private Integer minOccursValue = 1,  maxOccursValue = 1;

    /**
     * Constructor of the ElementProcessor, which receives only the schema.
     * @param schema    XSDSchema processed by this processor
     */
    public ElementProcessor(XSDSchema schema) {
        super(schema);
    }

    /**
     * Creates an element corresponding to the element node in the dom tree.
     * @param node      Node labeled with element in the dom tree
     * @return Either a CountingPattern containing the Element an element or an
     *      ElementRef is returned
     * @throws java.lang.Exception
     */
    @Override
    protected Particle processNode(Node node) throws XSDParseException {
        element = new Element(getName(node));

        // Check all element attributes
        attributes = node.getAttributes();
        visitChildren(node);
        if (attributes != null) {

            // id, nillable, abstract, form, final and block properties are all optional
            if (attributes.getNamedItem("id") != null) {
                if (attributes.getNamedItem("id").getNodeValue().equals("")) {
                    throw new EmptyIdException("Element: " + element.getName());
                }
                element.setId(((Attr) attributes.getNamedItem("id")).getValue());
            }
            if (attributes.getNamedItem("nillable") != null) {
                if (!((Attr) attributes.getNamedItem("nillable")).getValue().equals("true") && !((Attr) attributes.getNamedItem("nillable")).getValue().equals("false")) {
                    throw new InvalidNillableException(element.getName());
                }
                if (((Attr) attributes.getNamedItem("nillable")).getValue().equals("true")) {
                    element.setNillable();
                }
            }
            if (attributes.getNamedItem("abstract") != null) {
                if (!((Attr) attributes.getNamedItem("abstract")).getValue().equals("true") && !((Attr) attributes.getNamedItem("abstract")).getValue().equals("false")) {
                    throw new InvalidAbstractException(element.getName());
                }
                if (((Attr) attributes.getNamedItem("abstract")).getValue().equals("true")) {
                    element.setAbstract(true);
                }
            }
            if (attributes.getNamedItem("form") != null) {
                // Global-Elements must not have the form definition!
                if (node.getParentNode().getNodeType() == 1 && node.getParentNode().getNodeName().endsWith("schema")) {
                    throw new InvalidFormValueLocationException(element.getName());
                }
                String formValue = ((Attr) attributes.getNamedItem("form")).getValue();
                if (formValue.equals("qualified")) {
                    element.setForm(XSDSchema.Qualification.qualified);
                } else {
                    if (formValue.equals("unqualified")) {
                        element.setForm(XSDSchema.Qualification.unqualified);
                    } else {
                        throw new InvalidFormValueException(element.getName());
                    }
                }
            }
            if (node.getAttributes().getNamedItem("final") != null) {
                if (!(node.getParentNode().getNodeType() == 1 && node.getParentNode().getNodeName().endsWith("schema"))) {
                    throw new InvalidFinalLocationException(element.getName());
                }
                String finalModifierString = node.getAttributes().getNamedItem("final").getNodeValue();
                if (!finalModifierString.equals("")) {
                    String[] finalModifierArray = finalModifierString.split(" ");
                    for (String currentModifier : finalModifierArray) {
                        if (currentModifier.toUpperCase().equals(Element.Final.restriction.name().toUpperCase())) {
                            this.element.addFinalModifier(Element.Final.restriction);
                        } else {
                            if (currentModifier.toUpperCase().equals(Element.Final.extension.name().toUpperCase())) {
                                this.element.addFinalModifier(Element.Final.extension);
                            } else {
                                if (currentModifier.toUpperCase().equals("#all".toUpperCase())) {
                                    this.element.addFinalModifier(Element.Final.restriction);
                                    this.element.addFinalModifier(Element.Final.extension);
                                } else {
                                    throw new InvalidFinalValueException(element.getName());
                                }
                            }
                        }
                    }
                } else {
                    this.element.setFinalModifiers(new HashSet<Element.Final>());
                }
            }
            if (node.getAttributes().getNamedItem("block") != null) {
                String blockModifierString = node.getAttributes().getNamedItem("block").getNodeValue();
                if (!blockModifierString.equals("")) {
                    String[] blockModifierArray = blockModifierString.split(" ");
                    for (String currentModifier : blockModifierArray) {
                        if (currentModifier.toUpperCase().equals(Element.Block.restriction.name().toUpperCase())) {
                            this.element.addBlockModifier(Element.Block.restriction);
                        } else {
                            if (currentModifier.toUpperCase().equals(Element.Block.extension.name().toUpperCase())) {
                                this.element.addBlockModifier(Element.Block.extension);
                            } else {
                                if (currentModifier.toUpperCase().equals(Element.Block.substitution.name().toUpperCase())) {
                                    this.element.addBlockModifier(Element.Block.substitution);
                                } else {
                                    if (currentModifier.toUpperCase().equals("#all".toUpperCase())) {
                                        this.element.addBlockModifier(Element.Block.restriction);
                                        this.element.addBlockModifier(Element.Block.extension);
                                        this.element.addBlockModifier(Element.Block.substitution);
                                    } else {
                                        throw new InvalidBlockValueException(element.getName());
                                    }
                                }
                            }
                        }
                    }
                }else {
                    this.element.setBlockModifiers(new HashSet<Element.Block>());
                }
            }
            // Either fixed or default are allowed to appear in an element not both.
            if (attributes.getNamedItem("default") != null && attributes.getNamedItem("fixed") != null) {
                throw new ExclusiveAttributesException("fixed and default", element.getName());
            }
            if (attributes.getNamedItem("fixed") != null && attributes.getNamedItem("default") == null) {
                element.setFixed(((Attr) attributes.getNamedItem("fixed")).getValue().trim());
            }
            if (attributes.getNamedItem("default") != null && attributes.getNamedItem("fixed") == null) {
                element.setDefault(((Attr) attributes.getNamedItem("default")).getValue().trim());
            }

            // CountingPatterns are constructed for elements with minOccurs and maxOccurs different from default values
            if (attributes.getNamedItem("maxOccurs") != null && !((Attr) attributes.getNamedItem("maxOccurs")).getValue().equals("")) {
                maxOccursValue = (((Attr) attributes.getNamedItem("maxOccurs")).getValue().equals("unbounded") ? null : Integer.parseInt(((Attr) attributes.getNamedItem("maxOccurs")).getValue()));
                if (maxOccursValue != null && maxOccursValue < 0) {
                    // A negative value for maxOccurs is invalid.
                    throw new CountingPatternMaxOccursIllegalValueException("element");
                }
            }
            if (attributes.getNamedItem("minOccurs") != null && !((Attr) attributes.getNamedItem("minOccurs")).getValue().equals("")) {
                if (((Attr) attributes.getNamedItem("minOccurs")).getValue().equals("unbounded")) {
                    // The minOccurs-property is not allowed to have the value "unbounded".
                    throw new CountingPatternMinOccursIllegalValueException("element");
                } else {
                    minOccursValue = Integer.parseInt(((Attr) attributes.getNamedItem("minOccurs")).getValue());
                }
                // The minOccurs-property is not allowed to be negative and minOccursValue always must be smaller than the maxOccursValue.
                if (minOccursValue < 0) {
                    throw new CountingPatternMinOccursIllegalValueException("element");
                }
                if ((maxOccursValue != null && minOccursValue > maxOccursValue)) {
                    throw new CountingPatternMinOccursGreaterThanMaxOccursException("element");
                }
            }
            // TODO: The substitutionGroup head element like this element have to be global elements and the type of this element must be derived from the head element
//            if (attributes.getNamedItem("substitutionGroup") != null) {
//                if (!(node.getParentNode().getNodeType() == 1 && node.getParentNode().getNodeName().endsWith("schema"))) {
//                    throw new NonGlobalSubstitutionGroupException(element.getName());
//                }
//                if (attributes.getNamedItem("ref") != null) {
//                    throw new ExclusiveAttributesException("substitutionGroup and ref", element.getName());
//                }
//                if (!isQName(((Attr) attributes.getNamedItem("substitutionGroup")).getValue())) {
//                    throw new InvalidQNameException(((Attr) attributes.getNamedItem("substitutionGroup")).getValue(), "substitutionGroup");
//                }
//                QualifiedName substitutionGroupName = getName(((Attr) attributes.getNamedItem("substitutionGroup")).getValue());
//                if (!schema.getElementSymbolTable().hasReference(substitutionGroupName)) {
//                    Element element = new Element(substitutionGroupName);
//                    element.setDummy(true);
//                    schema.getElementSymbolTable().updateOrCreateReference(substitutionGroupName, element);
//                }
//                element.setSubstitutionGroup(schema.getElementSymbolTable().getReference(substitutionGroupName));
//                // Add this element to the global list of all substitutions with the substitutionGroupName as key
//                this.schema.addSubstitutionElement(schema.getElementSymbolTable().getReference(substitutionGroupName), schema.getElementSymbolTable().getReference(element.getName()));
//            }
            if (attributes.getNamedItem("type") != null) {
                if (attributes.getNamedItem("name") == null && attributes.getNamedItem("ref") == null) {
                    throw new MissingNameException();
                }
                if (!isQName(((Attr) attributes.getNamedItem("type")).getValue())) {
                    throw new InvalidQNameException(((Attr) attributes.getNamedItem("type")).getValue(), "type");
                }
                if (element.getTypeName() != null) {
                    throw new MultipleTypesException("element", element.getName().getFullyQualifiedName());
                }
                QualifiedName typeName = getName(((Attr) attributes.getNamedItem("type")).getValue());
                element.setTypeName(typeName);
            }
            if (attributes.getNamedItem("ref") != null) {
                if (attributes.getNamedItem("name") != null) {
                    throw new ExclusiveAttributesException("name and ref", element.getName());
                }
                if (attributes.getNamedItem("type") != null) {
                    throw new ExclusiveAttributesException("type and ref", element.getName());
                }
                if (attributes.getNamedItem("nillable") != null) {
                    throw new ExclusiveAttributesException("nillable and ref", element.getName());
                }
                if (attributes.getNamedItem("default") != null) {
                    throw new ExclusiveAttributesException("default and ref", element.getName());
                }
                if (attributes.getNamedItem("fixed") != null) {
                    throw new ExclusiveAttributesException("fixed and ref", element.getName());
                }
                if (attributes.getNamedItem("form") != null) {
                    throw new ExclusiveAttributesException("form and ref", element.getName());
                }
                if (attributes.getNamedItem("block") != null) {
                    throw new ExclusiveAttributesException("block and ref", element.getName());
                }
                if (!isQName(((Attr) attributes.getNamedItem("ref")).getValue())) {
                    throw new InvalidQNameException(((Attr) attributes.getNamedItem("ref")).getValue(), "ref");
                }
                QualifiedName refName = getName(((Attr) attributes.getNamedItem("ref")).getValue());
                ElementRef elementRef = new ElementRef(refName);
                if (attributes.getNamedItem("id") != null) {
                    elementRef.setId(((Attr) attributes.getNamedItem("id")).getValue());
                }
                if (maxOccursValue == null || (minOccursValue != 1 || maxOccursValue != 1)) {
                    CountingPattern countingPattern = new CountingPattern(elementRef, minOccursValue, maxOccursValue);
                    return countingPattern;
                }
                return elementRef;
            }
        }
// TODO: How to handle empty types?
//        if (element.getTypeName() == null) {
//            String anyTypeName = "{http://www.w3.org/2001/XMLSchema}anyType";
//            element.setTypeName(anyTypeName);
//        }
        if (maxOccursValue == null || (minOccursValue != 1 || maxOccursValue != 1)) {
            CountingPattern countingPattern = new CountingPattern(element, minOccursValue, maxOccursValue);
            return countingPattern;
        }
        return element;
    }

    /**
     * Visits a child of the element node and processes it according to its name
     * @param childNode     Node in the dom tree below the element Node
     * @throws java.lang.Exception
     */
    @Override
    protected void processChild(Node childNode) throws XSDParseException {

        // Tests if the node name is a local name and filters nodes with names #text, #comment and #document who are not in the enum
        String nodeName = childNode.getNodeName();
        if (nodeName.contains(":")) {
            nodeName = nodeName.split(":")[1];
        }
        if (childNode != null && !"#text".equals(nodeName) && !"#comment".equals(nodeName) && !"#document".equals(nodeName)) {
            switch (CASE.valueOf(nodeName.toUpperCase())) {
                case COMPLEXTYPE:
                    if (getDebug()) {
                        System.out.println("complexType");
                    }
                    if (element.getTypeName() != null) {
                        throw new MultipleTypesException("element", element.getName().getFullyQualifiedName());
                    } else {
                        ComplexTypeProcessor complexTypeProcessor = new ComplexTypeProcessor(schema);
                        element.setTypeName(complexTypeProcessor.processNode(childNode));
                    }
                    break;
                case SIMPLETYPE:
                    if (getDebug()) {
                        System.out.println("simpleType");
                    }
                    if (element.getTypeName() != null) {
                        throw new MultipleTypesException("element", element.getName().getFullyQualifiedName());
                    } else {
                        SimpleTypeProcessor simpleTypeProcessor = new SimpleTypeProcessor(schema);
                        element.setTypeName(simpleTypeProcessor.processNode(childNode));
                    }
                    break;
                case UNIQUE:
                    if (getDebug()) {
                        System.out.println("unique");
                    }
                    if (attributes.getNamedItem("ref") != null) {
                        throw new ExclusiveContentException("unique with ref", element.getName());
                    }
                    UniqueProcessor uniqueProcessor = new UniqueProcessor(schema);
                    element.addConstraint(uniqueProcessor.processNode(childNode));
                    break;
                case KEY:
                    if (getDebug()) {
                        System.out.println("key");
                    }
                    if (attributes.getNamedItem("ref") != null) {
                        throw new ExclusiveContentException("key with ref", element.getName());
                    }
                    KeyProcessor keyProcessor = new KeyProcessor(schema);
                    element.addConstraint(keyProcessor.processNode(childNode));
                    break;
                case KEYREF:
                    if (getDebug()) {
                        System.out.println("keyref");
                    }
                    if (attributes.getNamedItem("ref") != null) {
                        throw new ExclusiveContentException("keyref with ref", element.getName());
                    }
                    KeyrefProcessor keyrefProcessor = new KeyrefProcessor(schema);
                    element.addConstraint(keyrefProcessor.processNode(childNode));
                    break;
                case ANNOTATION:
                    if (getDebug()) {
                        System.out.println("annotation");
                    }
                    if (element.getAnnotation() == null) {
                        AnnotationProcessor annotationProcessor = new AnnotationProcessor(schema);
                        element.setAnnotation(annotationProcessor.processNode(childNode));
                    } else {
                        throw new MultipleAnnotationException("element");
                    }
                    break;
                default:
                    throw new UnsupportedContentException(nodeName, "element");
            }
        }
    }
}
