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

import eu.fox7.bonxai.common.CountingPattern;
import eu.fox7.bonxai.common.Particle;
import eu.fox7.bonxai.common.QualifiedName;
import eu.fox7.bonxai.common.SequencePattern;
import eu.fox7.bonxai.xsd.*;
import eu.fox7.bonxai.xsd.Element;

/**
 *
 */
public abstract class TypeWriter {

    /**
     * Writes the Type to the current Node.
     *
     * @param root
     * @param baseType
     * @param foundElements
     */
    protected static void writeType(Node root, Type type, XSDSchema schema, boolean writeName) {
        org.w3c.dom.Element rootNode;
//        if (foundElements.containsType(baseType) && root.getNodeName().contains("element")) {
        if (!type.isAnonymous() && root.getNodeName().contains("element")) {
            rootNode = (org.w3c.dom.Element) root;
            rootNode.setAttribute("type", type.getName().getQualifiedName());
        } else if (type instanceof SimpleType) {
            TypeWriter.writeSimpleType(root, (SimpleType) type, schema, writeName);
        } else if (type instanceof ComplexType) {
            writeComplexType(root, (ComplexType) type, schema, writeName);
        }
    }

    /**
     * Writes the SimpleType node and iterates through the contained
     * types/elements and calls the write-Methods recursively.
     *
     * <baseType final = (#all | (list | union | restriction)) name = NCName>
     * Content: (annotation?, (restriction | list | union)) </baseType>
     */
    protected static void writeSimpleType(Node root, SimpleType type, XSDSchema schema, boolean writeName) {
        org.w3c.dom.Element sTypeNode;
        sTypeNode = root.getOwnerDocument().createElementNS("http://www.w3.org/2001/XMLSchema", "simpleType");
        DOMHelper.setXSDPrefix(sTypeNode, schema);
        AnnotationWriter.writeAnnotation(sTypeNode, type, schema);
        String finalDefaults = "";
        if (type.getFinalModifiers() != null && !type.getFinalModifiers().isEmpty()) {
            boolean finalRestriction = false, finalList = false, finalUnion = false;
            for (SimpleTypeInheritanceModifier currentfinalValue : type.getFinalModifiers()) {
                switch (currentfinalValue) {
                    case Restriction:
                        finalRestriction = true;
                        break;
                    case List:
                        finalList = true;
                        break;
                    case Union:
                        finalUnion = true;
                        break;
                }
            }
            if (finalRestriction && finalList && finalUnion) {
                finalDefaults = "#all";
            } else {
                if (finalRestriction) {
                    finalDefaults = finalDefaults + "restriction ";
                }
                if (finalList) {
                    finalDefaults = finalDefaults + "list ";
                }
                if (finalUnion) {
                    finalDefaults = finalDefaults + "union ";
                }
                if (finalDefaults.length() > 0) {
                    finalDefaults = finalDefaults.substring(0, finalDefaults.length() - 1);
                }
            }
        }
        if (type.getFinalModifiers() != null) {
            sTypeNode.setAttribute("final", finalDefaults);
        }

        if (writeName) {
            sTypeNode.setAttribute("name", type.getName().getName());
        }
        if (type.getId() != null) {
            sTypeNode.setAttribute("id", type.getId());
        }

        root.appendChild(sTypeNode);
        writeSimpleTypeInheritance(sTypeNode, type, schema);
    }

