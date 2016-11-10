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

package eu.fox7.schematoolkit.converter.xsd2bonxai;

import eu.fox7.schematoolkit.bonxai.om.*;
import eu.fox7.schematoolkit.common.*;
import eu.fox7.schematoolkit.common.ElementRef;
import eu.fox7.schematoolkit.xsd.om.*;

class ParticleProcessor {
    private XSDSchema schema;
    private boolean addBonxaiTypes;
    
    /**
     * Creates a new ParticleProcessor.
     *
     * Particle processing relies on the given {@link SymbolTable}s for
     * GroupRef transformation. The given elementGroupSymbolTable must be the
     * one used in the target {@link Bonxai} instance.
     */
    public ParticleProcessor(XSDSchema schema, boolean addBonxaiTypes) {
        this.schema = schema;
        this.addBonxaiTypes = addBonxaiTypes;
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
            resultParticle = convertCountingPattern((CountingPattern) particle);
        } else if (particle instanceof AnyPattern) {
            resultParticle = convertAnyPattern((AnyPattern) particle);
        } else if (particle instanceof eu.fox7.schematoolkit.xsd.om.Element) {
            resultParticle = convertElement((eu.fox7.schematoolkit.xsd.om.Element) particle);
        } else if (particle instanceof ElementRef) {
            resultParticle = convertElementRef((ElementRef) particle);
        } else if (particle instanceof GroupReference) {
            resultParticle = convertGroupRef((GroupReference) particle);
        } else {
            throw new RuntimeException("Unknown Particle of class " + particle.getClass());
        }

        return resultParticle;
    }

    private Particle convertCountingPattern(CountingPattern countingPattern) {
        CountingPattern newCountingPattern = new CountingPattern(
            	convertParticle(countingPattern.getParticle()),
            	countingPattern.getMin(),
            	countingPattern.getMax()
            );
		return newCountingPattern;
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
    public eu.fox7.schematoolkit.bonxai.om.Element convertElement(eu.fox7.schematoolkit.xsd.om.Element xsdElement) {
    	QualifiedName typename = xsdElement.getTypeName();
    	Type type = schema.getType(typename);
    	BonxaiType bonxaiType = null;
    	
    	if (this.addBonxaiTypes && (type == null)) {
    		bonxaiType = new BonxaiType(typename);
    		bonxaiType.setFixedValue(xsdElement.getFixed());
    		bonxaiType.setDefaultValue(xsdElement.getDefault());
    		bonxaiType.setNillable(xsdElement.isNillable());
    	}
    	
    	eu.fox7.schematoolkit.bonxai.om.Element bonxaiElement = new eu.fox7.schematoolkit.bonxai.om.Element(
            xsdElement.getName(),
            bonxaiType
        );


        return bonxaiElement;
    }

    /**
     * Converts an ElementRef to an Element, since Bonxai does not know Element
     * references.
     */
    public Particle convertElementRef(ElementRef elementRef) {
    	if (elementRef.getElementName().getNamespaceURI().equals(schema.getDefaultNamespace().getUri())) {
    		eu.fox7.schematoolkit.xsd.om.Element element = schema.getElement(elementRef.getElementName());
        	return convertElement(element);
    	} else {
    		return new eu.fox7.schematoolkit.bonxai.om.ElementRef(elementRef.getElementName());
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
        anyPattern = new AnyPattern(anyPattern.getProcessContentsInstruction(), anyPattern.getNamespaces());
        return anyPattern;
    }
}
