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
package de.tudortmund.cs.bonxai.converter.xsd2bonxai;

import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import de.tudortmund.cs.bonxai.common.SymbolTable;
import de.tudortmund.cs.bonxai.common.SymbolTableFoundation;
import de.tudortmund.cs.bonxai.common.AnyAttribute;

// import de.tudortmund.cs.bonxai.xsd.Attribute;
import de.tudortmund.cs.bonxai.xsd.AttributeRef;
import de.tudortmund.cs.bonxai.xsd.AttributeGroupRef;
import de.tudortmund.cs.bonxai.xsd.AttributeUse;
import de.tudortmund.cs.bonxai.xsd.AttributeParticle;

// import de.tudortmund.cs.bonxai.bonxai.Attribute;
import de.tudortmund.cs.bonxai.bonxai.BonxaiType;
import de.tudortmund.cs.bonxai.bonxai.AttributeGroupReference;
import de.tudortmund.cs.bonxai.bonxai.AttributeGroupElement;
import de.tudortmund.cs.bonxai.bonxai.AttributePattern;
import de.tudortmund.cs.bonxai.bonxai.AttributeListElement;

/**
 * Processor class to convert Attributes from XSD to Bonxai.
 *
 * The methods in this class can be used to convert single {@link
 * AttributeParticle}s or a complete list of these to their corresponding Bonxai
 * representation. Note that to convert {@link AttributeGroupRef} (XSD) to
 * {@link AttributeGroupReference} (Bonxai), the {@link SymbolTable} instance
 * from the target Bonxai is needed, that is responsibe to store group
 * references.
 */
class AttributeProcessor {

    /**
     * Symbol table to resolve {@link AttributeGroupRef} in Bonxai.
     */
    private SymbolTableFoundation<AttributeGroupElement> symbolTable;

    /**
     * Creates a new AttributeProcessor.
     *
     * The processor will use the given {@link SymbolTable} to resolve {@link
     * AttributeRef}s inside attribute lists. The symbol table must the one to
     * be used in the generated Bonxai file. In case an {@link AttributeGroupRef}
     * is discovered inside {@linl convertAttributes()}, this table will be
     * used to add a corresponding reference in the returned Bonxai list.
     */
    public AttributeProcessor(SymbolTableFoundation<AttributeGroupElement> symbolTable) {
        this.symbolTable = symbolTable;
    }

    /**
     * Converts an XSD Attribute to an Bonxai Attribute.
     *
     * Will create a new Bonxai Attribute instance, which corresponds to the
     * given XSD Attribute, and return it.
     */
    public de.tudortmund.cs.bonxai.bonxai.Attribute convertAttribute(de.tudortmund.cs.bonxai.xsd.Attribute xsdAttribute) {
        BonxaiType bonxaiType;
        
        if(xsdAttribute.getSimpleType() == null){
            bonxaiType = new BonxaiType(xsdAttribute.getNamespace(), "anyType");
        }else{
            bonxaiType = new BonxaiType(xsdAttribute.getSimpleType().getNamespace(), xsdAttribute.getSimpleType().getLocalName());
        }
        return new de.tudortmund.cs.bonxai.bonxai.Attribute(
            xsdAttribute.getNamespace(),
            xsdAttribute.getLocalName(),
            bonxaiType,
            (xsdAttribute.getUse() == AttributeUse.Required),
            xsdAttribute.getDefault(),
            xsdAttribute.getFixed()
        );
    }

    /**
     * Converts a XSD AttributeRef to an Bonxai Attribute.
     *
     * Bonxai does not know AttributeRefs, so the reference target is simply
     * inlined. Note, that the "required" flag defined in the ref overwrites
     * the one potentially provided in the target.
     */
    public de.tudortmund.cs.bonxai.bonxai.Attribute convertAttributeRef(de.tudortmund.cs.bonxai.xsd.AttributeRef xsdAttributeRef) {
        if (xsdAttributeRef.getAttribute() == null) {
            throw new RuntimeException("AttributeRef with empty reference discovered.");
        }

        de.tudortmund.cs.bonxai.bonxai.Attribute bonxaiAttribute = convertAttribute(
            xsdAttributeRef.getAttribute()
        );

        if (xsdAttributeRef.getUse() == AttributeUse.Required) {
            bonxaiAttribute.setRequired();
        } else {
            bonxaiAttribute.setOptional();
        }

        return bonxaiAttribute;
    }