    /**
     * Writes the ComplexType node.
     *
     * @param root
     * @param baseType
     */
    protected static void writeComplexType(Node root, ComplexType type, XSDSchema schema, boolean writeName) {
        // <complexType
        // abstract = Boolean : false
        // block = (#all | List of (extension | restriction))
        // final = (#all | List of (extension | restriction))
        // id = ID
        // mixed = Boolean : false
        // name = NCName
        // {any attributes with non-schema Namespace...}>
        // Content: (annotation?, (simpleContent | complexContent | ((group |
        // all |
        // choice | sequence)?, ((attribute | attributeGroup)*,
        // anyAttribute?))))
        // </complexType>
        org.w3c.dom.Element cTypeNode;

        cTypeNode = root.getOwnerDocument().createElementNS("http://www.w3.org/2001/XMLSchema", "complexType");
        DOMHelper.setXSDPrefix(cTypeNode, schema);
        if (writeName) {
            cTypeNode.setAttribute("name", type.getName().getName());
        }
        AnnotationWriter.writeAnnotation(cTypeNode, type, schema);
        root.appendChild(cTypeNode);
        if (type.isAbstract() != null && type.isAbstract()) {
            cTypeNode.setAttribute("abstract", type.isAbstract().toString());
        }
        if (type.getId() != null) {
            cTypeNode.setAttribute("id", type.getId());
        }
        if (type.getFinalModifiers() != null) {
            writeComplexContentMod(cTypeNode, type.getFinalModifiers(), "final");
        }
        if (type.getBlockModifiers() != null) {
            writeComplexContentMod(cTypeNode, type.getBlockModifiers(), "block");
        }
        if (type.getMixed() == true) {
            cTypeNode.setAttribute("mixed", type.getMixed().toString());
        }
        boolean isInheritanceNull = false;
        if (type.getContent() != null) {
            if (type.getContent() instanceof SimpleContentType) {
                SimpleContentType sContentType = (SimpleContentType) type.getContent();
                if (sContentType.getInheritance() == null) {
                    isInheritanceNull = true;
                }
            }
            if (type.getContent() instanceof ComplexContentType) {
                ComplexContentType cContentType = (ComplexContentType) type.getContent();
                if (cContentType.getInheritance() == null) {
                    isInheritanceNull = true;
                }
            }
            writeContent(cTypeNode, type, schema);
        }
        /*
         * when a inheritance is defined attributes are written only in
         * extension/restriction node
         */
        if (type.getAttributes() != null && (isInheritanceNull || type.getContent() == null)) {
            AttributeWriter.writeAttributeParticles(cTypeNode, type.getAttributes(), schema);
        }
    }

    /**
     * Writes the nodes for the baseType Inheritance.
     *
     * @param sTypeNode
     * @param baseType
     */
    protected static void writeSimpleTypeInheritance(Node sTypeNode, SimpleType type, XSDSchema schema) {
        SimpleContentList contentlist;
        SimpleContentUnion contentUnion;
        SimpleContentRestriction contentRestrict;
        if (type.getInheritance() != null) {
            if (type.getInheritance() instanceof SimpleContentList) {
                contentlist = (SimpleContentList) type.getInheritance();
                TypeWriter.writeSimpContList(sTypeNode, contentlist, schema);
            } else if (type.getInheritance() instanceof SimpleContentUnion) {
                contentUnion = (SimpleContentUnion) type.getInheritance();
                TypeWriter.writeSimpContUnion(sTypeNode, contentUnion, schema);
            } else if (type.getInheritance() instanceof SimpleContentRestriction) {
                contentRestrict = (SimpleContentRestriction) type.getInheritance();
                TypeWriter.writeSimpleContentRestriction(sTypeNode, contentRestrict, schema);
            }
        }
    }

    /**
     * Writes the List-Node to the given root-Node.
     *
     * Example: <xs:list itemType="xs:date">
     *
     * @param sTypeNode
     * @param contentlist
     * @param foundElements
     */
    protected static void writeSimpContList(Node sTypeNode, SimpleContentList contentlist, XSDSchema schema) {
        // <list
        // id = ID
        // itemType = QName
        // {any attributes with non-schema Namespace}...>
        // Content: (annotation?, (baseType?))
        // </list>
        org.w3c.dom.Element listElement;
        listElement = sTypeNode.getOwnerDocument().createElementNS("http://www.w3.org/2001/XMLSchema", "list");
        DOMHelper.setXSDPrefix(listElement, schema);
        AnnotationWriter.writeAnnotation(listElement, contentlist, schema);
        QualifiedName simpleTypeName = contentlist.getBaseType();
        SimpleType simpleType = (SimpleType) schema.getType(simpleTypeName);
        
        sTypeNode.appendChild(listElement);
        /*
         * Types which were defined outside of the simple content union
         * are listed in the member types attribute, otherwise their
         * definition is appended to the simple content union
         */
        if (simpleType.isAnonymous()) {
            writeSimpleType(listElement, simpleType, schema, false);
        } else {
            listElement.setAttribute("itemType", simpleType.getName().getQualifiedName());
        }
        if (contentlist.getId() != null) {
            listElement.setAttribute("id", contentlist.getId());
        }
    }

