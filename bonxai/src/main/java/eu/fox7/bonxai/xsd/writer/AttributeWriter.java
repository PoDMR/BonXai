/**
 * This file is part of BonXai.
 *
 * BonXai is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * BonXai is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with BonXai.  If not, see <http://www.gnu.org/licenses/>.
 */
package eu.fox7.bonxai.xsd.writer;

import java.util.*;

import org.w3c.dom.*;

import eu.fox7.bonxai.common.AnyAttribute;
import eu.fox7.bonxai.xsd.*;

public abstract class AttributeWriter {

    /**
     * Writes the given list of AttributeParticles to the given root-Node
     *
     * @param root
     * @param attributes
     * @param foundElements
     */
    protected static void writeAttributeParticles(Node root, LinkedList<AttributeParticle> attributes,
            FoundElements foundElements) {
        for (AttributeParticle ap : attributes) {
            if (ap instanceof Attribute) {
                writeAttribute(root, (Attribute) ap, foundElements);
            } else if (ap instanceof AnyAttribute) {
                writeAnyAttribute(root, (AnyAttribute) ap, foundElements);
            } else if (ap instanceof AttributeGroupRef) {
                writeAttributeGroupRef(root, (AttributeGroupRef) ap, foundElements);
            } else if (ap instanceof AttributeRef) {
                writeAttributeRef(root, (AttributeRef) ap, foundElements);
            }
        }
    }

    /**
     * writes the given List of Attributes to the given Node.
     *
     * @param root
     * @param attributes
     */
    protected static void writeAttributeList(Node root, LinkedList<Attribute> attributes,
            FoundElements foundElements) {
        for (Attribute a : attributes) {
            writeAttribute(root, a, foundElements);
        }
    }

    /**
     * writes the given Attribute to the given Node.
     * @param root
     * @param attribute
     */
    protected static void writeAttribute(Node root, Attribute attribute, FoundElements foundElements) {
//        <attribute id = ID
//        default = string
//        fixed = string
//        form = (qualified | unqualified)
//        name = NCName
//        ref = QName
//        type = QName
//        use = (optional | prohibited | required) : optional >
//        Content: (annotation?, (simpleType?)) </attribute>
        org.w3c.dom.Element attrNode;
        String useString = "";
        SimpleType sType;

        attrNode = root.getOwnerDocument().createElementNS(
                "http://www.w3.org/2001/XMLSchema", "attribute");
        foundElements.setXSDPrefix(attrNode);

        if (attribute.getName() != null) {
            attrNode.setAttribute("name", attribute.getLocalName());
        }
        AnnotationWriter.writeAnnotation(attrNode, attribute, foundElements);

        if (attribute.getSimpleType() != null && !attribute.getSimpleType().getName().equals("{http://www.w3.org/2001/XMLSchema}anySimpleType")) {
            sType = attribute.getSimpleType();
            // Fixed Bug with containsType: if(foundElements.containsType(sType) || (sType.getInheritance() == null && sType.getFinalModifiers() == null))
            // Changed to write types when needed
            if (attribute.getTypeAttr() || (sType.getInheritance() == null && sType.getFinalModifiers() == null)) {
                attrNode.setAttribute("type", foundElements.getPrefix(sType.getNamespace()) + sType.getLocalName());
            } else {
                TypeWriter.writeSimpleType(attrNode, sType, foundElements, false);
            }
        }
        if (attribute.getDefault() != null) {
            attrNode.setAttribute("default", attribute.getDefault());
        }
        if (attribute.getId() != null) {
            attrNode.setAttribute("id", attribute.getId());
        }
        if (attribute.getFixed() != null) {
            attrNode.setAttribute("fixed", attribute.getFixed());
        }

        if (attribute.getForm() != null) {
            if (attribute.getForm().equals(XSDSchema.Qualification.qualified)) {
                attrNode.setAttribute("form", "qualified");
            } else {
                attrNode.setAttribute("form", "unqualified");
            }
        }

        if (attribute.getUse() != null) {
            switch (attribute.getUse()) {
                case Optional:
                    useString = "optional";
                    break;
                case Prohibited:
                    useString = "prohibited";
                    break;
                case Required:
                    useString = "required";
                    break;
            }
            // "optional" is the default-value for the use
            if (!useString.equals("optional")) {
                attrNode.setAttribute("use", useString);
            }
        }


        root.appendChild(attrNode);
    }

