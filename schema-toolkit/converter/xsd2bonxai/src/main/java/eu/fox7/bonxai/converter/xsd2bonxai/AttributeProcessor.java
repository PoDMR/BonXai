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
package eu.fox7.bonxai.converter.xsd2bonxai;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import eu.fox7.bonxai.bonxai.Attribute;
import eu.fox7.bonxai.bonxai.AttributePattern;
import eu.fox7.bonxai.bonxai.BonxaiType;
import eu.fox7.bonxai.common.AnyAttribute;
import eu.fox7.bonxai.common.AttributeGroupReference;
import eu.fox7.bonxai.common.AttributeParticle;
import eu.fox7.bonxai.common.QualifiedName;
import eu.fox7.bonxai.xsd.AttributeRef;
import eu.fox7.bonxai.xsd.AttributeUse;
import eu.fox7.bonxai.xsd.XSDSchema;

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
	private XSDSchema schema;
	
    /**
     * Creates a new AttributeProcessor.
     *
     * The processor will use the given {@link SymbolTable} to resolve {@link
     * AttributeRef}s inside attribute lists. The symbol table must the one to
     * be used in the generated Bonxai file. In case an {@link AttributeGroupRef}
     * is discovered inside {@linl convertAttributes()}, this table will be
     * used to add a corresponding reference in the returned Bonxai list.
     */
    public AttributeProcessor(XSDSchema schema) {
    	this.schema = schema;
    }

    /**
     * Converts an XSD Attribute to an Bonxai Attribute.
     *
     * Will create a new Bonxai Attribute instance, which corresponds to the
     * given XSD Attribute, and return it.
     */
    public eu.fox7.bonxai.bonxai.Attribute convertAttribute(eu.fox7.bonxai.xsd.Attribute xsdAttribute) {
        BonxaiType bonxaiType;
        
        if(xsdAttribute.getSimpleTypeName() == null){
            bonxaiType = new BonxaiType(new QualifiedName(xsdAttribute.getName().getNamespace(), "anyType"));
        }else{
            bonxaiType = new BonxaiType(xsdAttribute.getSimpleTypeName());
            bonxaiType.setDefaultValue(xsdAttribute.getDefault());
            bonxaiType.setFixedValue(xsdAttribute.getFixed());
        }
        return new eu.fox7.bonxai.bonxai.Attribute(
            xsdAttribute.getName(),
            bonxaiType,
            (xsdAttribute.getUse() == AttributeUse.Required)
        );
    }

    /**
     * Converts a XSD AttributeRef to an Bonxai Attribute.
     *
     * Bonxai does not know AttributeRefs, so the reference target is simply
     * inlined. Note, that the "required" flag defined in the ref overwrites
     * the one potentially provided in the target.
     */
    public AttributeParticle convertAttributeRef(eu.fox7.bonxai.xsd.AttributeRef xsdAttributeRef) {
        AttributeParticle particle;
    	if (this.schema.getDefaultNamespace().equals(xsdAttributeRef.getAttributeName().getNamespace())) {
        	eu.fox7.bonxai.xsd.Attribute xsdAttribute = schema.getAttribute(xsdAttributeRef.getAttributeName());
    		BonxaiType type = new BonxaiType(xsdAttribute.getSimpleTypeName());
        	Attribute bonxaiAttribute = new Attribute(xsdAttribute.getName(), type);
            if (xsdAttributeRef.getUse() == AttributeUse.Required) {
                bonxaiAttribute.setRequired();
            } else {
                bonxaiAttribute.setOptional();
            }
            particle = bonxaiAttribute;
        } else {
        	//TODO: AttributeReference
        	particle=null;
        }
        return particle;
    }

    /**
     * Converts a XSD AttributeGroupRef to the Bonxai correspondence
     * AttributeGroupReference.
     *
     * The {@link symbolTable} attribute received in the ctor is only needed by
     * this method. The reference to the XSD group is resolved and a new
     * reference is created in the {@link symbolTable} to be used in Bonxai.
     */
    public AttributeGroupReference convertAttributeGroupRef(AttributeGroupReference xsdAttributeGroupRef) {
    	return xsdAttributeGroupRef;
    }

    /**
     * Converts a XSD Attribute pattern to an Bonxai attribute pattern.
     *
     * This method receives the list of AttributeParticles as contained in the
     * XSD {@link ComplexType} and {@link AttributeGroup} and converts them to
     * the corresponding Bonxai representation. Attributes, AttributeRefs,
     * AttributeGroupRefs and AnyAttribute values are converted in this step.
     */
    public eu.fox7.bonxai.bonxai.AttributePattern convertAttributes(
            List<AttributeParticle> attributeParticles
    ) {
        AttributePattern bonxaiAttrPattern = new AttributePattern();

        Collection<AttributeParticle> bonxaiAttrList = new LinkedList<AttributeParticle>();

        bonxaiAttrPattern.setAttributeList(bonxaiAttrList);

        for (AttributeParticle xsdAttrParticle : attributeParticles) {
            if (xsdAttrParticle instanceof eu.fox7.bonxai.xsd.Attribute)
            {
                bonxaiAttrList.add(
                    convertAttribute(
                        (eu.fox7.bonxai.xsd.Attribute) xsdAttrParticle
                    )
                );
            } else if (xsdAttrParticle instanceof AttributeGroupReference) {
                bonxaiAttrList.add(
                    convertAttributeGroupRef((AttributeGroupReference) xsdAttrParticle)
                );
            } else if (xsdAttrParticle instanceof AttributeRef) {
                bonxaiAttrList.add(
                    convertAttributeRef(
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