    /**
     * Writes the Union-Node to the given root-Node.
     *
     * Example: <xs:union> <xs:baseType> <xs:restriction base="roadbikesize"/>
     * </xs:baseType> <xs:baseType> <xs:restriction
     * base="mountainbikesize"/> </xs:baseType> </xs:union>
     *
     * @param sTypeNode
     * @param contentUnion
     * @param foundElements
     */
    protected static void writeSimpContUnion(Node sTypeNode, SimpleContentUnion contentUnion, XSDSchema schema) {
        // <union
        // id = ID
        // memberTypes = List of QNames
        // {any attributes with non-schema Namespace}...>
        // Content: (annotation?, (baseType*))
        // </union>
        org.w3c.dom.Element unionElement;
        Collection<QualifiedName> types = contentUnion.getMemberTypes();
        StringBuilder memberTypesString;
        unionElement = sTypeNode.getOwnerDocument().createElementNS("http://www.w3.org/2001/XMLSchema", "union");
        DOMHelper.setXSDPrefix(unionElement, schema);
        AnnotationWriter.writeAnnotation(unionElement, contentUnion, schema);

        sTypeNode.appendChild(unionElement);
        memberTypesString = new StringBuilder();
        boolean first = true;
        for (QualifiedName typeName: types) {
        	SimpleType sType = (SimpleType) schema.getType(typeName);
        	if (sType.isAnonymous()) {
        		writeSimpleType(unionElement, sType, schema, false);
        	} else {
        		if (!first) {
        			memberTypesString.append(' ');
        		}
        		first = false;
        		memberTypesString.append(typeName.getQualifiedName());
        	}
        }

        if (contentUnion.getId() != null) {
            unionElement.setAttribute("id", contentUnion.getId());
        }
        if (!memberTypesString.toString().isEmpty()) {
            unionElement.setAttribute("memberTypes", memberTypesString.toString());
        }
    }

