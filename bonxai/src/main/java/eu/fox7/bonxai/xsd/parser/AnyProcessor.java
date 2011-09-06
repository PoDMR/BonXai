package eu.fox7.bonxai.xsd.parser;

import eu.fox7.bonxai.common.*;
import eu.fox7.bonxai.xsd.*;
import eu.fox7.bonxai.xsd.parser.exceptions.attribute.*;
import eu.fox7.bonxai.xsd.parser.exceptions.attribute.countingpattern.*;
import eu.fox7.bonxai.xsd.parser.exceptions.content.*;

import org.w3c.dom.*;

/*******************************************************************************
 * Processor for the handling of an any-tag in XSD
 *******************************************************************************
 * @author Lars Schmidt, Dominik Wolff
 */
class AnyProcessor extends Processor {

    // Value of the processContentInstruction attribute
    private ProcessContentsInstruction processContentsInstruction = ProcessContentsInstruction.Strict;

    // Namespace and id attributes
    private String namespace, idString;

    // Values of minOccurs and maxOccurs
    private Integer minOccursValue = 1, maxOccursValue = 1;

    // Annotation of this any
    private Annotation annotation;

    /**
     * This is the constructor of the class AnyProcessor.
     * @param schema
     */
    public AnyProcessor(XSDSchema schema) {
        super(schema);
    }


    /**
     * The method processNode handles all attributes and properties of
     * the current "any"-tag. The given properties are checked against the allowed
     * values defined in the XSD primer.
     *
     * <any
     * id = ID
     * maxOccurs = (nonNegativeInteger | unbounded)  : 1
     * minOccurs = nonNegativeInteger : 1
     * namespace = ((##any | ##other) | List of (anyURI | (##targetNamespace |
     *              ##local)) )  : ##any
     * processContents = (lax | skip | strict) : strict
     * {any attributes with non-schema namespace . . .}>
     * Content: (annotation?)
     * </any>
     *
     * The content is managed by the processChild-method below.
     *
     * @param node
     * @return Particle
     * @throws Exception
     */
    @Override
    protected Particle processNode(Node node) throws Exception {

        visitChildren(node);

        // List of Attributes
        NamedNodeMap attributes = node.getAttributes();
        if (attributes != null) {
            if (attributes.getNamedItem("id") != null) {
                if (node.getAttributes().getNamedItem("id").getNodeValue().equals("")) {
                    throw new EmptyIdException("any");
                }
                idString = ((Attr) attributes.getNamedItem("id")).getValue();
            }
            if (attributes.getNamedItem("maxOccurs") != null && !((Attr) attributes.getNamedItem("maxOccurs")).getValue().equals("")) {
                maxOccursValue = (((Attr) attributes.getNamedItem("maxOccurs")).getValue().equals("unbounded") ? null : Integer.parseInt(((Attr) attributes.getNamedItem("maxOccurs")).getValue()));
                if (maxOccursValue != null && maxOccursValue < 0) {
                    // A negative value for maxOccurs is invalid.
                    throw new CountingPatternMaxOccursIllegalValueException("any");
                }
            }
            if (attributes.getNamedItem("minOccurs") != null && !((Attr) attributes.getNamedItem("minOccurs")).getValue().equals("")) {
                if (((Attr) attributes.getNamedItem("minOccurs")).getValue().equals("unbounded")) {
                    // The minOccurs-property is not allowed to have the value "unbounded".
                    throw new CountingPatternMinOccursIllegalValueException("any");
                } else {
                    minOccursValue = Integer.parseInt(((Attr) attributes.getNamedItem("minOccurs")).getValue());
                }
                // The minOccurs-property is not allowed to be negative and minOccursValue always must be smaller than the maxOccursValue.
                if (minOccursValue < 0) {
                    throw new CountingPatternMinOccursIllegalValueException("any");
                }
                if ((maxOccursValue != null && minOccursValue > maxOccursValue)) {
                    throw new CountingPatternMinOccursGreaterThanMaxOccursException("any");
                }
            }
            // processContents Attribute of Any
            if (attributes.getNamedItem("processContents") != null) {
                if (((Attr) attributes.getNamedItem("processContents")).getValue().equals("strict")) {
                    processContentsInstruction = ProcessContentsInstruction.Strict;
                } else if (((Attr) attributes.getNamedItem("processContents")).getValue().equals("lax")) {
                    processContentsInstruction = ProcessContentsInstruction.Lax;
                } else if (((Attr) attributes.getNamedItem("processContents")).getValue().equals("skip")) {
                    processContentsInstruction = ProcessContentsInstruction.Skip;
                } else {
                    throw new InvalidProcessContentsValueException("any");
                }
            }
            // namespace Attribute of Any
            if (attributes.getNamedItem("namespace") != null) {
                String namespaceValue = ((Attr) attributes.getNamedItem("namespace")).getValue();
                if (namespaceValue.equals("##any") || namespaceValue.equals("##other") || namespaceValue.equals("##local") || namespaceValue.equals("##targetNamespace")) {
                    namespace = namespaceValue;
                } else {
                    String[] namespaceValueArray = namespaceValue.split(" ");
                    for (String currentNamespace : namespaceValueArray) {
                        if (!currentNamespace.equals("##local") && !currentNamespace.equals("##targetNamespace") && !isAnyUri(currentNamespace)) {
                            throw new InvalidNamespaceValueException("any");
                        }
                    }
                    namespace = namespaceValue;
                }
            }else{
                namespace = "##any";
            }
        }
        // If the countingpattern is valid, put the any-element into a countingPattern container.
        // This will be returned instead of only the direct any-element.
        if (maxOccursValue == null || minOccursValue != 1 || maxOccursValue != 1) {
            CountingPattern countingPattern = new CountingPattern(minOccursValue, maxOccursValue);
            AnyPattern anyPattern = new AnyPattern(processContentsInstruction, namespace);
            if (idString != null) {
    		anyPattern.setId(idString);
            }
            anyPattern.setAnnotation(annotation);
            countingPattern.addParticle(anyPattern);
            return countingPattern;
        }
        AnyPattern anyPattern = new AnyPattern(processContentsInstruction, namespace);
        if (idString != null) {
            anyPattern.setId(idString);
        }
        anyPattern.setAnnotation(annotation);
        return anyPattern;
    }

    /**
     * This method manages the possible content af the current "any"-tag.
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
                        throw new MultipleAnnotationException("any");
                    }
                    break;
                default:
                    throw new UnsupportedContentException(nodeName, "any");
            }
        }
    }
}