package eu.fox7.bonxai.xsd.parser;

import eu.fox7.bonxai.common.*;
import eu.fox7.bonxai.xsd.*;
import eu.fox7.bonxai.xsd.Element;
import eu.fox7.bonxai.xsd.parser.exceptions.attribute.*;
import eu.fox7.bonxai.xsd.parser.exceptions.attribute.countingpattern.*;
import eu.fox7.bonxai.xsd.parser.exceptions.content.*;

import org.w3c.dom.*;

/*******************************************************************************
 * Processor for the handling of an all-tag in XSD
 *******************************************************************************
 * @author Lars Schmidt, Dominik Wolff
 */
public class AllProcessor extends Processor {

    // AllPattern constructed by this processor
    private AllPattern allPattern = new AllPattern();
    //Values of minOccurs and maxOccurs
    private Integer minOccursValue = 1, maxOccursValue = 1;

    /**
     * This is the constructor of the class AllProcessor.
     * @param schema
     */
    public AllProcessor(XSDSchema schema) {
        super(schema);
    }

    /**
     * The method processNode handles all attributes and properties of
     * the current all-tag. The given properties are checked against the allowed
     * values defined in the XSD primer.
     *
     * <all
     * id = ID
     * maxOccurs = 1 : 1
     * minOccurs = (0 | 1) : 1
     * {any attributes with non-schema namespace . . .}>
     * Content: (annotation?, element*)
     * </all>
     *
     * The content is managed by the processChild-method below.
     * @param node
     * @return ParticleContainer
     * @throws Exception
     */
    @Override
    protected ParticleContainer processNode(Node node) throws Exception {
        // Call the visitChildren method to handle children and find necessary
        // details for the current allPattern
        visitChildren(node);
        // List of Attributes
        NamedNodeMap attributes = node.getAttributes();
        if (attributes != null) {
            if (attributes.getNamedItem("id") != null) {
                if (node.getAttributes().getNamedItem("id").getNodeValue().equals("")) {
                    throw new EmptyIdException("all");
                }
                allPattern.setId(((Attr) attributes.getNamedItem("id")).getValue());
            }

            // If invalid alls are not allowed minOccurs and maxOccurs must be checked
            if (!XSDParser.allowInvalidAll) {
                // maxOccurs = 1 : 1
                if (attributes.getNamedItem("maxOccurs") != null && !((Attr) attributes.getNamedItem("maxOccurs")).getValue().equals("")) {
                    if (!((Attr) attributes.getNamedItem("maxOccurs")).getValue().equals("1")) {
                        // The maxOccurs-property is only allowed to have the value "1".
                        throw new CountingPatternMaxOccursNotAllowedValueException("all");
                    } else {
                        maxOccursValue = Integer.parseInt(((Attr) attributes.getNamedItem("maxOccurs")).getValue());
                    }
                }
                // minOccurs = (0 | 1) : 1
                if (attributes.getNamedItem("minOccurs") != null && !((Attr) attributes.getNamedItem("minOccurs")).getValue().equals("")) {
                    if (!(((Attr) attributes.getNamedItem("minOccurs")).getValue().equals("0")
                            || ((Attr) attributes.getNamedItem("minOccurs")).getValue().equals("1"))) {
                        // The minOccurs-property is not allowed to have an other value than zero or one.
                        throw new CountingPatternMinOccursIllegalValueException("all");
                    } else {
                        minOccursValue = Integer.parseInt(((Attr) attributes.getNamedItem("minOccurs")).getValue());
                    }
                }
            } else {
                if (attributes.getNamedItem("maxOccurs") != null && !((Attr) attributes.getNamedItem("maxOccurs")).getValue().equals("")) {
                    maxOccursValue = Integer.parseInt(((Attr) attributes.getNamedItem("maxOccurs")).getValue());
                }
                if (attributes.getNamedItem("minOccurs") != null && !((Attr) attributes.getNamedItem("minOccurs")).getValue().equals("")) {
                    minOccursValue = Integer.parseInt(((Attr) attributes.getNamedItem("minOccurs")).getValue());
                }
            }
        }

        if (minOccursValue != 1 || maxOccursValue != 1) {
            CountingPattern countingPattern = new CountingPattern(minOccursValue, maxOccursValue);
            countingPattern.addParticle(allPattern);
            return countingPattern;
        }
        return allPattern;
    }