    /**
     * Writes the restrictions for a baseType.
     *
     * @param root
     * @param contentRestrict
     * @param foundElements
     */
    protected static void writeSimpleContentRestriction(Node root,
            SimpleContentRestriction contentRestrict, XSDSchema schema) {
        // <restriction
        // base = QName
        // id = ID
        // {any attributes with non-schema Namespace}...>
        // Content: (annotation?, (baseType?, (minExclusive | minInclusive |
        // maxExclusive | maxInclusive | totalDigits |fractionDigits | length |
        // minLength | maxLength | enumeration | whiteSpace | pattern)*))
        // </restriction>
        org.w3c.dom.Element restrElement;
        org.w3c.dom.Element tmpElement;
        Type baseType = null;
        restrElement = root.getOwnerDocument().createElementNS("http://www.w3.org/2001/XMLSchema", "restriction");
        DOMHelper.setXSDPrefix(restrElement, schema);
                AnnotationWriter.writeAnnotation(restrElement, contentRestrict, schema);
        QualifiedName baseTypeName = contentRestrict.getBaseType();
        baseType = schema.getType(baseTypeName);
        
        if (baseType.isAnonymous()) {
            writeSimpleType(restrElement, (SimpleType) baseType, schema, false);
        } else {
            restrElement.setAttribute("base", baseTypeName.getQualifiedName());
        }

        if (contentRestrict.getId() != null) {
            restrElement.setAttribute("id", contentRestrict.getId());
        }
        if (contentRestrict.getMinExclusive() != null) {
            tmpElement = root.getOwnerDocument().createElementNS("http://www.w3.org/2001/XMLSchema", "minExclusive");
            DOMHelper.setXSDPrefix(tmpElement, schema);
            tmpElement.setAttribute("value", contentRestrict.getMinExclusive().getValue());
            if (contentRestrict.getMinExclusive().getFixed() == true) {
                tmpElement.setAttribute("fixed", contentRestrict.getMinExclusive().getFixed().toString());
            }
            restrElement.appendChild(tmpElement);
        }
        if (contentRestrict.getMinInclusive() != null) {
            tmpElement = root.getOwnerDocument().createElementNS("http://www.w3.org/2001/XMLSchema", "minInclusive");
            DOMHelper.setXSDPrefix(tmpElement, schema);
            tmpElement.setAttribute("value", contentRestrict.getMinInclusive().getValue());
            if (contentRestrict.getMinInclusive().getFixed() == true) {
                tmpElement.setAttribute("fixed", contentRestrict.getMinInclusive().getFixed().toString());
            }
            restrElement.appendChild(tmpElement);
        }
        if (contentRestrict.getMaxExclusive() != null) {
            tmpElement = root.getOwnerDocument().createElementNS("http://www.w3.org/2001/XMLSchema", "maxExclusive");
            DOMHelper.setXSDPrefix(tmpElement, schema);
            tmpElement.setAttribute("value", contentRestrict.getMaxExclusive().getValue());
            if (contentRestrict.getMaxExclusive().getFixed() == true) {
                tmpElement.setAttribute("fixed", contentRestrict.getMaxExclusive().getFixed().toString());
            }
            restrElement.appendChild(tmpElement);
        }
        if (contentRestrict.getMaxInclusive() != null) {
            tmpElement = root.getOwnerDocument().createElementNS("http://www.w3.org/2001/XMLSchema", "maxInclusive");
            DOMHelper.setXSDPrefix(tmpElement, schema);
            tmpElement.setAttribute("value", contentRestrict.getMaxInclusive().getValue());
            if (contentRestrict.getMaxInclusive().getFixed() == true) {
                tmpElement.setAttribute("fixed", contentRestrict.getMaxInclusive().getFixed().toString());
            }
            restrElement.appendChild(tmpElement);
        }
        if (contentRestrict.getTotalDigits() != null) {
            tmpElement = root.getOwnerDocument().createElementNS("http://www.w3.org/2001/XMLSchema", "totalDigits");
            DOMHelper.setXSDPrefix(tmpElement, schema);
            tmpElement.setAttribute("value", contentRestrict.getTotalDigits().getValue().toString());
            if (contentRestrict.getTotalDigits().getFixed() == true) {
                tmpElement.setAttribute("fixed", contentRestrict.getTotalDigits().getFixed().toString());
            }
            restrElement.appendChild(tmpElement);
        }
        if (contentRestrict.getFractionDigits() != null) {
            tmpElement = root.getOwnerDocument().createElementNS("http://www.w3.org/2001/XMLSchema", "fractionDigits");
            DOMHelper.setXSDPrefix(tmpElement, schema);
            tmpElement.setAttribute("value", contentRestrict.getFractionDigits().getValue().toString());
            if (contentRestrict.getFractionDigits().getFixed() == true) {
                tmpElement.setAttribute("fixed", contentRestrict.getFractionDigits().getFixed().toString());
            }
            restrElement.appendChild(tmpElement);
        }
        if (contentRestrict.getLength() != null) {
            tmpElement = root.getOwnerDocument().createElementNS("http://www.w3.org/2001/XMLSchema", "length");
            DOMHelper.setXSDPrefix(tmpElement, schema);
            tmpElement.setAttribute("value", contentRestrict.getLength().getValue().toString());
            if (contentRestrict.getLength().getFixed() == true) {
                tmpElement.setAttribute("fixed", contentRestrict.getLength().getFixed().toString());
            }
            restrElement.appendChild(tmpElement);
        }
        if (contentRestrict.getMinLength() != null) {
            tmpElement = root.getOwnerDocument().createElementNS("http://www.w3.org/2001/XMLSchema", "minLength");
            DOMHelper.setXSDPrefix(tmpElement, schema);
            tmpElement.setAttribute("value", contentRestrict.getMinLength().getValue().toString());
            if (contentRestrict.getMinLength().getFixed() == true) {
                tmpElement.setAttribute("fixed", contentRestrict.getMinLength().getFixed().toString());
            }
            restrElement.appendChild(tmpElement);
        }
        if (contentRestrict.getMaxLength() != null) {
            tmpElement = root.getOwnerDocument().createElementNS("http://www.w3.org/2001/XMLSchema", "maxLength");
            DOMHelper.setXSDPrefix(tmpElement, schema);
            tmpElement.setAttribute("value", contentRestrict.getMaxLength().getValue().toString());
            if (contentRestrict.getMaxLength().getFixed() == true) {
                tmpElement.setAttribute("fixed", contentRestrict.getMaxLength().getFixed().toString());
            }
            restrElement.appendChild(tmpElement);
        }
        if (contentRestrict.getEnumeration() != null) {
            for (String s : contentRestrict.getEnumeration()) {
                tmpElement = root.getOwnerDocument().createElementNS("http://www.w3.org/2001/XMLSchema", "enumeration");
                DOMHelper.setXSDPrefix(tmpElement, schema);
                tmpElement.setAttribute("value", s);
                restrElement.appendChild(tmpElement);
            }
        }
        if (contentRestrict.getWhitespace() != null) {
            String value = "";
            tmpElement = root.getOwnerDocument().createElementNS("http://www.w3.org/2001/XMLSchema", "whiteSpace");
            DOMHelper.setXSDPrefix(tmpElement, schema);
            switch (contentRestrict.getWhitespace().getValue()) {
                case Collapse:
                    value = "collapse";
                    break;
                case Preserve:
                    value = "preserve";
                    break;
                case Replace:
                    value = "replace";
                    break;
            }
            tmpElement.setAttribute("value", value);
            if (contentRestrict.getWhitespace().getFixed() == true) {
                tmpElement.setAttribute("fixed", contentRestrict.getWhitespace().getFixed().toString());
            }
            restrElement.appendChild(tmpElement);
        }
        if (contentRestrict.getPattern() != null) {
            tmpElement = root.getOwnerDocument().createElementNS("http://www.w3.org/2001/XMLSchema", "pattern");
            DOMHelper.setXSDPrefix(tmpElement, schema);
            tmpElement.setAttribute("value", contentRestrict.getPattern().getValue());
            restrElement.appendChild(tmpElement);
        }
        AttributeWriter.writeAttributeParticles(restrElement, contentRestrict.getAttributes(), schema);

        root.appendChild(restrElement);
    }

