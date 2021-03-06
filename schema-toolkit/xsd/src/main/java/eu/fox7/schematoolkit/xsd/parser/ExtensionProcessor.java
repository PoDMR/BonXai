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

import org.w3c.dom.*;

/*******************************************************************************
 * Processor for the handling of an extension-tag in XSD
 *******************************************************************************
 * @author Lars Schmidt, Dominik Wolff
 */
class ExtensionProcessor extends Processor {

    // Inheritance of this Extension
    private Inheritance inheritance;

    // Content of the extension which is added to the base (Sequence of base and
    // extension content)
    private Particle particle;

    // True if complexContentExtension
    private boolean isComplexContentExtension = false;

    // Id of this extension
    private String idString;

    // Annotation of this extension
    private Annotation annotation;

    // True if an attribute was read in the content, only attributes are allowed
    // to follow
    private boolean alreadyAnAttributeAdded = false;

    /**
     * This is the constructor of the class ExtensionProcessor.
     * @param schema
     */
    public ExtensionProcessor(XSDSchema schema) {
        super(schema);
    }

    /**
     * This method manages the possible child-content af the current extension
     *
     * Under a SimpleContent:
     * Content: (annotation?, ((attribute | attributeGroup)*, anyAttribute?))
     *
     * Under a ComplexContent:
     * Content: (annotation?, ((group | all | choice | sequence)?, ((attribute |
     *          attributeGroup)*, anyAttribute?)))
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
                // Only for ComplexTypes
                case GROUP:
                    if (this.isComplexContentExtension) {
                        if (getDebug()) {
                            System.out.println("group");
                        }
                        // Attributes have to be the last elements in the content model
                        if (alreadyAnAttributeAdded) {
                            throw new AttributeIsNotLastInContentModelException("group to extension");
                        }
                        if (this.particle == null) {
                            GroupReferenceProcessor groupRefProcessor = new GroupReferenceProcessor(schema);
                            Object object = groupRefProcessor.processNode(childNode);
                            if (object instanceof Particle) {
                                particle = (Particle) object;
                            } else {
                                throw new IllegalObjectReturnedException(object.getClass().getName(), "extension");
                            }
                        } else {
                            throw new ExtensionMultipleParticleException("group");
                        }
                    }
                    break;
                case ALL:
                    if (this.isComplexContentExtension) {
                        if (getDebug()) {
                            System.out.println("all");
                        }
                        // Attributes have to be the last elements in the content model
                        if (alreadyAnAttributeAdded) {
                            throw new AttributeIsNotLastInContentModelException("all to extension");
                        }
                        if (this.particle == null) {
                            AllProcessor allProcessor = new AllProcessor(schema);
                            particle = allProcessor.processNode(childNode);
                        } else {
                            throw new ExtensionMultipleParticleException("all");
                        }
                    }
                    break;
                case CHOICE:
                    if (this.isComplexContentExtension) {
                        if (getDebug()) {
                            System.out.println("choice");
                        }
                        // Attributes have to be the last elements in the content model
                        if (alreadyAnAttributeAdded) {
                            throw new AttributeIsNotLastInContentModelException("choice to extension");
                        }
                        if (this.particle == null) {
                            ChoiceProcessor choiceProcessor = new ChoiceProcessor(schema);
                            particle = choiceProcessor.processNode(childNode);
                        } else {
                            throw new ExtensionMultipleParticleException("choice");
                        }
                    }
                    break;
                case SEQUENCE:
                    if (this.isComplexContentExtension) {
                        if (getDebug()) {
                            System.out.println("sequence");
                        }
                        // Attributes have to be the last elements in the content model
                        if (alreadyAnAttributeAdded) {
                            throw new AttributeIsNotLastInContentModelException("sequence to extension");
                        }
                        if (this.particle == null) {
                            SequenceProcessor sequenceProcessor = new SequenceProcessor(schema);
                            particle = sequenceProcessor.processNode(childNode);
                        } else {
                            throw new ExtensionMultipleParticleException("sequence");
                        }
                    }
                    break;

                // For ComplexTypes and SimpleTypes
                case ATTRIBUTE:
                    if (getDebug()) {
                        System.out.println("attribute");
                    }
                    AttributeProcessor attributeProcessor = new AttributeProcessor(schema);
                    AttributeParticle attributeParticle = attributeProcessor.processNode(childNode);
                    if (this.isComplexContentExtension) {
                        // An AnyAttribute is only allowed to be the last element in an attributeGroup.
                        // If a second anyAttribute is found after an anyAttribute, this is a failure.
                        if (!((ComplexContentExtension) inheritance).getAttributes().isEmpty() && ((ComplexContentExtension) inheritance).getAttributes().getLast() instanceof AnyAttribute) {
                            throw new AnyAttributeIsNotLastException("attribute to extension");
                        }
                        ((ComplexContentExtension) inheritance).addAttribute(attributeParticle);
                        alreadyAnAttributeAdded = true;
                    } else {
                        // An AnyAttribute is only allowed to be the last element in an attributeGroup.
                        // If a second anyAttribute is found after an anyAttribute, this is a failure.
                        if (!((SimpleContentExtension) inheritance).getAttributes().isEmpty() && ((SimpleContentExtension) inheritance).getAttributes().getLast() instanceof AnyAttribute) {
                            throw new AnyAttributeIsNotLastException("attribute to extension");
                        }
                        ((SimpleContentExtension) inheritance).addAttributeParticle(attributeParticle);
                        alreadyAnAttributeAdded = true;
                    }
                    break;
                case ATTRIBUTEGROUP:
                    if (getDebug()) {
                        System.out.println("attributeGroup");
                    }
                    AttributeGroupReferenceProcessor attributeGroupReferenceProcessor = new AttributeGroupReferenceProcessor(schema);
                    AttributeGroupReference attributeGroupRef = attributeGroupReferenceProcessor.processNode(childNode);
                    if (this.isComplexContentExtension) {
                        // An AnyAttribute is only allowed to be the last element in an attributeGroup.
                        // If a second anyAttribute is found after an anyAttribute, this is a failure.
                        if (!((ComplexContentExtension) inheritance).getAttributes().isEmpty() && ((ComplexContentExtension) inheritance).getAttributes().getLast() instanceof AnyAttribute) {
                            throw new AnyAttributeIsNotLastException("attributeGroup to extension");
                        }
                        ((ComplexContentExtension) inheritance).addAttribute(attributeGroupRef);
                        alreadyAnAttributeAdded = true;
                    } else {
                        // An AnyAttribute is only allowed to be the last element in an attributeGroup.
                        // If a second anyAttribute is found after an anyAttribute, this is a failure.
                        if (!((SimpleContentExtension) inheritance).getAttributes().isEmpty() && ((SimpleContentExtension) inheritance).getAttributes().getLast() instanceof AnyAttribute) {
                            throw new AnyAttributeIsNotLastException("attributeGroup to extension");
                        }
                        ((SimpleContentExtension) inheritance).addAttributeParticle(attributeGroupRef);
                        alreadyAnAttributeAdded = true;
                    }
                    break;
                case ANYATTRIBUTE:
                    if (getDebug()) {
                        System.out.println("anyAttribute");
                    }
                    AnyAttributeProcessor anyAttributeProcessor = new AnyAttributeProcessor(schema);
                    AnyAttribute anyAttribute = anyAttributeProcessor.processNode(childNode);
                    if (this.isComplexContentExtension) {
                        // An AnyAttribute is only allowed to be the last element in an attributeGroup.
                        // If a second anyAttribute is found after an anyAttribute, this is a failure.
                        if (!((ComplexContentExtension) inheritance).getAttributes().isEmpty() && ((ComplexContentExtension) inheritance).getAttributes().getLast() instanceof AnyAttribute) {
                            throw new AnyAttributeIsNotLastException("anyAttribute to extension");
                        }
                        ((ComplexContentExtension) inheritance).addAttribute(anyAttribute);
                        alreadyAnAttributeAdded = true;
                    } else {
                        // An AnyAttribute is only allowed to be the last element in an attributeGroup.
                        // If a second anyAttribute is found after an anyAttribute, this is a failure.
                        if (!((SimpleContentExtension) inheritance).getAttributes().isEmpty() && ((SimpleContentExtension) inheritance).getAttributes().getLast() instanceof AnyAttribute) {
                            throw new AnyAttributeIsNotLastException("anyAttribute to extension");
                        }
                        ((SimpleContentExtension) inheritance).addAttributeParticle(anyAttribute);
                        alreadyAnAttributeAdded = true;
                    }
                    break;
                case ANNOTATION:
                    if (annotation == null) {
                        AnnotationProcessor annotationProcessor = new AnnotationProcessor(schema);
                        annotation = annotationProcessor.processNode(childNode);
                    } else {
                        throw new MultipleAnnotationException("extension");
                    }
                    break;
                default:
                    throw new UnsupportedContentException(nodeName, "extension");
            }
        }
    }

    /**
     * The method processNode handles all attributes and properties of
     * the current extension-tag. The given properties are checked against the
     * allowed values defined in the XSD primer.
     * There are two different use cases of a extension in XSD.
     *
     * Under a SimpleContent:
     * -------------------------------------------------------------------------
     * <extension
     * base = QName
     * id = ID
     * {any attributes with non-schema namespace . . .}>
     * Content: (annotation?, ((attribute | attributeGroup)*, anyAttribute?))
     * </extension>
     * -------------------------------------------------------------------------
     *
     *
     * Under a ComplexContent:
     * -------------------------------------------------------------------------
     * <extension
     * base = QName
     * id = ID
     * {any attributes with non-schema namespace . . .}>
     * Content: (annotation?, ((group | all | choice | sequence)?, ((attribute |
     *          attributeGroup)*, anyAttribute?)))
     * </extension>
     * -------------------------------------------------------------------------
     *
     *
     * The content is managed by the processChild-method.
     * @param node
     * @return Inheritance
     * @throws Exception
     */
    @Override
    protected Inheritance processNode(Node node) throws XSDParseException {

        // We have to check the parentnode to determine if it is an extension
        // for a simpleContent or a complexContent
        // (check with if-statement over the name of the parentnode)

        String parentNodeName = node.getParentNode().getNodeName();
        if (parentNodeName.contains(":")) {
            parentNodeName = parentNodeName.split(":")[1];
        }

        if (parentNodeName.equals("complexContent")) {
            this.isComplexContentExtension = true;
        }
        // If there is an ID, we can add it to the corresponding object.
        if (node.getAttributes().getNamedItem("id") != null) {
            if (node.getAttributes().getNamedItem("id").getNodeValue().equals("")) {
                throw new EmptyIdException("extension");
            }
            idString = node.getAttributes().getNamedItem("id").getNodeValue();
        }

        // Check for Attribute "base"
        if (node.getAttributes() != null && node.getAttributes().getNamedItem("base") != null) {
            QualifiedName baseTypeName = getName(node.getAttributes().getNamedItem("base").getNodeValue());

            // Check the type of the parent and generate the corresponding Extension object
            if (this.isComplexContentExtension) {
                ComplexContentExtension complexContentExtension = new ComplexContentExtension(baseTypeName);
                this.inheritance = complexContentExtension;
            } else {
                SimpleContentExtension simpleContentExtension = new SimpleContentExtension(baseTypeName);
                this.inheritance = simpleContentExtension;
            }

            // Call the visitChildren method to handle children and find necessary details
            visitChildren(node);

            if (idString != null) {
                this.inheritance.setId(idString);
            }
            inheritance.setAnnotation(annotation);
        }
        return inheritance;
    }

    /**
     * This is used by the ComplexContentProcessor to get the particle out of
     * this restriction. This is necessary, because the standard return value is
     * the generated inheritance above.
     * @return Particle - Returns the particle of this restriction inheritance
     */
    protected Particle getParticle() {
        return this.particle;
    }
}
