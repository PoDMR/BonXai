package eu.fox7.schematoolkit.xsd.parser;

import eu.fox7.schematoolkit.common.*;
import eu.fox7.schematoolkit.xsd.om.*;
import eu.fox7.schematoolkit.xsd.parser.exceptions.XSDParseException;
import eu.fox7.schematoolkit.xsd.parser.exceptions.attribute.*;
import eu.fox7.schematoolkit.xsd.parser.exceptions.content.*;
import eu.fox7.schematoolkit.xsd.parser.exceptions.inheritance.*;
import eu.fox7.schematoolkit.xsd.parser.exceptions.type.MultipleTypesException;

import java.util.*;
import org.w3c.dom.*;

/*******************************************************************************
 * Processor for the handling of an restriction-tag in XSD
 *******************************************************************************
 * @author Lars Schmidt, Dominik Wolff
 */
public class RestrictionProcessor extends Processor {

    // Particle is for the use in ComplexContentType!
    // The corresponding Processor has get this from here and handle this
    private Particle particle;
    // Inheritance of ths restriction
    private Inheritance inheritance;
    // List of enumerations
    private LinkedList<String> enumeration;
    // Id of this restriction
    private String idString;
    // Annotation of this restriction
    private Annotation annotation;
    // SimpleType/ComplexType resulted from the content of the current restriction tag
    private QualifiedName anonymousSimpleTypeName;
    // True if an attribute was read in the content, only attributes are allowed
    // to follow
    private boolean alreadyAnAttributeAdded = false;

    /**
     * This is the constructor of the class RestrictionProcessor.
     * @param schema
     */
    public RestrictionProcessor(XSDSchema schema) {
        super(schema);
    }

    /**
     * This processor works in different usecases and so it handles different
     * restriction types. A so called type is pending on the direct parent in
     * the XSD-structure:
     *
     * - SimpleType-Restriction
     * - SimpleContent-Restriction
     * - ComplexContent-Restriction
     *
     */
    protected enum restrictionType {

        /**
         * SimpleType-Restriction
         */
        SIMPLETYPE,
        /**
         * SimpleContent-Restriction
         */
        SIMPLECONTENT,
        /**
         * ComplexContent-Restriction
         */
        COMPLEXCONTENT
    }