    /**
     * Writes the attributes to the typeNode depending on the Type of
     * InheritanceModifier found.
     *
     * @param typeNode
     *            Must be either "final" or "block"
     * @param modifiers
     * @param modType
     */
    protected static void writeComplexContentMod(org.w3c.dom.Element typeNode, HashSet<ComplexTypeInheritanceModifier> modifiers, String modType) {
        String modifierString = "";
        if (!modifiers.isEmpty()) {
            boolean modRestriction = false, modExtension = false;
            for (ComplexTypeInheritanceModifier currentValue : modifiers) {
                switch (currentValue) {
                    case Restriction:
                        modRestriction = true;
                        break;
                    case Extension:
                        modExtension = true;
                        break;
                }
            }
            if (modExtension && modRestriction) {
                modifierString = "#all";
            } else {
                if (modRestriction) {
                    modifierString = modifierString + "restriction ";
                }
                if (modExtension) {
                    modifierString = modifierString + "extension ";
                }
                if (modifierString.length() > 0) {
                    modifierString = modifierString.substring(0, modifierString.length() - 1);
                }
            }
        }
        typeNode.setAttribute(modType, modifierString);

    }

    /**
     * writes the content of a baseType and retrns the content Node
     * (simpleContent/complexContent).
     *
     * @param typeNode
     * @param cType
     * @return
     */
    protected static void writeContent(org.w3c.dom.Element typeNode, ComplexType cType, XSDSchema schema) {
        if (cType.getContent() instanceof SimpleContentType) {
            writeSimpleContent(typeNode, cType, schema);
        } else if (cType.getContent() instanceof ComplexContentType) {
            ComplexContentType cCont = (ComplexContentType) cType.getContent();
            writeComplexContent(typeNode, cCont, schema);
        }
    }

    /**
     * writes complexContent to the given complexType-Node.
     *
     * @param typeNode
     * @param cont
     * @param foundElements
     */
    protected static void writeComplexContent(org.w3c.dom.Element typeNode, ComplexContentType cont, XSDSchema schema) {
        // <complexContent
        // id = ID
        // mixed = Boolean
        // {any attributes with non-schema Namespace}...>
        // Content: (annotation?, (restriction | extension))
        // </complexContent>
        if (cont.getInheritance() == null) {
            // A complexType can have a content (particle) without any inheritance defined!
            if (cont.getMixed() == true) {
                typeNode.setAttribute("mixed", cont.getMixed().toString());
            }
            Particle particle = cont.getParticle();
            if ((particle instanceof Element) || 
            		((particle instanceof CountingPattern) && 
            				((CountingPattern) particle).getParticle() instanceof Element)) {
            	// element is not allowed at top-level, therefore it is wrapped in a sequence
            	SequencePattern sequencePattern = new SequencePattern();
            	sequencePattern.addParticle(particle);
            	particle = sequencePattern;
            }
            ParticleWriter.writeParticle(typeNode, particle, schema);
        } else {
            org.w3c.dom.Element contNode;
            contNode = typeNode.getOwnerDocument().createElementNS("http://www.w3.org/2001/XMLSchema", "complexContent");
            DOMHelper.setXSDPrefix(contNode, schema);
            AnnotationWriter.writeAnnotation(contNode, cont, schema);
            if (cont.getId() != null) {
                contNode.setAttribute("id", cont.getId());
            }
            if (cont.getMixed() == true) {
                contNode.setAttribute("mixed", cont.getMixed().toString());
            }
            if (cont.getInheritance() != null) {
                writeComplexContentInheritance(contNode, cont, schema);
            }

            typeNode.appendChild(contNode);
        }
    }