    /**
     * This method manages the possible content af the current all-tag.
     *
     * Content: (annotation?, element*)
     *
     * It is necessary to check if there are more than one element with the same
     * name. This is not allowed in the XSD-specification.
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
                    if (XSDParser.allowInvalidAll) {
                        if (getDebug()) {
                            System.out.println("all");
                        }
                        AllProcessor allProcessor = new AllProcessor(schema);
                        allPattern.addParticle(allProcessor.processNode(childNode));
                    } else {
                        throw new UnsupportedContentException(nodeName, "all");
                    }
                    break;
                case CHOICE:
                    if (XSDParser.allowInvalidAll) {
                        if (getDebug()) {
                            System.out.println("choice");
                        }
                        ChoiceProcessor choiceProcessor = new ChoiceProcessor(schema);
                        allPattern.addParticle(choiceProcessor.processNode(childNode));
                    } else {
                        throw new UnsupportedContentException(nodeName, "all");
                    }
                    break;
                case SEQUENCE:
                    if (XSDParser.allowInvalidAll) {
                        if (getDebug()) {
                            System.out.println("sequence");
                        }
                        SequenceProcessor sequenceProcessor = new SequenceProcessor(schema);
                        allPattern.addParticle(sequenceProcessor.processNode(childNode));
                    } else {
                        throw new UnsupportedContentException(nodeName, "all");
                    }
                    break;
                case ELEMENT:
                    if (getDebug()) {
                        System.out.println("element");
                    }
                    ElementProcessor elementProcessor = new ElementProcessor(schema);
                    Particle particle = elementProcessor.processNode(childNode);
                    String particleElementName = "";
                    // There must not be two elements with the same name under
                    // the "all"-tag
                    if (!allPattern.getParticles().isEmpty()) {
                        for (Particle currentParticle : allPattern.getParticles()) {
                            particleElementName = checkParticlesForSameElement(currentParticle, particle);
                            if (!particleElementName.equals("")) {
                                throw new SameElementUnderAllException(particleElementName);
                            }
                        }
                    }
                    // Every CountingPattern under the "all"-Tag has to have the value zero or one for min- and maxOccurs)
                    if (particle instanceof CountingPattern) {
                        CountingPattern countingPattern = (CountingPattern) particle;

                        if ((countingPattern.getMax() == null) || !((countingPattern.getMin() == 0 || countingPattern.getMin() == 1) && (countingPattern.getMax() == 0 || countingPattern.getMax() == 1))) {
                            throw new CountingPatternNotAllowedValueException("child of all");
                        }
                    }
                    allPattern.addParticle(particle);
                    break;
                case GROUP:
                    if (XSDParser.allowInvalidAll) {
                        if (getDebug()) {
                            System.out.println("group");
                        }
                        GroupProcessor groupProcessor = new GroupProcessor(schema);
                        allPattern.addParticle((Particle) groupProcessor.processNode(childNode));
                    } else {
                        throw new UnsupportedContentException(nodeName, "all");
                    }
                    break;
                case ANNOTATION:
                    AnnotationProcessor annotationProcessor = new AnnotationProcessor(schema);
                    if (allPattern.getAnnotation() == null) {
                        allPattern.setAnnotation(annotationProcessor.processNode(childNode));
                    } else {
                        throw new MultipleAnnotationException("all");
                    }
                    break;
                default:
                    throw new UnsupportedContentException(nodeName, "all");
            }
        }
    }

    /**
     * This method returns the name of the element, which is contained in both
     * of the two particles given in the parameterlist. Otherwise it returns the
     * empty String ""
     * This is only usefull for this all-processor.
     *
     * Only elements, elementRefs, and countingPatterns are supported.

     * @param particle
     * @return String
     */
    protected String checkParticlesForSameElement(Particle particleOne, Particle particleTwo) {
        String returnValue = "";
        Element elementOne = getElementFromParticle(particleOne);
        Element elementTwo = getElementFromParticle(particleTwo);
        if (elementOne != null && elementTwo != null) {
            if (elementOne.getName().equals(elementTwo.getName())) {
                returnValue = elementOne.getName();
            }
        }
        return returnValue;
    }

    /**
     * This method extracts an element from an particle.
     * This is only usefull for this all-processor.
     *
     * Only elements, elementRefs, and countingPatterns are supported.

     * @param particle
     * @return Element
     */
    protected Element getElementFromParticle(Particle particle) {
        if (particle instanceof Element) {
            return ((Element) particle);
        } else if (particle instanceof ElementRef) {
            return ((ElementRef) particle).getElement();
        } else if (particle instanceof CountingPattern) {
            Particle innerParticle = ((CountingPattern) particle).getParticles().getFirst();
            if (innerParticle instanceof Element) {
                return (Element) innerParticle;
            } else {
                return ((ElementRef) innerParticle).getElement();
            }
        }
        return null;
    }
}
