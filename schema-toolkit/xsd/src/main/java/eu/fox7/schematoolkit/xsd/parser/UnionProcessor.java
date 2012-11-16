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
import eu.fox7.schematoolkit.xsd.parser.exceptions.type.*;

import java.util.*;
import org.w3c.dom.*;

/*******************************************************************************
 * Processor for the handling of an union-tag in XSD
 *******************************************************************************
 * @author Lars Schmidt, Dominik Wolff
 */
public class UnionProcessor extends Processor {

    // SimpleTypes which are used in this union
    private LinkedList<QualifiedName> simpleTypes = new LinkedList<QualifiedName>();

    // New simpleContentUnion created by this processor
    private SimpleContentUnion simpleContentUnion;
    
    // Annotation of ths union
    private Annotation annotation;

    /**
     * This is the constructor of the class ListProcessor.
     * @param schema
     */
    public UnionProcessor(XSDSchema schema) {
        super(schema);
    }

    /**
     * The method processNode handles all attributes and properties of
     * the current union-tag. The given properties are checked against the
     * allowed values defined in the XSD primer.
     *
     * <union
     * id = ID
     * memberTypes = List of QName
     * {any attributes with non-schema namespace . . .}>
     * Content: (annotation?, simpleType*)
     * </union>
     *
     * The content is managed by the processChild-method.
     *
     * @param node
     * @return SimpleContentList
     * @throws Exception
     */
    @Override
    protected SimpleContentUnion processNode(Node node) throws XSDParseException {
        // Call the visitChildren method to handle children and find necessary details
        visitChildren(node);

        this.simpleContentUnion = new SimpleContentUnion(simpleTypes);
        simpleContentUnion.setAnnotation(annotation);

        if (node.getAttributes() != null && node.getAttributes().getNamedItem("memberTypes") != null) {
            // Check for Attribute "memberTypes" and get the typeRefs from the included simpleTypes
            String memberTypesValue = node.getAttributes().getNamedItem("memberTypes").getNodeValue();
            String[] typeRefNameArray = memberTypesValue.split(" ");
            for (int i = 0; i < typeRefNameArray.length; i++) {
                if (!isQName(typeRefNameArray[i])) {
                    throw new InvalidQNameException(typeRefNameArray[i], "union");
                }
                QualifiedName qualifiedItemName = getName(typeRefNameArray[i]);
                simpleContentUnion.addMemberType(qualifiedItemName);
            }
        }
        // If there is an ID, we can add it to the corresponding object.
        if (node.getAttributes().getNamedItem("id") != null) {
            if (node.getAttributes().getNamedItem("id").getNodeValue().equals("")) {
                throw new EmptyIdException("simpleContentUnion");
            }
            this.simpleContentUnion.setId(node.getAttributes().getNamedItem("id").getNodeValue());
        }

        // The union can combine simpleType-Children and a list of MemberTypes.
        // It is a problem, if there is no MemberType and no simpleType associated with the union.
        if (this.simpleContentUnion.getMemberTypes().isEmpty()) {
            throw new EmptySimpleContentUnionMemberTypesException("simpleContentUnion");
        }

        return this.simpleContentUnion;
    }

    /**
     * This method manages the possible child-content af the current union
     *
     * Content: (annotation?, simpleType*)
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
                case SIMPLETYPE:
                    if (getDebug()) {
                        System.out.println("SimpleType");
                    }
                    // The union can combine simpleType-Children and a list of MemberTypes.
                    // This case handles the simpleType-children.
                    SimpleTypeProcessor simpleTypeProcessor = new SimpleTypeProcessor(schema);
                    this.simpleTypes.add(simpleTypeProcessor.processNode(childNode));
                    break;
                case ANNOTATION:
                    if (getDebug()) {
                        System.out.println("annotation");
                    }
                    if (annotation == null) {
                        AnnotationProcessor annotationProcessor = new AnnotationProcessor(schema);
                        annotation = annotationProcessor.processNode(childNode);
                    } else {
                        throw new MultipleAnnotationException("union");
                    }
                    break;
                default:
                    throw new UnsupportedContentException(nodeName, "union");
            }
        }
    }
}
