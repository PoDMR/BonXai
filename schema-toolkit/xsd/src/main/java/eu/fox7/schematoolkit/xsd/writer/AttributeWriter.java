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
package eu.fox7.schematoolkit.xsd.writer;

import java.util.*;

import org.w3c.dom.*;

import eu.fox7.schematoolkit.common.AnyAttribute;
import eu.fox7.schematoolkit.common.AttributeGroupReference;
import eu.fox7.schematoolkit.common.AttributeParticle;
import eu.fox7.schematoolkit.common.Namespace;
import eu.fox7.schematoolkit.common.QualifiedName;
import eu.fox7.schematoolkit.xsd.om.*;

public abstract class AttributeWriter {

    /**
     * Writes the given list of AttributeParticles to the given root-Node
     *
     * @param root
     * @param attributes
     * @param foundElements
     */
    protected static void writeAttributeParticles(Node root, LinkedList<AttributeParticle> attributes,
            XSDSchema schema) {
        for (AttributeParticle ap : attributes) {
            if (ap instanceof Attribute) {
                writeAttribute(root, (Attribute) ap, schema);
            } else if (ap instanceof AnyAttribute) {
                writeAnyAttribute(root, (AnyAttribute) ap, schema);
            } else if (ap instanceof AttributeGroupReference) {
                writeAttributeGroupRef(root, (AttributeGroupReference) ap, schema);
            } else if (ap instanceof AttributeRef) {
                writeAttributeRef(root, (AttributeRef) ap, schema);
            }
        }
    }

    /**
     * writes the given List of Attributes to the given Node.
     *
     * @param root
     * @param attributes
     */
    protected static void writeAttributeList(Node root, Collection<Attribute> attributes,
            XSDSchema schema) {
        for (Attribute a : attributes) {
            writeAttribute(root, a, schema);
        }
    }

    /**
     * writes the given Attribute to the given Node.
     * @param root
     * @param attribute
     */
    protected static void writeAttribute(Node root, Attribute attribute, XSDSchema schema) {
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
        DOMHelper.setXSDPrefix(attrNode, schema);

        if (attribute.getName() != null) {
            attrNode.setAttribute("name", attribute.getName().getName());
        }
        AnnotationWriter.writeAnnotation(attrNode, attribute, schema);

        QualifiedName simpleTypeName = attribute.getSimpleTypeName();
        if (!simpleTypeName.getFullyQualifiedName().equals("{http://www.w3.org/2001/XMLSchema}anySimpleType")) {
            QualifiedName typeName = attribute.getSimpleTypeName();
            try {
            	sType = (SimpleType) schema.getType(typeName);
            } catch (ClassCastException e) {
            	throw new RuntimeException("Attribute has a complex type.", e);
            }

            if (sType == null || !sType.isAnonymous() || (sType.getInheritance() == null && sType.getFinalModifiers() == null)) {
                attrNode.setAttribute("type", schema.getQualifiedName(typeName));
            } else {
                TypeWriter.writeSimpleType(attrNode, sType, schema, false);
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
    protected static void writeAnyAttribute(Node root, AnyAttribute anyAttr, XSDSchema schema) {
//        <anyAttribute id = ID
//        namespace = ((##any | ##other) | List of (anyURI |
//        (##targetNamespace | ##local)) ) : ##any
//        processContents = (lax | skip | strict) : strict >
//        Content: (annotation?)</anyAttribute>
        org.w3c.dom.Element attrNode;
        String procCont = "";

        attrNode = root.getOwnerDocument().createElementNS(
                "http://www.w3.org/2001/XMLSchema", "anyAttribute");
        DOMHelper.setXSDPrefix(attrNode, schema);
        AnnotationWriter.writeAnnotation(attrNode, anyAttr, schema);

        if (!anyAttr.isAnyNamespace()) {
        	Collection<Namespace> namespaces = anyAttr.getNamespaces();
        	StringBuilder sb = new StringBuilder();
        	Iterator<Namespace> it = namespaces.iterator();
        	while(it.hasNext()) {
            	Namespace namespace = it.next();
            	sb.append(namespace.getUri());
            	if (it.hasNext())
            		sb.append(' ');
        	}
        	
            attrNode.setAttribute("namespace", sb.toString());
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
    protected static void writeAttributeRef(Node root, AttributeRef attr, XSDSchema schema) {
        org.w3c.dom.Element attrNode;

        String useString = "";

        attrNode = root.getOwnerDocument().createElementNS(
                "http://www.w3.org/2001/XMLSchema", "attribute");
        DOMHelper.setXSDPrefix(attrNode, schema);
        AnnotationWriter.writeAnnotation(attrNode, attr, schema);
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

        attrNode.setAttribute("ref", schema.getQualifiedName(attr.getAttributeName()));

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
    protected static void writeAttributeGroupRef(Node root, AttributeGroupReference attrGroup, XSDSchema schema) {
        org.w3c.dom.Element attrNode;

        attrNode = root.getOwnerDocument().createElementNS(
                "http://www.w3.org/2001/XMLSchema", "attributeGroup");
        DOMHelper.setXSDPrefix(attrNode, schema);
        AnnotationWriter.writeAnnotation(attrNode, attrGroup, schema);
        attrNode.setAttribute("ref", schema.getQualifiedName(attrGroup.getName()));


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
    public static void writeAttributeGroup(Node root, AttributeGroup ag, XSDSchema schema) {
//        <attributeGroup id = ID
//        name = NCName
//        ref = QName >
//        Content: (annotation?,
//        ((attribute | attributeGroup)*, anyAttribute?)) </attributeGroup>
        org.w3c.dom.Element attrNode;

        attrNode = root.getOwnerDocument().createElementNS(
                "http://www.w3.org/2001/XMLSchema", "attributeGroup");
        DOMHelper.setXSDPrefix(attrNode, schema);
        AnnotationWriter.writeAnnotation(attrNode, ag, schema);

        if (ag.getName() != null) {
            attrNode.setAttribute("name", ag.getName().getName());
        }
        if (ag.getId() != null) {
            attrNode.setAttribute("id", ag.getId());
        }


        writeAttributeParticles(attrNode, ag.getAttributeParticles(), schema);

        root.appendChild(attrNode);
    }
}
