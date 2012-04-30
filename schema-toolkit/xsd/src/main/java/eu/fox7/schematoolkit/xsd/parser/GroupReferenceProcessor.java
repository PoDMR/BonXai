package eu.fox7.schematoolkit.xsd.parser;

import eu.fox7.schematoolkit.common.*;
import eu.fox7.schematoolkit.xsd.om.*;
import eu.fox7.schematoolkit.xsd.parser.exceptions.XSDParseException;
import eu.fox7.schematoolkit.xsd.parser.exceptions.attribute.*;
import eu.fox7.schematoolkit.xsd.parser.exceptions.attribute.countingpattern.*;
import eu.fox7.schematoolkit.xsd.parser.exceptions.content.*;

import org.w3c.dom.*;

/*******************************************************************************
 * Processor for the handling of an group-tag in XSD
 *******************************************************************************
 * @author Lars Schmidt, Dominik Wolff
 */
public class GroupReferenceProcessor extends Processor {
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
    public GroupReferenceProcessor(XSDSchema schema) {
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
    protected Particle processNode(Node node) throws XSDParseException {
        // Call the visitChildren method to handle children and find necessary details for the current group
        visitChildren(node);
        QualifiedName groupName = getName(node);

        // List of Attributes
        NamedNodeMap attributes = node.getAttributes();
        if (attributes != null && attributes.getNamedItem("name") != null && attributes.getNamedItem("ref") != null) {
            throw new ExclusiveAttributesException("name and ref", groupName);
        }
        if (attributes != null && attributes.getNamedItem("ref") != null) {
            String refValue = ((Attr) attributes.getNamedItem("ref")).getValue();
            if (!isQName(refValue)) {
                throw new InvalidQNameException(refValue, "group");
            }

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

            QualifiedName refName = getName(refValue);
            
            GroupReference groupRef = new GroupReference(refName);
            groupRef.setAnnotation(annotation);
            if (idString != null) {
                groupRef.setId(idString);
            }
            
            if (maxOccursValue == null || minOccursValue != 1 || maxOccursValue != 1) {
                CountingPattern countingPattern = new CountingPattern(groupRef, minOccursValue, maxOccursValue);
                return countingPattern;
            }

            return groupRef;
        } else
        	throw new XSDParseException("Not a group reference");
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
    protected void processChild(Node childNode) throws XSDParseException {
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
                        throw new MultipleAnnotationException("group");
                    }
                    break;
                default:
                    throw new UnsupportedContentException(nodeName, "group-reference");
            }
        }
    }
}