    /**
     * This method manages the possible child-content af the current restriction
     *
     * Under a SimpleType:
     * Content: (annotation?, (simpleType?, (minExclusive | minInclusive |
     *          maxExclusive | maxInclusive | totalDigits | fractionDigits |
     *          length | minLength | maxLength | enumeration | whiteSpace |
     *          pattern)*))
     *
     * Under a ComplexContent:
     * Content: (annotation?, (group | all | choice | sequence)?, ((attribute |
     *          attributeGroup)*, anyAttribute?))
     *
     * Under a SimpleContent:
     * Content: (annotation?, (simpleType?, (minExclusive | minInclusive |
     *          maxExclusive | maxInclusive | totalDigits | fractionDigits |
     *          length | minLength | maxLength | enumeration | whiteSpace |
     *          pattern)*)?, ((attribute | attributeGroup)*, anyAttribute?))
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
        String grandParentName = childNode.getParentNode().getParentNode().getNodeName();
        if (grandParentName.contains(":")) {
            grandParentName = grandParentName.split(":")[1];
        }

        if (childNode != null && !"#text".equals(nodeName) && !"#comment".equals(nodeName) && !"#document".equals(nodeName)) {
            switch (CASE.valueOf(nodeName.toUpperCase())) {
                // Only for COMPLEXCONTENT
                case GROUP:
                    if (grandParentName.toUpperCase().equals(restrictionType.COMPLEXCONTENT.name())) {
                        if (getDebug()) {
                            System.out.println("group");
                        }
                        // Attributes have to be the last elements in the content model
                        if (alreadyAnAttributeAdded) {
                            throw new AttributeIsNotLastInContentModelException("group to restriction");
                        }
                        if (this.particle == null) {
                            GroupReferenceProcessor groupRefProcessor = new GroupReferenceProcessor(schema);
                            Object object = groupRefProcessor.processNode(childNode);
                            if (object instanceof Particle) {
                                particle = (Particle) object;
                            } else {
                                throw new IllegalObjectReturnedException(object.getClass().getName(), "restriction");
                            }
                        } else {
                            throw new RestrictionMultipleParticleException("group");
                        }
                    } else {
                        throw new RestrictionWrongChildException(grandParentName.toUpperCase(), "GROUP");
                    }
                    break;
                case ALL:
                    if (grandParentName.toUpperCase().equals(restrictionType.COMPLEXCONTENT.name())) {
                        if (getDebug()) {
                            System.out.println("all");
                        }
                        // Attributes have to be the last elements in the content model
                        if (alreadyAnAttributeAdded) {
                            throw new AttributeIsNotLastInContentModelException("all to restriction");
                        }
                        if (this.particle == null) {
                            AllProcessor allProcessor = new AllProcessor(schema);
                            particle = allProcessor.processNode(childNode);
                        } else {
                            throw new RestrictionMultipleParticleException("all");
                        }
                    } else {
                        throw new RestrictionWrongChildException(grandParentName.toUpperCase(), "all");
                    }
                    break;
                case CHOICE:
                    if (grandParentName.toUpperCase().equals(restrictionType.COMPLEXCONTENT.name())) {
                        if (getDebug()) {
                            System.out.println("choice");
                        }
                        // Attributes have to be the last elements in the content model
                        if (alreadyAnAttributeAdded) {
                            throw new AttributeIsNotLastInContentModelException("choice to restriction");
                        }
                        if (this.particle == null) {
                            ChoiceProcessor choiceProcessor = new ChoiceProcessor(schema);
                            particle = choiceProcessor.processNode(childNode);
                        } else {
                            throw new RestrictionMultipleParticleException("choice");
                        }
                    } else {
                        throw new RestrictionWrongChildException(grandParentName.toUpperCase(), "choice");
                    }
                    break;
                case SEQUENCE:
                    if (grandParentName.toUpperCase().equals(restrictionType.COMPLEXCONTENT.name())) {
                        if (getDebug()) {
                            System.out.println("sequence");
                        }
                        // Attributes have to be the last elements in the content model
                        if (alreadyAnAttributeAdded) {
                            throw new AttributeIsNotLastInContentModelException("sequence to restriction");
                        }
                        if (this.particle == null) {
                            SequenceProcessor sequenceProcessor = new SequenceProcessor(schema);
                            particle = sequenceProcessor.processNode(childNode);
                        } else {
                            throw new RestrictionMultipleParticleException("sequence");
                        }
                    } else {
                        throw new RestrictionWrongChildException(grandParentName.toUpperCase(), "sequence");
                    }
                    break;

                // Only for COMPLEXCONTENT and SIMPLECONTENT
                case ATTRIBUTE:
                    if (grandParentName.toUpperCase().equals(restrictionType.COMPLEXCONTENT.name())
                            || grandParentName.toUpperCase().equals(restrictionType.SIMPLECONTENT.name())) {
                        if (getDebug()) {
                            System.out.println("attribute");
                        }
                        AttributeProcessor attributeProcessor = new AttributeProcessor(schema);
                        AttributeParticle attributeParticle = attributeProcessor.processNode(childNode);
                        if (grandParentName.toUpperCase().equals(restrictionType.COMPLEXCONTENT.name())) {
                            // An AnyAttribute is only allowed to be the last element in an attributeGroup.
                            // If a second anyAttribute is found after an anyAttribute, this is a failure.
                            if (!((ComplexContentRestriction) inheritance).getAttributes().isEmpty() && ((ComplexContentRestriction) inheritance).getAttributes().getLast() instanceof AnyAttribute) {
                                throw new AnyAttributeIsNotLastException("attribute to restriction");
                            }
                            ((ComplexContentRestriction) inheritance).addAttribute(attributeParticle);
                            alreadyAnAttributeAdded = true;
                        } else {
                            // An AnyAttribute is only allowed to be the last element in an attributeGroup.
                            // If a second anyAttribute is found after an anyAttribute, this is a failure.
                            if (!((SimpleContentRestriction) inheritance).getAttributes().isEmpty() && ((SimpleContentRestriction) inheritance).getAttributes().getLast() instanceof AnyAttribute) {
                                throw new AnyAttributeIsNotLastException("attribute to restriction");
                            }
                            // IMPORTANT: xsd object model has to feature attributes for SimpleContentRestriction!
                            ((SimpleContentRestriction) inheritance).addAttribute(attributeParticle);
                            alreadyAnAttributeAdded = true;
                        }
                    } else {
                        throw new RestrictionWrongChildException(grandParentName.toUpperCase(), "attribute");
                    }
                    break;
                case ATTRIBUTEGROUP:
                    if (grandParentName.toUpperCase().equals(restrictionType.COMPLEXCONTENT.name())
                            || grandParentName.toUpperCase().equals(restrictionType.SIMPLECONTENT.name())) {
                        if (getDebug()) {
                            System.out.println("attributeGroup");
                        }
                        AttributeGroupReferenceProcessor attributeGroupReferenceProcessor = new AttributeGroupReferenceProcessor(schema);
                        AttributeGroupReference attributeGroupRef =  attributeGroupReferenceProcessor.processNode(childNode);
                        if (grandParentName.toUpperCase().equals(restrictionType.COMPLEXCONTENT.name())) {
                            // An AnyAttribute is only allowed to be the last element in an attributeGroup.
                            // If a second anyAttribute is found after an anyAttribute, this is a failure.
                            if (!((ComplexContentRestriction) inheritance).getAttributes().isEmpty() && ((ComplexContentRestriction) inheritance).getAttributes().getLast() instanceof AnyAttribute) {
                                throw new AnyAttributeIsNotLastException("attributeGroup to restriction");
                            }
                            ((ComplexContentRestriction) inheritance).addAttribute(attributeGroupRef);
                            alreadyAnAttributeAdded = true;
                        } else {
                            // An AnyAttribute is only allowed to be the last element in an attributeGroup.
                            // If a second anyAttribute is found after an anyAttribute, this is a failure.
                            if (!((SimpleContentRestriction) inheritance).getAttributes().isEmpty() && ((SimpleContentRestriction) inheritance).getAttributes().getLast() instanceof AnyAttribute) {
                                throw new AnyAttributeIsNotLastException("attributeGroup to restriction");
                            }
                            // IMPORTANT: xsd object model has to feature attributes for SimpleContentRestriction!
                            ((SimpleContentRestriction) inheritance).addAttribute(attributeGroupRef);
                            alreadyAnAttributeAdded = true;
                        }
                    } else {
                        throw new RestrictionWrongChildException(grandParentName.toUpperCase(), "attributeGroup");
                    }
                    break;
                case ANYATTRIBUTE:
                    if (grandParentName.toUpperCase().equals(restrictionType.COMPLEXCONTENT.name())
                            || grandParentName.toUpperCase().equals(restrictionType.SIMPLECONTENT.name())) {
                        if (getDebug()) {
                            System.out.println("anyAttribute");
                        }
                        AnyAttributeProcessor anyAttributeProcessor = new AnyAttributeProcessor(schema);
                        AnyAttribute anyAttribute = anyAttributeProcessor.processNode(childNode);
                        if (grandParentName.toUpperCase().equals(restrictionType.COMPLEXCONTENT.name())) {
                            // An AnyAttribute is only allowed to be the last element in an attributeGroup.
                            // If a second anyAttribute is found after an anyAttribute, this is a failure.
                            if (!((ComplexContentRestriction) inheritance).getAttributes().isEmpty() && ((ComplexContentRestriction) inheritance).getAttributes().getLast() instanceof AnyAttribute) {
                                throw new AnyAttributeIsNotLastException("anyAttribute to restriction");
                            }
                            ((ComplexContentRestriction) inheritance).addAttribute(anyAttribute);
                            alreadyAnAttributeAdded = true;
                        } else {
                            // An AnyAttribute is only allowed to be the last element in an attributeGroup.
                            // If a second anyAttribute is found after an anyAttribute, this is a failure.
                            if (!((SimpleContentRestriction) inheritance).getAttributes().isEmpty() && ((SimpleContentRestriction) inheritance).getAttributes().getLast() instanceof AnyAttribute) {
                                throw new AnyAttributeIsNotLastException("anyAttribute to restriction");
                            }
                            // IMPORTANT: xsd object model has to feature attributes for SimpleContentRestriction!
                            ((SimpleContentRestriction) inheritance).addAttribute(anyAttribute);
                            alreadyAnAttributeAdded = true;
                        }
                    } else {
                        throw new RestrictionWrongChildException(grandParentName.toUpperCase(), "anyAttribute");
                    }
                    break;
                // Only for SIMPLETYPE and SIMPLECONTENT
                case SIMPLETYPE:
                    if (grandParentName.toUpperCase().equals(restrictionType.SIMPLETYPE.name())
                            || grandParentName.toUpperCase().equals(restrictionType.SIMPLECONTENT.name())) {
                        if (getDebug()) {
                            System.out.println("SimpleType");
                        }
                        SimpleTypeProcessor simpleTypeProcessor = new SimpleTypeProcessor(schema);
                        this.anonymousSimpleTypeName = simpleTypeProcessor.processNode(childNode);
                    } else {
                        throw new RestrictionWrongChildException(grandParentName.toUpperCase(), "SimpleType");
                    }
                    break;
                case MINEXCLUSIVE:
                    if (grandParentName.toUpperCase().equals(restrictionType.SIMPLETYPE.name())
                            || grandParentName.toUpperCase().equals(restrictionType.SIMPLECONTENT.name())) {
                        if (getDebug()) {
                            System.out.println("minexclusive");
                        }
                        Attr attributeValue = (Attr) childNode.getAttributes().getNamedItem("value");
                        String stringValue = attributeValue.getValue();
                        Attr attributeFixed = (Attr) childNode.getAttributes().getNamedItem("fixed");
                        boolean booleanFixed = (attributeFixed != null && attributeFixed.getValue().equals("true"));
                        SimpleContentFixableRestrictionProperty<String> scfrp = new SimpleContentFixableRestrictionProperty<String>(stringValue, booleanFixed);
                        ((SimpleContentRestriction) this.inheritance).setMinExclusive(scfrp);
                    } else {
                        throw new RestrictionWrongChildException(grandParentName.toUpperCase(), "minexclusive");
                    }
                    break;
                case MAXEXCLUSIVE:
                    if (grandParentName.toUpperCase().equals(restrictionType.SIMPLETYPE.name())
                            || grandParentName.toUpperCase().equals(restrictionType.SIMPLECONTENT.name())) {
                        if (getDebug()) {
                            System.out.println("maxexclusive");
                        }
                        Attr attributeValue = (Attr) childNode.getAttributes().getNamedItem("value");
                        String stringValue = attributeValue.getValue();
                        Attr attributeFixed = (Attr) childNode.getAttributes().getNamedItem("fixed");
                        boolean booleanFixed = (attributeFixed != null && attributeFixed.getValue().equals("true"));
                        SimpleContentFixableRestrictionProperty<String> scfrp = new SimpleContentFixableRestrictionProperty<String>(stringValue, booleanFixed);
                        ((SimpleContentRestriction) this.inheritance).setMaxExclusive(scfrp);
                    } else {
                        throw new RestrictionWrongChildException(grandParentName.toUpperCase(), "maxexclusive");
                    }
                    break;
                case MININCLUSIVE:
                    if (grandParentName.toUpperCase().equals(restrictionType.SIMPLETYPE.name())
                            || grandParentName.toUpperCase().equals(restrictionType.SIMPLECONTENT.name())) {
                        if (getDebug()) {
                            System.out.println("mininclusive");
                        }
                        Attr attributeValue = (Attr) childNode.getAttributes().getNamedItem("value");
                        String stringValue = attributeValue.getValue();
                        Attr attributeFixed = (Attr) childNode.getAttributes().getNamedItem("fixed");
                        boolean booleanFixed =  (attributeFixed != null && attributeFixed.getValue().equals("true"));
                        SimpleContentFixableRestrictionProperty<String> scfrp = new SimpleContentFixableRestrictionProperty<String>(stringValue, booleanFixed);
                        ((SimpleContentRestriction) this.inheritance).setMinInclusive(scfrp);
                    } else {
                        throw new RestrictionWrongChildException(grandParentName.toUpperCase(), "mininclusive");
                    }
                    break;
                case MAXINCLUSIVE:
                    if (grandParentName.toUpperCase().equals(restrictionType.SIMPLETYPE.name())
                            || grandParentName.toUpperCase().equals(restrictionType.SIMPLECONTENT.name())) {
                        if (getDebug()) {
                            System.out.println("maxinclusive");
                        }
                        Attr attributeValue = (Attr) childNode.getAttributes().getNamedItem("value");
                        String stringValue = attributeValue.getValue();
                        Attr attributeFixed = (Attr) childNode.getAttributes().getNamedItem("fixed");
                        boolean booleanFixed =  (attributeFixed != null && attributeFixed.getValue().equals("true"));
                        SimpleContentFixableRestrictionProperty<String> scfrp = new SimpleContentFixableRestrictionProperty<String>(stringValue, booleanFixed);
                        ((SimpleContentRestriction) this.inheritance).setMaxInclusive(scfrp);
                    } else {
                        throw new RestrictionWrongChildException(grandParentName.toUpperCase(), "maxinclusive");
                    }
                    break;
                case TOTALDIGITS:
                    if (grandParentName.toUpperCase().equals(restrictionType.SIMPLETYPE.name())
                            || grandParentName.toUpperCase().equals(restrictionType.SIMPLECONTENT.name())) {
                        if (getDebug()) {
                            System.out.println("totaldigits");
                        }
                        Attr attributeValue = (Attr) childNode.getAttributes().getNamedItem("value");
                        Integer integerValue = Integer.parseInt(attributeValue.getValue());
                        Attr attributeFixed = (Attr) childNode.getAttributes().getNamedItem("fixed");
                        boolean booleanFixed =  (attributeFixed != null && attributeFixed.getValue().equals("true"));
                        SimpleContentFixableRestrictionProperty<Integer> scfrp = new SimpleContentFixableRestrictionProperty<Integer>(integerValue, booleanFixed);
                        ((SimpleContentRestriction) this.inheritance).setTotalDigits(scfrp);
                    } else {
                        throw new RestrictionWrongChildException(grandParentName.toUpperCase(), "totaldigits");
                    }
                    break;
                case FRACTIONDIGITS:
                    if (grandParentName.toUpperCase().equals(restrictionType.SIMPLETYPE.name())
                            || grandParentName.toUpperCase().equals(restrictionType.SIMPLECONTENT.name())) {
                        if (getDebug()) {
                            System.out.println("fractiondigits");
                        }
                        Attr attributeValue = (Attr) childNode.getAttributes().getNamedItem("value");
                        Integer integerValue = Integer.parseInt(attributeValue.getValue());
                        Attr attributeFixed = (Attr) childNode.getAttributes().getNamedItem("fixed");
                        boolean booleanFixed =  (attributeFixed != null && attributeFixed.getValue().equals("true"));
                        SimpleContentFixableRestrictionProperty<Integer> scfrp = new SimpleContentFixableRestrictionProperty<Integer>(integerValue, booleanFixed);
                        ((SimpleContentRestriction) this.inheritance).setFractionDigits(scfrp);
                    } else {
                        throw new RestrictionWrongChildException(grandParentName.toUpperCase(), "fractiondigits");
                    }
                    break;
                case LENGTH:
                    if (grandParentName.toUpperCase().equals(restrictionType.SIMPLETYPE.name())
                            || grandParentName.toUpperCase().equals(restrictionType.SIMPLECONTENT.name())) {
                        if (getDebug()) {
                            System.out.println("length");
                        }
                        Attr attributeValue = (Attr) childNode.getAttributes().getNamedItem("value");
                        Integer integerValue = Integer.parseInt(attributeValue.getValue());
                        Attr attributeFixed = (Attr) childNode.getAttributes().getNamedItem("fixed");
                        boolean booleanFixed =  (attributeFixed != null && attributeFixed.getValue().equals("true"));
                        SimpleContentFixableRestrictionProperty<Integer> scfrp = new SimpleContentFixableRestrictionProperty<Integer>(integerValue, booleanFixed);
                        ((SimpleContentRestriction) this.inheritance).setLength(scfrp);
                    } else {
                        throw new RestrictionWrongChildException(grandParentName.toUpperCase(), "length");
                    }
                    break;
                case MINLENGTH:
                    if (grandParentName.toUpperCase().equals(restrictionType.SIMPLETYPE.name())
                            || grandParentName.toUpperCase().equals(restrictionType.SIMPLECONTENT.name())) {
                        if (getDebug()) {
                            System.out.println("minlength");
                        }
                        Attr attributeValue = (Attr) childNode.getAttributes().getNamedItem("value");
                        Integer integerValue = Integer.parseInt(attributeValue.getValue());
                        Attr attributeFixed = (Attr) childNode.getAttributes().getNamedItem("fixed");
                        boolean booleanFixed =  (attributeFixed != null && attributeFixed.getValue().equals("true"));
                        SimpleContentFixableRestrictionProperty<Integer> scfrp = new SimpleContentFixableRestrictionProperty<Integer>(integerValue, booleanFixed);
                        ((SimpleContentRestriction) this.inheritance).setMinLength(scfrp);
                    } else {
                        throw new RestrictionWrongChildException(grandParentName.toUpperCase(), "minlength");
                    }
                    break;
                case MAXLENGTH:
                    if (grandParentName.toUpperCase().equals(restrictionType.SIMPLETYPE.name())
                            || grandParentName.toUpperCase().equals(restrictionType.SIMPLECONTENT.name())) {
                        if (getDebug()) {
                            System.out.println("maxlength");
                        }
                        Attr attributeValue = (Attr) childNode.getAttributes().getNamedItem("value");
                        Integer integerValue = Integer.parseInt(attributeValue.getValue());
                        Attr attributeFixed = (Attr) childNode.getAttributes().getNamedItem("fixed");
                        boolean booleanFixed =  (attributeFixed != null && attributeFixed.getValue().equals("true"));
                        SimpleContentFixableRestrictionProperty<Integer> scfrp = new SimpleContentFixableRestrictionProperty<Integer>(integerValue, booleanFixed);
                        ((SimpleContentRestriction) this.inheritance).setMaxLength(scfrp);
                    } else {
                        throw new RestrictionWrongChildException(grandParentName.toUpperCase(), "maxlength");
                    }
                    break;
                case ENUMERATION:
                    if (grandParentName.toUpperCase().equals(restrictionType.SIMPLETYPE.name())
                            || grandParentName.toUpperCase().equals(restrictionType.SIMPLECONTENT.name())) {
                        if (getDebug()) {
                            System.out.println("enumeration");
                        }
                        Attr attributeValue = (Attr) childNode.getAttributes().getNamedItem("value");
                        String stringValue = attributeValue.getValue();
                        this.enumeration.add(stringValue);
                    } else {
                        throw new RestrictionWrongChildException(grandParentName.toUpperCase(), "enumeration");
                    }
                    break;
                case WHITESPACE:
                    if (grandParentName.toUpperCase().equals(restrictionType.SIMPLETYPE.name())
                            || grandParentName.toUpperCase().equals(restrictionType.SIMPLECONTENT.name())) {
                        if (getDebug()) {
                            System.out.println("whitespace");
                        }
                        Attr attributeValue = (Attr) childNode.getAttributes().getNamedItem("value");
                        String stringValue = attributeValue.getValue();
                        Attr attributeFixed = (Attr) childNode.getAttributes().getNamedItem("fixed");
                        boolean booleanFixed =  (attributeFixed != null && attributeFixed.getValue().equals("true"));
                        SimpleContentPropertyWhitespace scpw = null;
                        if (stringValue.equals("collapse")) {
                            scpw = SimpleContentPropertyWhitespace.collapse;
                        }
                        if (stringValue.equals("preserve")) {
                            scpw = SimpleContentPropertyWhitespace.preserve;
                        }
                        if (stringValue.equals("replace")) {
                            scpw = SimpleContentPropertyWhitespace.replace;
                        }

                        SimpleContentFixableRestrictionProperty<SimpleContentPropertyWhitespace> scfrp = new SimpleContentFixableRestrictionProperty<SimpleContentPropertyWhitespace>(scpw, booleanFixed);
                        ((SimpleContentRestriction) this.inheritance).setWhitespace(scfrp);
                    } else {
                        throw new RestrictionWrongChildException(grandParentName.toUpperCase(), "whitespace");
                    }
                    break;
                case PATTERN:
                    if (grandParentName.toUpperCase().equals(restrictionType.SIMPLETYPE.name())
                            || grandParentName.toUpperCase().equals(restrictionType.SIMPLECONTENT.name())) {
                        if (getDebug()) {
                            System.out.println("pattern");
                        }
                        Attr attributeValue = (Attr) childNode.getAttributes().getNamedItem("value");
                        String stringValue = attributeValue.getValue();
                        SimpleContentRestrictionProperty<String> scrp = new SimpleContentRestrictionProperty<String>(stringValue);
                        ((SimpleContentRestriction) this.inheritance).setPattern(scrp);
                    } else {
                        throw new RestrictionWrongChildException(grandParentName.toUpperCase(), "pattern");
                    }
                    break;
                case ANNOTATION:
                    if (annotation == null) {
                        AnnotationProcessor annotationProcessor = new AnnotationProcessor(schema);
                        annotation = annotationProcessor.processNode(childNode);
                    } else {
                        throw new MultipleAnnotationException("restriction");
                    }
                    break;
                default:
                    throw new UnsupportedContentException(nodeName, "restriction");
            }
        }
    }

    /**
     * The method processNode handles all attributes and properties of
     * the current restriction-tag. The given properties are checked against the
     * allowed values defined in the XSD primer.
     * There are three different use cases of a restriction in XSD.
     *
     * Under a SimpleType:
     * -------------------------------------------------------------------------
     * <restriction
     * base = QName
     * id = ID
     * {any attributes with non-schema namespace . . .}>
     * Content: (annotation?, (simpleType?, (minExclusive | minInclusive |
     *          maxExclusive | maxInclusive | totalDigits | fractionDigits |
     *          length | minLength | maxLength | enumeration | whiteSpace |
     *          pattern)*))
     * </restriction>
     *-------------------------------------------------------------------------
     *
     *
     * Under a ComplexContent:
     * -------------------------------------------------------------------------
     * <restriction
     * base = QName
     * id = ID
     * {any attributes with non-schema namespace . . .}>
     * Content: (annotation?, (group | all | choice | sequence)?, ((attribute |
     *          attributeGroup)*, anyAttribute?))
     * </restriction>
     * -------------------------------------------------------------------------
     *
     *
     * Under a SimpleContent:
     * -------------------------------------------------------------------------
     * <restriction
     * base = QName
     * id = ID
     * {any attributes with non-schema namespace . . .}>
     * Content: (annotation?, (simpleType?, (minExclusive | minInclusive |
     *          maxExclusive | maxInclusive | totalDigits | fractionDigits |
     *          length | minLength | maxLength | enumeration | whiteSpace |
     *          pattern)*)?, ((attribute | attributeGroup)*, anyAttribute?))
     * </restriction>
     * -------------------------------------------------------------------------
     *
     * The content is managed by the processChild-method.
     * @param node
     * @return Inheritance
     * @throws Exception
     */
    @Override
    protected Inheritance processNode(Node node) throws XSDParseException {

        // We have to check the parentnode to determine if it is an restriction
        // for a simpleContent or a complexContent or a simpletype
        // (check with if-statement over the given enum and the name of the parentnode)

        String parentNodeName = node.getParentNode().getNodeName();
        if (parentNodeName.contains(":")) {
            parentNodeName = parentNodeName.split(":")[1];
        }

        QualifiedName baseTypeName = null;

        // Check for Attribute "base" and get the typeRef from the Type Symboltable
        if (node.getAttributes() != null && node.getAttributes().getNamedItem("base") != null) {
            baseTypeName = getName(node.getAttributes().getNamedItem("base").getNodeValue());
        }
        // If there is an ID, we can add it to the corresponding object.
        if (node.getAttributes().getNamedItem("id") != null) {
            if (node.getAttributes().getNamedItem("id").getNodeValue().equals("")) {
                throw new EmptyIdException("restriction");
            }
            idString = node.getAttributes().getNamedItem("id").getNodeValue();
        }

        // switch over grandParentName
        if (parentNodeName.toUpperCase().equals(restrictionType.SIMPLETYPE.name())
                || parentNodeName.toUpperCase().equals(restrictionType.SIMPLECONTENT.name())) {
        	this.inheritance = new SimpleContentRestriction(baseTypeName);
            this.enumeration = new LinkedList<String>();
        } else if (parentNodeName.toUpperCase().equals(restrictionType.COMPLEXCONTENT.name())) {
            this.inheritance = new ComplexContentRestriction(baseTypeName);
        }

        // Call the visitChildren method to handle children and find necessary details
        visitChildren(node);
        
        if (parentNodeName.toUpperCase().equals(restrictionType.SIMPLETYPE.name())
                || parentNodeName.toUpperCase().equals(restrictionType.SIMPLECONTENT.name())) {

            if (parentNodeName.toUpperCase().equals(restrictionType.SIMPLETYPE.name())) {
                if (baseTypeName != null && this.anonymousSimpleTypeName != null) {
                    throw new MultipleTypesException(restrictionType.SIMPLETYPE.name(), "restriction");
                }
                if (((SimpleContentRestriction) this.inheritance).getBaseType() == null) {
                    ((SimpleContentRestriction) this.inheritance).setBaseType(anonymousSimpleTypeName);
                }
            } else {
                ((SimpleContentRestriction) this.inheritance).setBaseType(anonymousSimpleTypeName);
            }
            
            // check if enumeration is not empty and set it to the inheritance
            if (!this.enumeration.isEmpty()) {
                ((SimpleContentRestriction) this.inheritance).addEnumeration(this.enumeration);
            }
        }
        if (idString != null) {
            this.inheritance.setId(idString);
        }
        inheritance.setAnnotation(annotation);

        return this.inheritance;
    }

    /**
     * This is used by the ComplexContentProcessor to get the particle out of
     * this restriction. This is necessary, because the standard return value is
     * the generated inheritance above.
     * @return Particle - Returns the particle of this restriction inheritance
     */
    protected Particle getParticle() {
        return this.particle;
    }
}
