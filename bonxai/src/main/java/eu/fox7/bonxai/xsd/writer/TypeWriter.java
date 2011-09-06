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
    protected static void writeType(Node root, Type type, FoundElements foundElements, boolean writeName) {
        org.w3c.dom.Element rootNode;
//        if (foundElements.containsType(baseType) && root.getNodeName().contains("element")) {
        if (!type.isAnonymous() && root.getNodeName().contains("element")) {
            rootNode = (org.w3c.dom.Element) root;
            rootNode.setAttribute("type", foundElements.getPrefix(type.getNamespace()) + type.getLocalName());
        } else if (type instanceof SimpleType) {
            TypeWriter.writeSimpleType(root, (SimpleType) type, foundElements, writeName);
        } else if (type instanceof ComplexType) {
            writeComplexType(root, (ComplexType) type, foundElements, writeName);
        }
    }

    /**
     * Writes the SimpleType node and iterates through the contained
     * types/elements and calls the write-Methods recursively.
     *
     * <baseType final = (#all | (list | union | restriction)) name = NCName>
     * Content: (annotation?, (restriction | list | union)) </baseType>
     */
    protected static void writeSimpleType(Node root, SimpleType type, FoundElements foundElements, boolean writeName) {
        org.w3c.dom.Element sTypeNode;
        foundElements.addType(type);
        sTypeNode = root.getOwnerDocument().createElementNS("http://www.w3.org/2001/XMLSchema", "simpleType");
        foundElements.setXSDPrefix(sTypeNode);
        AnnotationWriter.writeAnnotation(sTypeNode, type, foundElements);
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
            sTypeNode.setAttribute("name", type.getLocalName());
        }
        if (type.getId() != null) {
            sTypeNode.setAttribute("id", type.getId());
        }

        root.appendChild(sTypeNode);
        writeSimpleTypeInheritance(sTypeNode, type, foundElements);
    }

    /**
     * Writes the ComplexType node.
     *
     * @param root
     * @param baseType
     */
    protected static void writeComplexType(Node root, ComplexType type, FoundElements foundElements, boolean writeName) {
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

        foundElements.addType(type);
        cTypeNode = root.getOwnerDocument().createElementNS("http://www.w3.org/2001/XMLSchema", "complexType");
        foundElements.setXSDPrefix(cTypeNode);
        if (writeName) {
            cTypeNode.setAttribute("name", type.getLocalName());
        }
        AnnotationWriter.writeAnnotation(cTypeNode, type, foundElements);
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
            writeContent(cTypeNode, type, foundElements);
        }
        /*
         * when a inheritance is defined attributes are written only in
         * extension/restriction node
         */
        if (type.getAttributes() != null && (isInheritanceNull || type.getContent() == null)) {
            AttributeWriter.writeAttributeParticles(cTypeNode, type.getAttributes(), foundElements);
        }
    }

    /**
     * Writes the nodes for the baseType Inheritance.
     *
     * @param sTypeNode
     * @param baseType
     */
    protected static void writeSimpleTypeInheritance(Node sTypeNode, SimpleType type, FoundElements foundElements) {
        SimpleContentList contentlist;
        SimpleContentUnion contentUnion;
        SimpleContentRestriction contentRestrict;
        if (type.getInheritance() != null) {
            if (type.getInheritance() instanceof SimpleContentList) {
                contentlist = (SimpleContentList) type.getInheritance();
                TypeWriter.writeSimpContList(sTypeNode, contentlist, foundElements);
            } else if (type.getInheritance() instanceof SimpleContentUnion) {
                contentUnion = (SimpleContentUnion) type.getInheritance();
                TypeWriter.writeSimpContUnion(sTypeNode, contentUnion, foundElements);
            } else if (type.getInheritance() instanceof SimpleContentRestriction) {
                contentRestrict = (SimpleContentRestriction) type.getInheritance();
                TypeWriter.writeSimpleContentRestriction(sTypeNode, contentRestrict, foundElements);
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
    protected static void writeSimpContList(Node sTypeNode, SimpleContentList contentlist, FoundElements foundElements) {
        // <list
        // id = ID
        // itemType = QName
        // {any attributes with non-schema Namespace}...>
        // Content: (annotation?, (baseType?))
        // </list>
        org.w3c.dom.Element listElement;
        SimpleType simpleType = null;
        listElement = sTypeNode.getOwnerDocument().createElementNS("http://www.w3.org/2001/XMLSchema", "list");
        foundElements.setXSDPrefix(listElement);
        AnnotationWriter.writeAnnotation(listElement, contentlist, foundElements);
        simpleType = (SimpleType) contentlist.getBase();
        
        sTypeNode.appendChild(listElement);
        /*
         * Types which were defined outside of the simple content union
         * are listed in the member types attribute, otherwise their
         * definition is appended to the simple content union
         */
        if (simpleType.isAnonymous()) {
            writeSimpleType(listElement, simpleType, foundElements, false);
        } else {
            listElement.setAttribute("itemType", foundElements.getPrefix(simpleType.getNamespace()) + simpleType.getLocalName());
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
    protected static void writeSimpContUnion(Node sTypeNode, SimpleContentUnion contentUnion, FoundElements foundElements) {
        // <union
        // id = ID
        // memberTypes = List of QNames
        // {any attributes with non-schema Namespace}...>
        // Content: (annotation?, (baseType*))
        // </union>
        org.w3c.dom.Element unionElement;
        LinkedList<eu.fox7.bonxai.common.SymbolTableRef<Type>> types;
        LinkedList<eu.fox7.bonxai.common.SymbolTableRef<Type>> memberTypes;
        StringBuilder memberTypesString;
        unionElement = sTypeNode.getOwnerDocument().createElementNS("http://www.w3.org/2001/XMLSchema", "union");
        foundElements.setXSDPrefix(unionElement);
        AnnotationWriter.writeAnnotation(unionElement, contentUnion, foundElements);
        types = contentUnion.getAnonymousSimpleTypes();
        memberTypes = contentUnion.getMemberTypes();
        
        sTypeNode.appendChild(unionElement);
        memberTypesString = new StringBuilder();
        for (eu.fox7.bonxai.common.SymbolTableRef<Type> t : types) {
            writeSimpleType(unionElement, (SimpleType) t.getReference(), foundElements, false);
        }
        for (eu.fox7.bonxai.common.SymbolTableRef<Type> currentMemberType : memberTypes) {
            memberTypesString.append(foundElements.getPrefix(currentMemberType.getReference().getNamespace()) + currentMemberType.getReference().getLocalName() + " ");
        }

        if (memberTypesString.length() > 0) {
            memberTypesString = new StringBuilder(memberTypesString.substring(0, memberTypesString.length() - 1));
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
            SimpleContentRestriction contentRestrict, FoundElements foundElements) {
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
        foundElements.setXSDPrefix(restrElement);
                AnnotationWriter.writeAnnotation(restrElement, contentRestrict, foundElements);
        baseType = contentRestrict.getBase();

        if (baseType instanceof SimpleType) {
            if (baseType.isAnonymous()) {
                writeSimpleType(restrElement, (SimpleType) baseType, foundElements, false);
            } else {
                restrElement.setAttribute("base", foundElements.getPrefix(contentRestrict.getBase().getNamespace()) + contentRestrict.getBase().getLocalName());
            }
        } else if (baseType instanceof ComplexType) {
            restrElement.setAttribute("base", foundElements.getPrefix(contentRestrict.getBase().getNamespace()) + contentRestrict.getBase().getLocalName());
            if (contentRestrict.getAnonymousSimpleType() != null) {
                SimpleType anonymousSimpleType = contentRestrict.getAnonymousSimpleType();
                writeSimpleType(restrElement, anonymousSimpleType, foundElements, false);
            }
        }

        if (contentRestrict.getId() != null) {
            restrElement.setAttribute("id", contentRestrict.getId());
        }
        if (contentRestrict.getMinExclusive() != null) {
            tmpElement = root.getOwnerDocument().createElementNS("http://www.w3.org/2001/XMLSchema", "minExclusive");
            foundElements.setXSDPrefix(tmpElement);
            tmpElement.setAttribute("value", contentRestrict.getMinExclusive().getValue());
            if (contentRestrict.getMinExclusive().getFixed() == true) {
                tmpElement.setAttribute("fixed", contentRestrict.getMinExclusive().getFixed().toString());
            }
            restrElement.appendChild(tmpElement);
        }
        if (contentRestrict.getMinInclusive() != null) {
            tmpElement = root.getOwnerDocument().createElementNS("http://www.w3.org/2001/XMLSchema", "minInclusive");
            foundElements.setXSDPrefix(tmpElement);
            tmpElement.setAttribute("value", contentRestrict.getMinInclusive().getValue());
            if (contentRestrict.getMinInclusive().getFixed() == true) {
                tmpElement.setAttribute("fixed", contentRestrict.getMinInclusive().getFixed().toString());
            }
            restrElement.appendChild(tmpElement);
        }
        if (contentRestrict.getMaxExclusive() != null) {
            tmpElement = root.getOwnerDocument().createElementNS("http://www.w3.org/2001/XMLSchema", "maxExclusive");
            foundElements.setXSDPrefix(tmpElement);
            tmpElement.setAttribute("value", contentRestrict.getMaxExclusive().getValue());
            if (contentRestrict.getMaxExclusive().getFixed() == true) {
                tmpElement.setAttribute("fixed", contentRestrict.getMaxExclusive().getFixed().toString());
            }
            restrElement.appendChild(tmpElement);
        }
        if (contentRestrict.getMaxInclusive() != null) {
            tmpElement = root.getOwnerDocument().createElementNS("http://www.w3.org/2001/XMLSchema", "maxInclusive");
            foundElements.setXSDPrefix(tmpElement);
            tmpElement.setAttribute("value", contentRestrict.getMaxInclusive().getValue());
            if (contentRestrict.getMaxInclusive().getFixed() == true) {
                tmpElement.setAttribute("fixed", contentRestrict.getMaxInclusive().getFixed().toString());
            }
            restrElement.appendChild(tmpElement);
        }
        if (contentRestrict.getTotalDigits() != null) {
            tmpElement = root.getOwnerDocument().createElementNS("http://www.w3.org/2001/XMLSchema", "totalDigits");
            foundElements.setXSDPrefix(tmpElement);
            tmpElement.setAttribute("value", contentRestrict.getTotalDigits().getValue().toString());
            if (contentRestrict.getTotalDigits().getFixed() == true) {
                tmpElement.setAttribute("fixed", contentRestrict.getTotalDigits().getFixed().toString());
            }
            restrElement.appendChild(tmpElement);
        }
        if (contentRestrict.getFractionDigits() != null) {
            tmpElement = root.getOwnerDocument().createElementNS("http://www.w3.org/2001/XMLSchema", "fractionDigits");
            foundElements.setXSDPrefix(tmpElement);
            tmpElement.setAttribute("value", contentRestrict.getFractionDigits().getValue().toString());
            if (contentRestrict.getFractionDigits().getFixed() == true) {
                tmpElement.setAttribute("fixed", contentRestrict.getFractionDigits().getFixed().toString());
            }
            restrElement.appendChild(tmpElement);
        }
        if (contentRestrict.getLength() != null) {
            tmpElement = root.getOwnerDocument().createElementNS("http://www.w3.org/2001/XMLSchema", "length");
            foundElements.setXSDPrefix(tmpElement);
            tmpElement.setAttribute("value", contentRestrict.getLength().getValue().toString());
            if (contentRestrict.getLength().getFixed() == true) {
                tmpElement.setAttribute("fixed", contentRestrict.getLength().getFixed().toString());
            }
            restrElement.appendChild(tmpElement);
        }
        if (contentRestrict.getMinLength() != null) {
            tmpElement = root.getOwnerDocument().createElementNS("http://www.w3.org/2001/XMLSchema", "minLength");
            foundElements.setXSDPrefix(tmpElement);
            tmpElement.setAttribute("value", contentRestrict.getMinLength().getValue().toString());
            if (contentRestrict.getMinLength().getFixed() == true) {
                tmpElement.setAttribute("fixed", contentRestrict.getMinLength().getFixed().toString());
            }
            restrElement.appendChild(tmpElement);
        }
        if (contentRestrict.getMaxLength() != null) {
            tmpElement = root.getOwnerDocument().createElementNS("http://www.w3.org/2001/XMLSchema", "maxLength");
            foundElements.setXSDPrefix(tmpElement);
            tmpElement.setAttribute("value", contentRestrict.getMaxLength().getValue().toString());
            if (contentRestrict.getMaxLength().getFixed() == true) {
                tmpElement.setAttribute("fixed", contentRestrict.getMaxLength().getFixed().toString());
            }
            restrElement.appendChild(tmpElement);
        }
        if (contentRestrict.getEnumeration() != null) {
            for (String s : contentRestrict.getEnumeration()) {
                tmpElement = root.getOwnerDocument().createElementNS("http://www.w3.org/2001/XMLSchema", "enumeration");
                foundElements.setXSDPrefix(tmpElement);
                tmpElement.setAttribute("value", s);
                restrElement.appendChild(tmpElement);
            }
        }
        if (contentRestrict.getWhitespace() != null) {
            String value = "";
            tmpElement = root.getOwnerDocument().createElementNS("http://www.w3.org/2001/XMLSchema", "whiteSpace");
            foundElements.setXSDPrefix(tmpElement);
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
            foundElements.setXSDPrefix(tmpElement);
            tmpElement.setAttribute("value", contentRestrict.getPattern().getValue());
            restrElement.appendChild(tmpElement);
        }
        AttributeWriter.writeAttributeParticles(restrElement, contentRestrict.getAttributes(), foundElements);

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
    protected static void writeContent(org.w3c.dom.Element typeNode, ComplexType cType, FoundElements foundElements) {
        if (cType.getContent() instanceof SimpleContentType) {
            writeSimpleContent(typeNode, cType, foundElements);
        } else if (cType.getContent() instanceof ComplexContentType) {
            ComplexContentType cCont = (ComplexContentType) cType.getContent();
            writeComplexContent(typeNode, cCont, foundElements);
        }
    }

    /**
     * writes complexContent to the given complexType-Node.
     *
     * @param typeNode
     * @param cont
     * @param foundElements
     */
    protected static void writeComplexContent(org.w3c.dom.Element typeNode, ComplexContentType cont, FoundElements foundElements) {
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
            				((CountingPattern) particle).getParticles().getFirst() instanceof Element)) {
            	// element is not allowed at top-level, therefore it is wrapped in a sequence
            	SequencePattern sequencePattern = new SequencePattern();
            	sequencePattern.addParticle(cont.getParticle());
            	particle = sequencePattern;
            }
            ParticleWriter.writeParticle(typeNode, particle, foundElements);
        } else {
            org.w3c.dom.Element contNode;
            contNode = typeNode.getOwnerDocument().createElementNS("http://www.w3.org/2001/XMLSchema", "complexContent");
            foundElements.setXSDPrefix(contNode);
            AnnotationWriter.writeAnnotation(contNode, cont, foundElements);
            if (cont.getId() != null) {
                contNode.setAttribute("id", cont.getId());
            }
            if (cont.getMixed() == true) {
                contNode.setAttribute("mixed", cont.getMixed().toString());
            }
            if (cont.getInheritance() != null) {
                writeComplexContentInheritance(contNode, cont, foundElements);
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
    protected static void writeComplexContentInheritance(org.w3c.dom.Element contNode, ComplexContentType cCont, FoundElements foundElements) {
        ComplexContentInheritance inheritance = cCont.getInheritance();
        if (inheritance instanceof ComplexContentExtension) {
            writeComplexContentExtension(contNode, cCont, foundElements);
        } else if (inheritance instanceof ComplexContentRestriction) {
            writeComplexContentRestriction(contNode, cCont, foundElements);
        }
    }

    /**
     * writes the complexContent-Restriction
     *
     * @param contNode
     * @param rest
     */
    protected static void writeComplexContentRestriction(org.w3c.dom.Element contNode, ComplexContentType cContent, FoundElements foundElements) {
        // <restriction
        // base = QName
        // id = ID
        // {any attributes with non-schema Namespace}...>
        // Content: (annotation?, (group | all | choice | sequence)?,
        // ((attribute |
        // attributeGroup)*, anyAttribute?))
        // </restriction>
        org.w3c.dom.Element restNode;
        Type baseType;
        restNode = contNode.getOwnerDocument().createElementNS("http://www.w3.org/2001/XMLSchema", "restriction");
        foundElements.setXSDPrefix(restNode);
        AnnotationWriter.writeAnnotation(restNode, cContent, foundElements);
        baseType = cContent.getInheritance().getBase();
        if (baseType != null) {
            restNode.setAttribute("base", foundElements.getPrefix(baseType.getNamespace()) + baseType.getLocalName());
        }
        if (cContent.getInheritance().getId() != null) {
            restNode.setAttribute("id", cContent.getInheritance().getId());
        }
        ParticleWriter.writeParticle(restNode, cContent.getParticle(), foundElements);
        if (cContent.getInheritance() != null && cContent.getInheritance().getAttributes() != null) {
            AttributeWriter.writeAttributeParticles(restNode, cContent.getInheritance().getAttributes(), foundElements);
        }
        
        contNode.appendChild(restNode);
    }

    /**
     * writes the complexContent Extension.
     *
     * @param contNode
     * @param cContent
     */
    protected static void writeComplexContentExtension(org.w3c.dom.Element contNode, ComplexContentType cContent, FoundElements foundElements) {
        org.w3c.dom.Element extNode;
        Type baseType;
        extNode = contNode.getOwnerDocument().createElementNS("http://www.w3.org/2001/XMLSchema", "extension");
        foundElements.setXSDPrefix(extNode);
        AnnotationWriter.writeAnnotation(extNode, cContent, foundElements);
        baseType = cContent.getInheritance().getBase();
        if (baseType != null) {
            extNode.setAttribute("base", foundElements.getPrefix(baseType.getNamespace()) + baseType.getLocalName());
        }
        if (cContent.getInheritance().getId() != null) {
            extNode.setAttribute("id", cContent.getInheritance().getId());
        }
        ParticleWriter.writeParticle(extNode, cContent.getParticle(), foundElements);
        if (cContent.getInheritance() != null && cContent.getInheritance().getAttributes() != null) {
            AttributeWriter.writeAttributeParticles(extNode, cContent.getInheritance().getAttributes(), foundElements);
        }
        
        contNode.appendChild(extNode);
    }

    /**
     * writes the simpleContent to the given comlexType-Node
     *
     * @param typeNode
     * @param cType
     * @param foundElements
     */
    protected static void writeSimpleContent(org.w3c.dom.Element typeNode, ComplexType cType, FoundElements foundElements) {
        // <simpleContent
        // id = ID
        // {any attributes with non-schema Namespace}...>
        // Content: (annotation?, (restriction | extension))
        // </simpleContent>
        org.w3c.dom.Element contNode = null;
        SimpleContentInheritance inheritance;
        SimpleContentType scType;
        contNode = typeNode.getOwnerDocument().createElementNS("http://www.w3.org/2001/XMLSchema", "simpleContent");
        foundElements.setXSDPrefix(contNode);
        scType = (SimpleContentType) cType.getContent();
        inheritance = scType.getInheritance();
        if (scType.getId() != null) {
            contNode.setAttribute("id", scType.getId());
        }
        if (inheritance instanceof SimpleContentRestriction) {
            SimpleContentRestriction sRestr = (SimpleContentRestriction) inheritance;
            writeSimpleContentRestriction(contNode, sRestr, foundElements);
        } else if (inheritance instanceof SimpleContentExtension) {
            SimpleContentExtension sExt = (SimpleContentExtension) inheritance;
            writeSimpleContentExtension(contNode, sExt, cType, foundElements);
        }
        AnnotationWriter.writeAnnotation(contNode, scType, foundElements);
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
    protected static void writeSimpleContentExtension(org.w3c.dom.Element contNode, SimpleContentExtension ext, ComplexType cType, FoundElements foundElements) {
//        <extension id = ID
//        base = QName>
//        Content: (annotation?, ((attribute | attributeGroup)*, anyAttribute?))
//        </extension>
        org.w3c.dom.Element extNode;
        extNode = contNode.getOwnerDocument().createElementNS("http://www.w3.org/2001/XMLSchema", "extension");
        foundElements.setXSDPrefix(extNode);
        extNode.setAttribute("base", foundElements.getPrefix(ext.getBase().getNamespace()) + ext.getBase().getLocalName());
        if (ext.getId() != null) {
            extNode.setAttribute("id", ext.getId());
        }
        AttributeWriter.writeAttributeParticles(extNode, ext.getAttributes(), foundElements);
        AnnotationWriter.writeAnnotation(extNode, ext, foundElements);
        contNode.appendChild(extNode);
    }
}
