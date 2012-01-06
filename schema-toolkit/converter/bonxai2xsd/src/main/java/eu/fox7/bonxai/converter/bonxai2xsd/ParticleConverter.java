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
package eu.fox7.bonxai.converter.bonxai2xsd;


import java.util.LinkedList;

import eu.fox7.bonxai.typeautomaton.TypeAutomaton;
import eu.fox7.flt.automata.NotDFAException;
import eu.fox7.flt.automata.impl.sparse.State;
import eu.fox7.flt.automata.impl.sparse.Symbol;
import eu.fox7.schematoolkit.bonxai.om.*;
import eu.fox7.schematoolkit.common.*;
import eu.fox7.schematoolkit.xsd.om.*;

/**
 * Bonxai2XSDConverter for group declarations inside an Bonxai schema.
 */
public class ParticleConverter {

	private TypeAutomaton typeAutomaton;
	
	public ParticleConverter(TypeAutomaton typeAutomaton) {
		this.typeAutomaton = typeAutomaton;
	}
	
    /**
     * Convert a particle.
     *
     * Converts a single particle, or element pattern, to XSD.
     *
     * @return particle
     */
    public Particle convertParticle(DefaultNamespace namespace, Particle particle, State sourceState) {
        if ((particle instanceof eu.fox7.schematoolkit.bonxai.om.Element)
                && (((eu.fox7.schematoolkit.bonxai.om.Element) particle).getName().getNamespace().equals(namespace))) {
            eu.fox7.schematoolkit.bonxai.om.Element source = (eu.fox7.schematoolkit.bonxai.om.Element) particle;

            eu.fox7.schematoolkit.xsd.om.Element element = new eu.fox7.schematoolkit.xsd.om.Element(source.getName());

            if (source.getType() != null) {
            	BonxaiType type = source.getType();
                if (type.getDefaultValue() != null) {
                    element.setDefault(type.getDefaultValue());
                }

                if (type.getFixedValue() != null) {
                    element.setFixed(type.getFixedValue());
                }

                if (source.isMissing()) {
                    element.setNillable();
                }

                element.setTypeName(source.getType().getTypename());
            } else {
            	State toState;
				try {
					toState = typeAutomaton.getNextState(Symbol.create(source.getName().getFullyQualifiedName()), sourceState);
					QualifiedName typename = typeAutomaton.getType(toState).getName();
					element.setTypeName(typename);
					ElementProperties elementProperties = typeAutomaton.getElementProperties(toState);
					if (elementProperties != null)
						element.setProperties(elementProperties);
				} catch (NotDFAException e) {
					throw new RuntimeException(e);
				}
            }

            return element;
        } else if (particle instanceof eu.fox7.schematoolkit.common.AnyPattern) {
            // Replace all occurrences of "," in the namespace attribute with " " spaces.
            AnyPattern anyPattern = (eu.fox7.schematoolkit.common.AnyPattern) particle;
            particle = new AnyPattern(anyPattern.getProcessContentsInstruction(), anyPattern.getNamespaces());
            return particle;
        } else if (particle instanceof eu.fox7.schematoolkit.bonxai.om.Element) {
            // For elements in foreign namespaces we just create an element
            // reference
            return new ElementRef(((eu.fox7.schematoolkit.bonxai.om.Element) particle).getName());
        } else if (particle instanceof GroupReference) {
                return new GroupReference(((GroupReference) particle).getName());
        } else if (particle instanceof ParticleContainer) {
            ParticleContainer container = null;
            if (particle instanceof SequencePattern) {
                container = new SequencePattern();
            } else if (particle instanceof ChoicePattern) {
                container = new ChoicePattern();
            } else if (particle instanceof AllPattern) {
                container = new AllPattern();
            } 

            /* traverse through the List of particles in this sequence to add
             * the elements to the container
             */
            if (!((ParticleContainer) particle).getParticles().isEmpty()) {
                for (Particle currentParticle : ((ParticleContainer) particle).getParticles()) {
                    container.addParticle(convertParticle(namespace, currentParticle, sourceState));
                }
            }

            return container;
        } else if (particle instanceof CountingPattern) {
        	return new CountingPattern(convertParticle(namespace, ((CountingPattern) particle).getParticle(), sourceState),
        			((CountingPattern) particle).getMin(),
        			((CountingPattern) particle).getMax());
        } else {
            throw new RuntimeException("Particle type not supported.");
        }
    }

    /**
     * Convert an attribute pattern.
     *
     * Converts an attribute pattern to the equivalent classes in XSD.
     *
     * @return particle
     */
    public LinkedList<AttributeParticle> convertParticle(AttributePattern pattern) {
        LinkedList<AttributeParticle> list = new LinkedList<AttributeParticle>();

        // Append any attribute, if available
        if (pattern.getAnyAttribute() != null) {
            list.add(pattern.getAnyAttribute());
        }

        if (pattern.getAttributeList() != null) {
            for (AttributeParticle attribute : pattern.getAttributeList()) {
                list.add(this.convertAttribute(attribute));
            }
        }

        return list;
    }

    /**
     * Convert a single attribute.
     *
     * @return attributeParticle
     */
    protected AttributeParticle convertAttribute(AttributeParticle attribute) {
        if (attribute instanceof eu.fox7.schematoolkit.bonxai.om.Attribute) {
            return new eu.fox7.schematoolkit.xsd.om.Attribute(((eu.fox7.schematoolkit.bonxai.om.Attribute) attribute).getName(),
                    ((eu.fox7.schematoolkit.bonxai.om.Attribute) attribute).getType().getTypename());
        } else if (attribute instanceof AttributeGroupReference) {
            return new AttributeGroupReference( ((AttributeGroupReference) attribute).getName() );
        } else {
        	throw new RuntimeException("Attribute class not supported: " + attribute.getClass());
        }
    }
}

