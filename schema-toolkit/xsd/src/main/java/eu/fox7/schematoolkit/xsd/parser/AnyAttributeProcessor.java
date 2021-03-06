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
import eu.fox7.schematoolkit.xsd.parser.exceptions.content.*;

import org.w3c.dom.*;

/*******************************************************************************
 * Processor for the handling of an anyAttribute-tag in XSD
 *******************************************************************************
 * @author Lars Schmidt, Dominik Wolff
 */
class AnyAttributeProcessor extends Processor {

    // Value of the processcontent attribute
    private ProcessContentsInstruction processContentsInstruction = ProcessContentsInstruction.STRICT;

    // Value of namespace and id attributes
    private String namespace, idString;

    // Annotation of this anyAttribute
    private Annotation annotation;

    /**
     * This is the constructor of the class AnyAttributeProcessor.
     * @param schema
     */
    public AnyAttributeProcessor(XSDSchema schema) {
        super(schema);
    }

    /**
     * The method processNode handles all attributes and properties of
     * the current "anyAttribute"-tag. The given properties are checked against the allowed
     * values defined in the XSD primer.
     *
     * <anyAttribute
     * id = ID
     * namespace = ((##any | ##other) | List of (anyURI | (##targetNamespace |
     *              ##local)) )  : ##any
     * processContents = (lax | skip | strict) : strict
     * {any attributes with non-schema namespace . . .}>
     * Content: (annotation?)
     * </anyAttribute>
     *
     * The content is managed by the processChild-method below.
     *
     * @param node
     * @return AnyAttribute
     * @throws Exception
     */
    @Override
    protected AnyAttribute processNode(Node node) throws XSDParseException {
        // List of Attributes
        NamedNodeMap attributes = node.getAttributes();
        if (attributes != null) {
            // ID Attribute of AnyAttribute
            if (attributes.getNamedItem("id") != null) {
                if (node.getAttributes().getNamedItem("id").getNodeValue().equals("")) {
                    throw new EmptyIdException("anyAttribute");
                }
                idString = ((Attr) attributes.getNamedItem("id")).getValue();
            }
            // processContents Attribute of AnyAttribute
            if (attributes.getNamedItem("processContents") != null) {
                if (((Attr) attributes.getNamedItem("processContents")).getValue().equals("strict")) {
                    processContentsInstruction = ProcessContentsInstruction.STRICT;
                } else if (((Attr) attributes.getNamedItem("processContents")).getValue().equals("lax")) {
                    processContentsInstruction = ProcessContentsInstruction.LAX;
                } else if (((Attr) attributes.getNamedItem("processContents")).getValue().equals("skip")) {
                    processContentsInstruction = ProcessContentsInstruction.SKIP;
                } else {
                    throw new InvalidProcessContentsValueException("anyAttribute");
                }
            }
            // namespace Attribute of AnyAttribute
            if (attributes.getNamedItem("namespace") != null) {
                String namespaceValue = ((Attr) attributes.getNamedItem("namespace")).getValue();
                if (namespaceValue.equals("##any") || namespaceValue.equals("##other") || namespaceValue.equals("##local") || namespaceValue.equals("##targetNamespace")) {
                    namespace = namespaceValue;
                } else {
                    String[] namespaceValueArray = namespaceValue.split(" ");
                    for (String currentNamespace : namespaceValueArray) {
                        if (!currentNamespace.equals("##local") && !currentNamespace.equals("##targetNamespace") && !isAnyUri(currentNamespace)) {
                            throw new InvalidNamespaceValueException("anyAttribute");
                        }
                    }
                    namespace = namespaceValue;
                }
            }
        }
        
        visitChildren(node);
        AnyAttribute anyAttribute = new AnyAttribute(processContentsInstruction, namespace);
        if (idString != null) {
            anyAttribute.setId(idString);
        }
        anyAttribute.setAnnotation(annotation);
        return anyAttribute;
    }

    /**
     * This method manages the possible content af the current "anyAttribute"-tag.
     *
     * Content: (annotation?)
     *
     * @param childNode
     * @throws Exception
     */
    @Override
    protected void processChild(Node childNode) throws XSDParseException {

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
                        throw new MultipleAnnotationException("anyAttribute");
                    }
                    break;
                default:
                    throw new UnsupportedContentException(nodeName, "anyattribute");
            }
        }
    }
}
