/**
 * Copyright 2009-2012 TU Dortmund
 *
 * This file is part of FoXLib.
 *
 * FoXLib is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * FoXLib is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with BonXai.  If not, see <http://www.gnu.org/licenses/>.
 */

package eu.fox7.schematoolkit.xsd.parser;

import eu.fox7.schematoolkit.common.*;
import eu.fox7.schematoolkit.xsd.om.*;
import eu.fox7.schematoolkit.xsd.parser.exceptions.XSDParseException;
import eu.fox7.schematoolkit.xsd.parser.exceptions.attribute.*;
import eu.fox7.schematoolkit.xsd.parser.exceptions.attribute.countingpattern.*;
import eu.fox7.schematoolkit.xsd.parser.exceptions.content.*;

import org.w3c.dom.*;

/*******************************************************************************
 * Processor for the handling of an choice-tag in XSD
 *******************************************************************************
 * @author Lars Schmidt, Dominik Wolff
 */
class ChoiceProcessor extends Processor {

    // ChoicePattern created by this processor
    private ChoicePattern choicePattern = new ChoicePattern();
    // Values of minOccurs and maxOccurs
    private Integer minOccursValue = 1, maxOccursValue = 1;

    /**
     * This is the constructor of the class ChoiceProcessor.
     * @param schema
     */
    public ChoiceProcessor(XSDSchema schema) {
        super(schema);
    }

    /**
     * The method processNode handles all attributes and properties of
     * the current choice-tag. The given properties are checked against the allowed
     * values defined in the XSD primer.
     *
     * <choice
     * id = ID
     * maxOccurs = (nonNegativeInteger | unbounded)  : 1
     * minOccurs = nonNegativeInteger : 1
     * {any attributes with non-schema namespace . . .}>
     * Content: (annotation?, (element | group | choice | sequence | any)*)
     * </choice>
     *
     * The content is managed by the processChild-method below.
     * @param node
     * @return ParticleContainer
     * @throws Exception
     */
    @Override
    protected Particle processNode(Node node) throws XSDParseException {
        // Call the visitChildren method to handle children and find necessary details for the current choicePattern
        visitChildren(node);
        // List of Attributes
        NamedNodeMap attributes = node.getAttributes();
        if (attributes != null) {
            if (attributes.getNamedItem("id") != null) {
                if (node.getAttributes().getNamedItem("id").getNodeValue().equals("")) {
                    throw new EmptyIdException("Choice");
                }
                choicePattern.setId(((Attr) attributes.getNamedItem("id")).getValue());
            }
            // maxOccurs = (nonNegativeInteger | unbounded)  : 1
            if (attributes.getNamedItem("maxOccurs") != null && !((Attr) attributes.getNamedItem("maxOccurs")).getValue().equals("")) {
                maxOccursValue = (((Attr) attributes.getNamedItem("maxOccurs")).getValue().equals("unbounded") ? null : Integer.parseInt(((Attr) attributes.getNamedItem("maxOccurs")).getValue()));
                // The maxOccurs-property is not allowed to be negative.
                if (maxOccursValue != null && maxOccursValue < 0) {
                    throw new CountingPatternMaxOccursIllegalValueException("choice");
                }
            }
            if (attributes.getNamedItem("minOccurs") != null && !((Attr) attributes.getNamedItem("minOccurs")).getValue().equals("")) {
                if (((Attr) attributes.getNamedItem("minOccurs")).getValue().equals("unbounded")) {
                    // The minOccurs-property is not allowed to have the value "unbounded".
                    throw new CountingPatternMinOccursIllegalValueException("choice");
                } else {
                    minOccursValue = Integer.parseInt(((Attr) attributes.getNamedItem("minOccurs")).getValue());
                }
                // The minOccurs-property is not allowed to be negative and minOccursValue always must be smaller than the maxOccursValue.
                if (minOccursValue < 0) {
                    throw new CountingPatternMinOccursIllegalValueException("choice");
                }
                if ((maxOccursValue != null && minOccursValue > maxOccursValue)) {
                    throw new CountingPatternMinOccursGreaterThanMaxOccursException("choice");
                }
            }
        }

        if (maxOccursValue == null || minOccursValue != 1 || maxOccursValue != 1) {
            CountingPattern countingPattern = new CountingPattern(choicePattern, minOccursValue, maxOccursValue);
            return countingPattern;
        }
        return choicePattern;
    }

    /**
     * This method manages the possible content af the current choice-tag.
     *
     * Content: (annotation?, (element | group | choice | sequence | any)*)
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
            // All types of content-tags are allowed to exist zero or more times.
            // So here is no check for cardinality constraint necessary.

            // The annotation can be added once or not at all
            switch (CASE.valueOf(nodeName.toUpperCase())) {
                case ELEMENT:
                    if (getDebug()) {
                        System.out.println("element");
                    }
                    ElementProcessor elementProcessor = new ElementProcessor(schema);
                    choicePattern.addParticle(elementProcessor.processNode(childNode));
                    break;
                case GROUP:
                    if (getDebug()) {
                        System.out.println("group");
                    }
                    GroupReferenceProcessor groupRefProcessor = new GroupReferenceProcessor(schema);
                    Object object = groupRefProcessor.processNode(childNode);
                    if (object instanceof Particle) {
                        choicePattern.addParticle((Particle) object);
                    } else {
                        throw new IllegalObjectReturnedException(object.getClass().getName(), "choice");
                    }
                    break;
                case ALL:
                    if (XSDParser.allowInvalidAll) {
                        if (getDebug()) {
                            System.out.println("all");
                        }
                        AllProcessor allProcessor = new AllProcessor(schema);
                        choicePattern.addParticle(allProcessor.processNode(childNode));
                    } else {
                        throw new UnsupportedContentException(nodeName, "choice");
                    }
                    break;
                case CHOICE:
                    if (getDebug()) {
                        System.out.println("choice");
                    }
                    ChoiceProcessor choiceProcessor = new ChoiceProcessor(schema);
                    choicePattern.addParticle(choiceProcessor.processNode(childNode));
                    break;
                case SEQUENCE:
                    if (getDebug()) {
                        System.out.println("sequence");
                    }
                    SequenceProcessor sequenceProcessor = new SequenceProcessor(schema);
                    choicePattern.addParticle(sequenceProcessor.processNode(childNode));
                    break;
                case ANY:
                    if (getDebug()) {
                        System.out.println("any");
                    }
                    AnyProcessor anyProcessor = new AnyProcessor(schema);
                    choicePattern.addParticle(anyProcessor.processNode(childNode));
                    break;
                case ANNOTATION:
                    AnnotationProcessor annotationProcessor = new AnnotationProcessor(schema);
                    if (choicePattern.getAnnotation() == null) {
                        choicePattern.setAnnotation(annotationProcessor.processNode(childNode));
                    } else {
                        throw new MultipleAnnotationException("choice");
                    }
                    break;
                default:
                    throw new UnsupportedContentException(nodeName, "choice");
            }
        }
    }
}
