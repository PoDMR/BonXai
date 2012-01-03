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

import eu.fox7.bonxai.bonxai.*;
import eu.fox7.bonxai.common.*;
import eu.fox7.bonxai.xsd.*;

class ParticleProcessor {
    private XSDSchema schema;
    
    /**
     * Creates a new ParticleProcessor.
     *
     * Particle processing relies on the given {@link SymbolTable}s for
     * GroupRef transformation. The given elementGroupSymbolTable must be the
     * one used in the target {@link Bonxai} instance.
     */
    public ParticleProcessor(XSDSchema schema) {
        this.schema = schema;
    }


    /**
     * Dispatches to the conversion of the given particle to the corresponding
     * methods and returns the result.
     */
    public Particle convertParticle(Particle particle) {
        Particle resultParticle;

        if (particle instanceof ParticleContainer) {
            resultParticle = convertParticleContainer((ParticleContainer) particle);
        } else if (particle instanceof CountingPattern) {
            resultParticle = new CountingPattern(
            	((CountingPattern) particle).getParticle(),
                ((CountingPattern) particle).getMin(),
                ((CountingPattern) particle).getMax()
            );
        } else if (particle instanceof AnyPattern) {
            resultParticle = convertAnyPattern((AnyPattern) particle);
        } else if (particle instanceof eu.fox7.bonxai.xsd.Element) {
            resultParticle = convertElement((eu.fox7.bonxai.xsd.Element) particle);
        } else if (particle instanceof ElementRef) {
            resultParticle = convertElementRef((ElementRef) particle);
        } else if (particle instanceof GroupReference) {
            resultParticle = convertGroupRef((GroupReference) particle);
        } else {
            throw new RuntimeException("Unknown Particle of class " + particle.getClass());
        }

        return resultParticle;
    }

    /**
     * Converts particleContainer, depending on its realization.
     *
     * There are no dedicated methods for the different ParticleContainers,
     * since they do not need to be converted themselves but only the contained
     * Particles.
     */
    public ParticleContainer convertParticleContainer(ParticleContainer particleContainer) {
        ParticleContainer result;

        if (particleContainer instanceof AllPattern) {

            result = new AllPattern();

        } else if (particleContainer instanceof ChoicePattern) {

            result = new ChoicePattern();

        } else if (particleContainer instanceof SequencePattern) {

            result = new SequencePattern();

        } else {
            throw new RuntimeException("Unknown ParticleContainer of class " + particleContainer.getClass());
        }

        for (Particle nestedParticle : particleContainer.getParticles()) {
            result.addParticle(convertParticle(nestedParticle));
        }

        return result;
    }

    /**
     * Converts a single Element.
     */
    public eu.fox7.bonxai.bonxai.Element convertElement(eu.fox7.bonxai.xsd.Element xsdElement) {
    	// TODO: decide whether to produce a BonxaiType a BonxaiSimpleType or just a plain Element
    	QualifiedName typename = xsdElement.getTypeName();
    	Type type = schema.getType(typename);
    	BonxaiType bonxaiType = null;
    	
    	if (type == null) {
    		bonxaiType = new BonxaiType(typename);
    		bonxaiType.setFixedValue(xsdElement.getFixed());
    		bonxaiType.setDefaultValue(xsdElement.getDefault());
    	}
    	
    	eu.fox7.bonxai.bonxai.Element bonxaiElement = new eu.fox7.bonxai.bonxai.Element(
            xsdElement.getName(),
            bonxaiType,
            xsdElement.isNillable()
        );


        return bonxaiElement;
    }

    /**
     * Converts an ElementRef to an Element, since Bonxai does not know Element
     * references.
     */
    public Particle convertElementRef(ElementRef elementRef) {
    	if (elementRef.getElementName().getNamespace().equals(schema.getDefaultNamespace())) {
    		eu.fox7.bonxai.xsd.Element element = schema.getElement(elementRef.getElementName());
        	return convertElement(element);
    	} else {
    		return new ElementRef(elementRef.getElementName());
    	}

    	
    }

    /**
     * Converts a GroupRef.
     *
     * Attention, this method receives the {@link SymbolTableRef} for the group
     * to reference from the {@link SymbolTable} given in the ctor. It is
     * neccessary that this is the SymbolTable used in the target Bonxai for
     * group management!
     */
    public GroupReference convertGroupRef(GroupReference groupRef) {
    	return new GroupReference(groupRef.getName());
    }

    /**
     * Converts the AnyPattern.
     */
    public AnyPattern convertAnyPattern(AnyPattern anyPattern) {
        // Replace all occurrences of " " in the namespace attribute with ",".
        String namespaceList = "";
        if (anyPattern.getNamespace() == null) {
            namespaceList = "any";
        } else if (anyPattern.getNamespace().equals("##any")) {
            namespaceList = "any";
        } else if (anyPattern.getNamespace().equals("##other")) {
            namespaceList = "other";
        } else {
            String[] namespaceArray = anyPattern.getNamespace().split(" ");
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
        anyPattern = new AnyPattern(anyPattern.getProcessContentsInstruction(), namespaceList);
        return anyPattern;
    }
}
