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
 * Processor for the handling of an attributeGroup-tag in XSD
 *******************************************************************************
 * @author Lars Schmidt, Dominik Wolff
 */
public class AttributeGroupReferenceProcessor extends Processor {

    // Created AttributeGroupReference
    private AttributeGroupReference attributeGroupReference;
    // Id of this AttributeGroup
    private String idString;

    /**
     * This is the constructor of the class AttributeGroupProcessor.
     * @param schema
     */
    public AttributeGroupReferenceProcessor(XSDSchema schema) {
        super(schema);
    }

    /**
     * The method processNode handles all attributes and properties of
     * the current attributeGroup-tag. The given properties are checked against the allowed
     * values defined in the XSD primer.
     *
     * <attributeGroup
     * id = ID
     * name = NCName
     * ref = QName
     * {any attributes with non-schema namespace . . .}>
     * Content: (annotation?, ((attribute | attributeGroup)*, anyAttribute?))
     * </attributeGroup>
     *
     * The content is managed by the processChild-method below.
     *
     * @param node
     * @return Object
     * @throws Exception
     */
    @Override
    protected AttributeGroupReference processNode(Node node) throws XSDParseException {
        visitChildren(node);
        // List of Attributes
        NamedNodeMap attributes = node.getAttributes();

        if (attributes != null && attributes.getNamedItem("ref") != null) {
            String refValue = ((Attr) attributes.getNamedItem("ref")).getValue();
            if (!isQName(refValue)) {
                throw new InvalidQNameException(refValue, "attributeGroup");
            }

            if (attributes.getNamedItem("name") != null) {
                throw new ExclusiveAttributesException("name and ref", getName(node));
            }
            // ref Attribute of AttributeGroup
            QualifiedName refName = getName(refValue);

            return new AttributeGroupReference(refName);
        } else 
        	throw new XSDParseException("AttributeGroupReference without a reference");
    }

    /**
     * This method manages the possible content af the current attributeGroup-tag.
     *
     * Content: (annotation?, ((attribute | attributeGroup)*, anyAttribute?))
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
                    // There can only be one annotation in an attributeGroup
                    if (attributeGroupReference.getAnnotation() == null) {
                        AnnotationProcessor annotationProcessor = new AnnotationProcessor(schema);
                        attributeGroupReference.setAnnotation(annotationProcessor.processNode(childNode));
                    } else {
                        throw new MultipleAnnotationException("attributeGroupReference");
                    }
                    break;
                default:
                    throw new UnsupportedContentException(nodeName, "attributeGroup");
            }
        }
    }
}