    /**
     * writes an AnyAttribute-Node.
     * @param root
     * @param anyAttr
     */
    protected static void writeAnyAttribute(Node root, AnyAttribute anyAttr, FoundElements foundElements) {
//        <anyAttribute id = ID
//        namespace = ((##any | ##other) | List of (anyURI |
//        (##targetNamespace | ##local)) ) : ##any
//        processContents = (lax | skip | strict) : strict >
//        Content: (annotation?)</anyAttribute>
        org.w3c.dom.Element attrNode;
        String procCont = "";

        attrNode = root.getOwnerDocument().createElementNS(
                "http://www.w3.org/2001/XMLSchema", "anyAttribute");
        foundElements.setXSDPrefix(attrNode);
        AnnotationWriter.writeAnnotation(attrNode, anyAttr, foundElements);

        if (anyAttr.getNamespace() != null && !anyAttr.getNamespace().equals("##any")) {
            attrNode.setAttribute("namespace", anyAttr.getNamespace());
        }

        switch (anyAttr.getProcessContentsInstruction()) {
            case Lax:
                procCont = "lax";
                break;
            case Skip:
                procCont = "skip";
                break;
            case Strict:
                procCont = "strict";
                break;
        }
        if (anyAttr.getId() != null) {
            attrNode.setAttribute("id", anyAttr.getId());
        }
        if (!procCont.equals("strict")) {
            attrNode.setAttribute("processContents", procCont);
        }

        root.appendChild(attrNode);
    }

    /**
     * writes referenced Attribute Nodes.
     *
     * @param root
     * @param attr
     */
    protected static void writeAttributeRef(Node root, AttributeRef attr, FoundElements foundElements) {
        org.w3c.dom.Element attrNode;

        String useString = "";

        attrNode = root.getOwnerDocument().createElementNS(
                "http://www.w3.org/2001/XMLSchema", "attribute");
        foundElements.setXSDPrefix(attrNode);
        AnnotationWriter.writeAnnotation(attrNode, attr, foundElements);
        if (attr.getId() != null) {
            attrNode.setAttribute("id", attr.getId());
        }
        if (attr.getDefault() != null) {
            attrNode.setAttribute("default", attr.getDefault());
        }
        if (attr.getFixed() != null) {
            attrNode.setAttribute("fixed", attr.getFixed());
        }
        if (attr.getUse() != null) {
            switch (attr.getUse()) {
                case Optional:
                    useString = "optional";
                    break;
                case Prohibited:
                    useString = "prohibited";
                    break;
                case Required:
                    useString = "required";
                    break;
            }
            // "optional" is the default-value for the use
            if (!useString.equals("optional")) {
                attrNode.setAttribute("use", useString);
            }
        }

        attrNode.setAttribute("ref", foundElements.getPrefix(attr.getAttribute().getNamespace()) + attr.getAttribute().getLocalName());

        root.appendChild(attrNode);
    }

    /**
     * writes an attributeGroupNode which references to a already defined attributeGroup.
     * The referenced attributeGroup must contain a Name.
     *
     * @param root
     * @param attrGroup
     * @param foundElements
     */
    protected static void writeAttributeGroupRef(Node root, AttributeGroupRef attrGroup, FoundElements foundElements) {
        org.w3c.dom.Element attrNode;

        attrNode = root.getOwnerDocument().createElementNS(
                "http://www.w3.org/2001/XMLSchema", "attributeGroup");
        foundElements.setXSDPrefix(attrNode);
        AnnotationWriter.writeAnnotation(attrNode, attrGroup, foundElements);
        attrNode.setAttribute("ref", foundElements.getPrefix(attrGroup.getAttributeGroup().getNamespace()) + attrGroup.getAttributeGroup().getLocalName());


        if (attrGroup.getId() != null) {
            attrNode.setAttribute("id", attrGroup.getId());
        }
        root.appendChild(attrNode);
    }

    /**
     * writes an attributeGroup-Node.
     *
     * @param root
     * @param ag
     * @param foundElements
     */
    public static void writeAttributeGroup(Node root, AttributeGroup ag, FoundElements foundElements) {
//        <attributeGroup id = ID
//        name = NCName
//        ref = QName >
//        Content: (annotation?,
//        ((attribute | attributeGroup)*, anyAttribute?)) </attributeGroup>
        org.w3c.dom.Element attrNode;

        attrNode = root.getOwnerDocument().createElementNS(
                "http://www.w3.org/2001/XMLSchema", "attributeGroup");
        foundElements.setXSDPrefix(attrNode);
        AnnotationWriter.writeAnnotation(attrNode, ag, foundElements);

        if (ag.getName() != null) {
            attrNode.setAttribute("name", ag.getLocalName());
        }
        if (ag.getId() != null) {
            attrNode.setAttribute("id", ag.getId());
        }


        writeAttributeParticles(attrNode, ag.getAttributeParticles(), foundElements);

        root.appendChild(attrNode);
    }
}
