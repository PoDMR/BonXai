package eu.fox7.bonxai.xsd.parser;

import eu.fox7.bonxai.common.*;
import eu.fox7.bonxai.xsd.*;
import eu.fox7.bonxai.xsd.Group;
import eu.fox7.bonxai.xsd.parser.exceptions.attribute.*;
import eu.fox7.bonxai.xsd.parser.exceptions.attribute.countingpattern.*;
import eu.fox7.bonxai.xsd.parser.exceptions.content.*;

import org.w3c.dom.*;

/*******************************************************************************
 * Processor for the handling of an group-tag in XSD
 *******************************************************************************
 * @author Lars Schmidt, Dominik Wolff
 */
public class GroupProcessor extends Processor {

    // Content of this group
    private ParticleContainer container;
    // Value of minOccurs and MaxOccurs
    private Integer minOccursValue = 1, maxOccursValue = 1;
    // Annotation of this group
    private Annotation annotation;
    // Id of this group
    private String idString;

    /**
     * This is the constructor of the class GroupProcessor.
     * @param schema
     */
    public GroupProcessor(XSDSchema schema) {
        super(schema);
    }

    /**
     * The method processNode handles all attributes and properties of
     * the current group-tag. The given properties are checked against the allowed
     * values defined in the XSD primer.
     *
     * <group
     * id = ID
     * maxOccurs = (nonNegativeInteger | unbounded)  : 1
     * minOccurs = nonNegativeInteger : 1
     * name = NCName
     * ref = QName
     * {any attributes with non-schema namespace . . .}>
     * Content: (annotation?, (all | choice | sequence)?)
     * </group>
     *
     * The content is managed by the processChild-method below.
     * 
     * @param node
     * @return Object
     * @throws Exception
     */
    @Override
    protected Object processNode(Node node) throws Exception {
        // Call the visitChildren method to handle children and find necessary details for the current group
        visitChildren(node);
        String groupName = getName(node);

        // List of Attributes
        NamedNodeMap attributes = node.getAttributes();
        if (attributes != null && attributes.getNamedItem("name") != null && attributes.getNamedItem("ref") != null) {
            throw new ExclusiveAttributesException("name and ref", groupName);
        }
        if (attributes != null && attributes.getNamedItem("ref") != null && container == null) {
            String refValue = ((Attr) attributes.getNamedItem("ref")).getValue();
            if (!isQName(refValue)) {
                throw new InvalidQNameException(refValue, "group");
            }
            String refName = getName(refValue);
            // The following part sets the ID of a groupRef.
            if (attributes.getNamedItem("id") != null) {
                if (attributes.getNamedItem("id").getNodeValue().equals("")) {
                    throw new EmptyIdException("group");
                }
                idString = ((Attr) attributes.getNamedItem("id")).getValue();
            }
            if (attributes.getNamedItem("maxOccurs") != null && !((Attr) attributes.getNamedItem("maxOccurs")).getValue().equals("")) {
                maxOccursValue = (((Attr) attributes.getNamedItem("maxOccurs")).getValue().equals("unbounded") ? null : Integer.parseInt(((Attr) attributes.getNamedItem("maxOccurs")).getValue()));
                // The maxOccurs-property is not allowed to be negative.
                if (maxOccursValue != null && maxOccursValue < 0) {
                    throw new CountingPatternMaxOccursIllegalValueException("group");
                }
            }
            if (attributes.getNamedItem("minOccurs") != null && !((Attr) attributes.getNamedItem("minOccurs")).getValue().equals("")) {
                if (((Attr) attributes.getNamedItem("minOccurs")).getValue().equals("unbounded")) {
                    // The minOccurs-property is not allowed to have the value "unbounded".
                    throw new CountingPatternMinOccursIllegalValueException("group");
                } else {
                    minOccursValue = Integer.parseInt(((Attr) attributes.getNamedItem("minOccurs")).getValue());
                }
                // The minOccurs-property is not allowed to be negative and minOccursValue always must be smaller than the maxOccursValue.
                if (minOccursValue < 0) {
                    throw new CountingPatternMinOccursIllegalValueException("group");
                }
                if (maxOccursValue != null && minOccursValue > maxOccursValue) {
                    throw new CountingPatternMinOccursGreaterThanMaxOccursException("group");
                }
            }

            if (!schema.getGroupSymbolTable().hasReference(refName)) {
                Group groupRefElement = new Group(refName, container);
                groupRefElement.setDummy(true);
                schema.getGroupSymbolTable().updateOrCreateReference(refName, groupRefElement);
            }

            if (maxOccursValue == null || minOccursValue != 1 || maxOccursValue != 1) {
                CountingPattern countingPattern = new CountingPattern(minOccursValue, maxOccursValue);
                GroupRef groupRef = new GroupRef(schema.getGroupSymbolTable().getReference(refName));
                groupRef.setAnnotation(annotation);
                if (idString != null) {
                    groupRef.setId(idString);
                }
                countingPattern.addParticle(groupRef);
                return countingPattern;
            }

            // ref Attribute of Group
            GroupRef groupRef = new GroupRef(schema.getGroupSymbolTable().getReference(refName));
            groupRef.setAnnotation(annotation);
            if (idString != null) {
                groupRef.setId(idString);
            }
            return groupRef;
        }

        if (!isNCName(getLocalName(node))) {
            throw new InvalidNCNameException(getLocalName(node), "group");
        }
        // Generate XSD group object

        Group group = schema.getGroupSymbolTable().updateOrCreateReference(groupName, new Group(groupName, container)).getReference();
        group.setAnnotation(annotation);

        // The following part sets the ID of a group.
        // This is necessary at this point, because the Setting of the ID above is only for groupRefs.
        if (node.getAttributes().getNamedItem("id") != null) {
            if (node.getAttributes().getNamedItem("id").getNodeValue().equals("")) {
                throw new EmptyIdException("group");
            }
            group.setId(node.getAttributes().getNamedItem("id").getNodeValue());
        }
        group.setDummy(false);
        return group;
    }

    /**
     * This method manages the possible content af the current group-tag.
     *
     * Content: (annotation?, (all | choice | sequence)?)
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
                case ALL:
                    if (getDebug()) {
                        System.out.println("all");
                    }
                    if (container != null) {
                        throw new GroupMultipleParticleContainerException("all");
                    } else {
                        AllProcessor allProcessor = new AllProcessor(schema);
                        container = allProcessor.processNode(childNode);
                    }
                    break;
                case CHOICE:
                    if (getDebug()) {
                        System.out.println("choice");
                    }
                    if (container != null) {
                        throw new GroupMultipleParticleContainerException("choice");
                    } else {
                        ChoiceProcessor choiceProcessor = new ChoiceProcessor(schema);
                        container = choiceProcessor.processNode(childNode);
                    }
                    break;
                case SEQUENCE:
                    if (getDebug()) {
                        System.out.println("sequence");
                    }
                    if (container != null) {
                        throw new GroupMultipleParticleContainerException("sequence");
                    } else {
                        SequenceProcessor sequenceProcessor = new SequenceProcessor(schema);
                        container = sequenceProcessor.processNode(childNode);
                    }
                    break;
                case ANNOTATION:
                    if (annotation == null) {
                        AnnotationProcessor annotationProcessor = new AnnotationProcessor(schema);
                        annotation = annotationProcessor.processNode(childNode);
                    } else {
                        throw new MultipleAnnotationException("group");
                    }
                    break;
                default:
                    throw new UnsupportedContentException(nodeName, "group");
            }
        }
    }
}
