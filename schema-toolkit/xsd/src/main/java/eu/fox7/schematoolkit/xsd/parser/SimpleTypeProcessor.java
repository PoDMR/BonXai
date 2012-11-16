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
import eu.fox7.schematoolkit.xsd.parser.exceptions.inheritance.*;

import java.util.HashSet;
import org.w3c.dom.*;

/*******************************************************************************
 * Processor for the handling of an simpletype-tag in XSD
 *******************************************************************************
 * @author Lars Schmidt, Dominik Wolff
 */
public class SimpleTypeProcessor extends Processor {

    // Created simpleType
    private SimpleType simpleType;
    // Inhertance of this simpleType
    private SimpleTypeInheritance inheritance;
    // Annotation of this simpleType
    private Annotation annotation;

    /**
     * This is the constructor of the class SimpleTypeProcessor.
     * @param schema
     */
    public SimpleTypeProcessor(XSDSchema schema) {
        super(schema);
    }

    /**
     * The method processNode handles all attributes and properties of
     * the current simpletype-tag. The given properties are checked against the
     * allowed values defined in the XSD primer.
     *
     * <simpleType
     * final = (#all | List of (list | union | restriction))
     * id = ID
     * name = NCName
     * {any attributes with non-schema namespace . . .}>
     * Content: (annotation?, (restriction | list | union))
     * </simpleType>
     *
     * The content is managed by the processChild-method.
     *
     * @param node
     * @return SymbolTableRef<Type>
     * @throws Exception
     */
    @Override
    protected QualifiedName processNode(Node node) throws XSDParseException {

        // Call the visitChildren method to handle children and find necessary details for the current simpleType
        visitChildren(node);

        // Generate XSD simpleType object with regards to the inheritance
        if (inheritance == null) {
            throw new MissingSimpleTypeInheritanceException(this.getName(node));
        } else {
            // Create simpleType

            if (node.getAttributes().getNamedItem("name") != null && !node.getAttributes().getNamedItem("name").getNodeValue().equals("")) {
                this.simpleType = new SimpleType(this.getName(node), this.inheritance);
            } else {
                // Type is an anonymous type
                this.simpleType = new SimpleType(this.getUniqueName(), this.inheritance, true);
            }

            simpleType.setAnnotation(annotation);

            // final attribute handling
            if (node.getAttributes().getNamedItem("final") != null) {
                String finalModifierString = node.getAttributes().getNamedItem("final").getNodeValue();
                if (!finalModifierString.equals("")) {
                    String[] finalModifierArray = finalModifierString.split(" ");
                    for (String currentModifier : finalModifierArray) {
                        if (currentModifier.toUpperCase().equals(SimpleTypeInheritanceModifier.Restriction.name().toUpperCase())) {
                            this.simpleType.addFinalModifier(SimpleTypeInheritanceModifier.Restriction);
                        } else {
                            if (currentModifier.toUpperCase().equals(SimpleTypeInheritanceModifier.List.name().toUpperCase())) {
                                this.simpleType.addFinalModifier(SimpleTypeInheritanceModifier.List);
                            } else {
                                if (currentModifier.toUpperCase().equals(SimpleTypeInheritanceModifier.Union.name().toUpperCase())) {
                                    this.simpleType.addFinalModifier(SimpleTypeInheritanceModifier.Union);
                                } else {
                                    if (currentModifier.toUpperCase().equals("#all".toUpperCase())) {
                                        this.simpleType.addFinalModifier(SimpleTypeInheritanceModifier.Restriction);
                                        this.simpleType.addFinalModifier(SimpleTypeInheritanceModifier.List);
                                        this.simpleType.addFinalModifier(SimpleTypeInheritanceModifier.Union);
                                    } else {
                                        throw new InvalidFinalValueException(simpleType.getName());
                                    }
                                }
                            }
                        }
                    }
                } else {
                    this.simpleType.setFinalModifiers(new HashSet<SimpleTypeInheritanceModifier>());
                }
            }
            if (node.getAttributes().getNamedItem("id") != null) {
                if (node.getAttributes().getNamedItem("id").getNodeValue().equals("")) {
                    throw new EmptyIdException("SimpleType: " + simpleType.getName());
                }
                this.simpleType.setId(node.getAttributes().getNamedItem("id").getNodeValue());
            }
        }
        
        this.schema.addType(simpleType);
        return simpleType.getName();
    }

    /**
     * This method manages the possible child-content af the current simpleType
     *
     * Content: (annotation?, (restriction | list | union))
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
                case RESTRICTION:
                    if (getDebug()) {
                        System.out.println("restriction");
                    }
                    // There must not be an inheritance before we add this one here.
                    if (this.inheritance == null) {
                        RestrictionProcessor restrictionProcessor = new RestrictionProcessor(schema);
                        this.inheritance = (SimpleContentRestriction) restrictionProcessor.processNode(childNode);
                    } else {
                        throw new SimpleTypeMultipleInheritanceException("simpleType: restriction");
                    }
                    break;
                case LIST:
                    if (getDebug()) {
                        System.out.println("list");
                    }
                    // There must not be an inheritance before we add this one here.
                    if (this.inheritance == null) {
                        ListProcessor listProcessor = new ListProcessor(schema);
                        this.inheritance = listProcessor.processNode(childNode);
                    } else {
                        throw new SimpleTypeMultipleInheritanceException("simpleType: list");
                    }
                    break;
                case UNION:
                    if (getDebug()) {
                        System.out.println("union");
                    }
                    // There must not be an inheritance before we add this one here.
                    if (this.inheritance == null) {
                        UnionProcessor unionProcessor = new UnionProcessor(schema);
                        this.inheritance = unionProcessor.processNode(childNode);
                    } else {
                        throw new SimpleTypeMultipleInheritanceException("simpleType: union");
                    }
                    break;
                case ANNOTATION:
                    if (getDebug()) {
                        System.out.println("annotation");
                    }
                    if (annotation == null) {
                        AnnotationProcessor annotationProcessor = new AnnotationProcessor(schema);
                        annotation = annotationProcessor.processNode(childNode);
                    } else {
                        throw new MultipleAnnotationException("simpleType");
                    }
                    break;
                default:
                    throw new UnsupportedContentException(nodeName, "simpleType");
            }
        }
    }
}
