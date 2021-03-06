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

import eu.fox7.schematoolkit.common.QualifiedName;
import eu.fox7.schematoolkit.xsd.om.*;
import eu.fox7.schematoolkit.xsd.parser.exceptions.XSDParseException;
import eu.fox7.schematoolkit.xsd.parser.exceptions.attribute.*;
import eu.fox7.schematoolkit.xsd.parser.exceptions.constraint.*;
import eu.fox7.schematoolkit.xsd.parser.exceptions.content.*;

import org.w3c.dom.*;

/*******************************************************************************
 * Class processes a unique constraint for the parser and returns a unique object
 * of the XML-XSDSchema object model to the caller class.
 *******************************************************************************
 * @author Lars Schmidt, Dominik Wolff
 */
public class UniqueProcessor extends Processor {

    // Unique object with selector and fields created by this class
    private Unique unique;

    // Name of the above unique object
    private QualifiedName uniqueName;

    /**
     * Constructor of the UniqueProcessor, which receives only the schema.
     * @param schema    New schema created by the parser and its processors
     */
    public UniqueProcessor(XSDSchema schema) {
        super(schema);
    }

    /**
     * Visits a child of the unique node and processes it according to its name
     * @param childNode     Node in the Dom tree below the Node labeled with key
     * @throws java.lang.Exception
     */
    @Override
    protected void processChild(Node childNode) throws XSDParseException {

        // Tests if the node name is a local name and filters nodes with names #text, #comment and #document who are not in the enum
        String nodeName = childNode.getNodeName();
        if (nodeName.contains(":")) {
            nodeName = nodeName.split(":")[1];
        }
        NamedNodeMap attributes;
        if (childNode != null && !"#text".equals(nodeName) && !"#comment".equals(nodeName) && !"#document".equals(nodeName)) {
            switch (CASE.valueOf(nodeName.toUpperCase())) {
                case SELECTOR:
                    if (getDebug()) {
                        System.out.println("selector");
                    }
                    // If the selector is already set an exception is thrown
                    if (!unique.getSelector().equals("")) {
                        throw new DuplicateSelectorException(unique);
                    }
                    // The selector of a unique constraint is not allowed to be empty
                    attributes = childNode.getAttributes();
                    if (attributes != null && attributes.getNamedItem("xpath") != null && !attributes.getNamedItem("xpath").getNodeValue().equals("")) {
                        unique.setSelector(attributes.getNamedItem("xpath").getNodeValue());
                    } else {
                        throw new EmptySelectorException(unique);
                    }
                    break;
                case FIELD:
                    if (getDebug()) {
                        System.out.println("field");
                    }
                    // The field too is not allowed to be empty
                    attributes = childNode.getAttributes();
                    if (attributes != null && attributes.getNamedItem("xpath") != null && !attributes.getNamedItem("xpath").getNodeValue().equals("")) {
                        unique.addField(attributes.getNamedItem("xpath").getNodeValue());
                    } else {
                        throw new EmptyFieldException(unique);
                    }
                    break;
                case ANNOTATION:
                    if (unique.getAnnotation() == null) {
                        AnnotationProcessor annotationProcessor = new AnnotationProcessor(schema);
                        unique.setAnnotation(annotationProcessor.processNode(childNode));
                    } else {
                        throw new MultipleAnnotationException("unique");
                    }
                    break;
                default:
                    throw new UnsupportedContentException(nodeName, "unique");
            }
        }
    }

    /**
     * Creates a unique object corresponding to the unique node in the Dom tree.
     * @param node      Node labeled with unique in the Dom tree
     * @return unique object representing a unique constraint in XML XSDSchema
     * @throws java.lang.Exception
     */
    @Override
    protected Unique processNode(Node node) throws XSDParseException {

        // Creates the unique object and visits all children to set selector and fields
        uniqueName = getName(node);
        unique = new Unique(uniqueName, "");
        visitChildren(node);

        // For every unique a selector has to be present. If not an exception is thrown.
        if (unique.getSelector().equals("")) {
            throw new MissingSelectorException(unique);
        }
        // A unique object has to contain at least one field
        if (unique.getFields().isEmpty()) {
            throw new MissingFieldException(unique);
        }
        if (node.getAttributes().getNamedItem("id") != null) {
            if (node.getAttributes().getNamedItem("id").getNodeValue().equals("")) {
                throw new EmptyIdException("Unique");
            }
            unique.setId(node.getAttributes().getNamedItem("id").getNodeValue());
        }
        return unique;
    }
}