    /**
     * Converts a XSD AttributeGroupRef to the Bonxai correspondence
     * AttributeGroupReference.
     *
     * The {@link symbolTable} attribute received in the ctor is only needed by
     * this method. The reference to the XSD group is resolved and a new
     * reference is created in the {@link symbolTable} to be used in Bonxai.
     */
    public de.tudortmund.cs.bonxai.bonxai.AttributeGroupReference convertAttributeGroupRef(de.tudortmund.cs.bonxai.xsd.AttributeGroupRef xsdAttributeGroupRef) {
        if (symbolTable.hasReference(xsdAttributeGroupRef.getAttributeGroup().getLocalName())){
            return new de.tudortmund.cs.bonxai.bonxai.AttributeGroupReference(symbolTable.getReference( xsdAttributeGroupRef.getAttributeGroup().getLocalName()));
        }else{
            symbolTable.updateOrCreateReference( xsdAttributeGroupRef.getAttributeGroup().getLocalName(), new AttributeGroupElement(xsdAttributeGroupRef.getAttributeGroup().getLocalName(), null));
            return new de.tudortmund.cs.bonxai.bonxai.AttributeGroupReference(symbolTable.getReference( xsdAttributeGroupRef.getAttributeGroup().getLocalName()));
        }
    }

    /**
     * Converts a XSD Attribute pattern to an Bonxai attribute pattern.
     *
     * This method receives the list of AttributeParticles as contained in the
     * XSD {@link ComplexType} and {@link AttributeGroup} and converts them to
     * the corresponding Bonxai representation. Attributes, AttributeRefs,
     * AttributeGroupRefs and AnyAttribute values are converted in this step.
     */
    public de.tudortmund.cs.bonxai.bonxai.AttributePattern convertAttributes(
            List<AttributeParticle> attributeParticles
    ) {
        AttributePattern bonxaiAttrPattern = new AttributePattern();

        Vector<AttributeListElement> bonxaiAttrList = new Vector<AttributeListElement>();

        bonxaiAttrPattern.setAttributeList(bonxaiAttrList);

        for (AttributeParticle xsdAttrParticle : attributeParticles) {
            if (xsdAttrParticle instanceof de.tudortmund.cs.bonxai.xsd.Attribute)
            {
                bonxaiAttrList.add(
                    (AttributeListElement) convertAttribute(
                        (de.tudortmund.cs.bonxai.xsd.Attribute) xsdAttrParticle
                    )
                );
            } else if (xsdAttrParticle instanceof AttributeGroupRef) {
                bonxaiAttrList.add(
                    (AttributeListElement) convertAttributeGroupRef(
                        (AttributeGroupRef) xsdAttrParticle
                    )
                );
            } else if (xsdAttrParticle instanceof AttributeRef) {
                bonxaiAttrList.add(
                    (AttributeListElement) convertAttributeRef(
                        (AttributeRef) xsdAttrParticle
                    )
                );
            } else if (xsdAttrParticle instanceof AnyAttribute) {
                AnyAttribute anyAttribute = (AnyAttribute) xsdAttrParticle;
                // Replace all occurrences of " " in the namespace attribute with ",".
                String namespaceList = "";
                if (anyAttribute.getNamespace() == null) {
                    namespaceList = "any";
                } else if (anyAttribute.getNamespace().equals("##any")) {
                    namespaceList = "any";
                } else if (anyAttribute.getNamespace().equals("##other")) {
                    namespaceList = "other";
                } else {
                    String[] namespaceArray = anyAttribute.getNamespace().split(" ");
                    for (String namespace : namespaceArray) {
                        if (namespace.equals("##local")) {
                            namespace = "local";
                        } else if (namespace.equals("##targetNamespace")) {
                            namespace = "targetNamespace";
                        } else {
                            namespaceList = "any";
                        }
                        if (!namespaceList.equals("any")) {
                            if (namespaceList.equals("")) {
                                namespaceList = namespace;
                            } else {
                                namespaceList = "," + namespace;
                            }
                        }
                    }
                }
                AnyAttribute resultAnyAttribute = new AnyAttribute(anyAttribute.getProcessContentsInstruction(), namespaceList);
                bonxaiAttrPattern.setAnyAttribute(resultAnyAttribute);
            } else {
                // This should never happen!
                throw new RuntimeException("Unexpected object of type " + xsdAttrParticle.getClass() + " in attribute list.");

            }
        }
        return bonxaiAttrPattern;
    }
}