    /**
     * writes the Inheritance (extension / restriction) for the given
     * complexContent.
     *
     * @param contNode
     * @param cContType
     * @param foundElements
     */
    protected static void writeComplexContentInheritance(org.w3c.dom.Element contNode, ComplexContentType cCont, XSDSchema schema) {
    	ComplexContentInheritance inheritance = cCont.getInheritance();
        org.w3c.dom.Element childNode = null;
        if (inheritance instanceof ComplexContentExtension) {
            childNode = contNode.getOwnerDocument().createElementNS("http://www.w3.org/2001/XMLSchema", "extension");
        } else if (inheritance instanceof ComplexContentRestriction) {
            childNode = contNode.getOwnerDocument().createElementNS("http://www.w3.org/2001/XMLSchema", "restriction");
        }

        DOMHelper.setXSDPrefix(childNode, schema);
        AnnotationWriter.writeAnnotation(childNode, cCont, schema);
        QualifiedName baseType = cCont.getInheritance().getBaseType();
        if (baseType != null) {
            childNode.setAttribute("base", baseType.getQualifiedName());
        }
        if (cCont.getInheritance().getId() != null) {
            childNode.setAttribute("id", cCont.getInheritance().getId());
        }
        ParticleWriter.writeParticle(childNode, cCont.getParticle(), schema);
        if (cCont.getInheritance() != null && cCont.getInheritance().getAttributes() != null) {
            AttributeWriter.writeAttributeParticles(childNode, cCont.getInheritance().getAttributes(), schema);
        }
        
        contNode.appendChild(childNode);

    
    }




    /**
     * writes the simpleContent to the given comlexType-Node
     *
     * @param typeNode
     * @param cType
     * @param foundElements
     */
    protected static void writeSimpleContent(org.w3c.dom.Element typeNode, ComplexType cType, XSDSchema schema) {
        // <simpleContent
        // id = ID
        // {any attributes with non-schema Namespace}...>
        // Content: (annotation?, (restriction | extension))
        // </simpleContent>
        org.w3c.dom.Element contNode = null;
        SimpleContentInheritance inheritance;
        SimpleContentType scType;
        contNode = typeNode.getOwnerDocument().createElementNS("http://www.w3.org/2001/XMLSchema", "simpleContent");
        DOMHelper.setXSDPrefix(contNode, schema);
        scType = (SimpleContentType) cType.getContent();
        inheritance = scType.getInheritance();
        if (scType.getId() != null) {
            contNode.setAttribute("id", scType.getId());
        }
        if (inheritance instanceof SimpleContentRestriction) {
            SimpleContentRestriction sRestr = (SimpleContentRestriction) inheritance;
            writeSimpleContentRestriction(contNode, sRestr, schema);
        } else if (inheritance instanceof SimpleContentExtension) {
            SimpleContentExtension sExt = (SimpleContentExtension) inheritance;
            writeSimpleContentExtension(contNode, sExt, cType, schema);
        }
        AnnotationWriter.writeAnnotation(contNode, scType, schema);
        typeNode.appendChild(contNode);
    }

    /**
     * writes the simpleContent Extension.
     *
     * @param contNode
     * @param ext
     * @param cType
     * @param foundElements
     */
    protected static void writeSimpleContentExtension(org.w3c.dom.Element contNode, SimpleContentExtension ext, ComplexType cType, XSDSchema schema) {
//        <extension id = ID
//        base = QName>
//        Content: (annotation?, ((attribute | attributeGroup)*, anyAttribute?))
//        </extension>
        org.w3c.dom.Element extNode;
        extNode = contNode.getOwnerDocument().createElementNS("http://www.w3.org/2001/XMLSchema", "extension");
        DOMHelper.setXSDPrefix(extNode, schema);
        extNode.setAttribute("base", ext.getBaseType().getQualifiedName());
        if (ext.getId() != null) {
            extNode.setAttribute("id", ext.getId());
        }
        AttributeWriter.writeAttributeParticles(extNode, ext.getAttributes(), schema);
        AnnotationWriter.writeAnnotation(extNode, ext, schema);
        contNode.appendChild(extNode);
    }
}
