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
import eu.fox7.schematoolkit.xsd.om.Group;
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
public class GroupProcessor extends Processor {

    // Content of this group
    private Particle container;
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
    protected Group processNode(Node node) throws XSDParseException {
        // Call the visitChildren method to handle children and find necessary details for the current group
        visitChildren(node);
        QualifiedName groupName = getName(node);

        // List of Attributes
        NamedNodeMap attributes = node.getAttributes();
        if (attributes != null && attributes.getNamedItem("name") != null && attributes.getNamedItem("ref") != null) {
            throw new ExclusiveAttributesException("name and ref", groupName);
        }


        // Generate XSD group object

        Group group = new Group(groupName, container);
        group.setAnnotation(annotation);

        // The following part sets the ID of a group.
        // This is necessary at this point, because the Setting of the ID above is only for groupRefs.
        if (node.getAttributes().getNamedItem("id") != null) {
            if (node.getAttributes().getNamedItem("id").getNodeValue().equals("")) {
                throw new EmptyIdException("group");
            }
            group.setId(node.getAttributes().getNamedItem("id").getNodeValue());
        }
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
    protected void processChild(Node childNode) throws XSDParseException {
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
