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
package de.tudortmund.cs.bonxai.converter.bonxai2xsd;

import gjb.flt.automata.NotDFAException;
import gjb.flt.automata.impl.sparse.AnnotatedStateDFA;
import gjb.flt.automata.impl.sparse.State;
import gjb.flt.automata.impl.sparse.Symbol;

import java.util.LinkedList;

import de.tudortmund.cs.bonxai.common.*;
import de.tudortmund.cs.bonxai.bonxai.*;
import de.tudortmund.cs.bonxai.typeautomaton.ElementProperties;
import de.tudortmund.cs.bonxai.typeautomaton.TypeAutomaton;
import de.tudortmund.cs.bonxai.xsd.*;

/**
 * Bonxai2XSDConverter for group declarations inside an Bonxai schema.
 */
public class ParticleConverter {

	private SymbolTableFoundation<AttributeGroup> attributeGroupSymbolTable;
	private SymbolTableFoundation<de.tudortmund.cs.bonxai.xsd.Element> elementSymbolTable;
	private SymbolTableFoundation<Type> typeSymbolTable;
	private SymbolTableFoundation<de.tudortmund.cs.bonxai.xsd.Group> groupSymbolTable;
	private TypeAutomaton typeAutomaton;
	
	public ParticleConverter(TypeAutomaton typeAutomaton, 
			SymbolTableFoundation<AttributeGroup> attributeGroupSymbolTable, 
			SymbolTableFoundation<de.tudortmund.cs.bonxai.xsd.Element> elementSymbolTable, 
			SymbolTableFoundation<de.tudortmund.cs.bonxai.xsd.Group> groupSymbolTable, 
			SymbolTableFoundation<Type> typeSymbolTable) {
		this.attributeGroupSymbolTable=attributeGroupSymbolTable;
		this.elementSymbolTable=elementSymbolTable;
		this.groupSymbolTable=groupSymbolTable;
		this.typeSymbolTable=typeSymbolTable;
		this.typeAutomaton = typeAutomaton;
	}
	
    /**
     * Convert a particle.
     *
     * Converts a single particle, or element pattern, to XSD.
     *
     * @return particle
     */
    public Particle convertParticle(String namespace, Particle particle, State sourceState) {
        if ((particle instanceof de.tudortmund.cs.bonxai.bonxai.Element)
                && (((de.tudortmund.cs.bonxai.bonxai.Element) particle).getNamespace().equals(namespace))) {
            de.tudortmund.cs.bonxai.bonxai.Element source = (de.tudortmund.cs.bonxai.bonxai.Element) particle;

            de.tudortmund.cs.bonxai.xsd.Element element = new de.tudortmund.cs.bonxai.xsd.Element(
                    "{" + source.getNamespace() + "}" + source.getName());

            if (source.getType() != null) {
                if (source.getDefault() != null) {
                    element.setDefault(source.getDefault());
                }

                if (source.getFixed() != null) {
                    element.setFixed(source.getFixed());
                }

                if (source.isMissing()) {
                    element.setNillable();
                }

                element.setType(convertType(source.getType()));
                element.setTypeAttr(true);
            } else {
            	String qualifiedName = "{" + source.getNamespace() + "}" + source.getName();
            	State toState;
				try {
					toState = typeAutomaton.getNextState(Symbol.create(qualifiedName), sourceState);
					element.setType(typeAutomaton.getType(toState));
					element.setTypeAttr(true);
					ElementProperties elementProperties = typeAutomaton.getElementProperties(toState);
					element.setProperties(elementProperties);
				} catch (NotDFAException e) {
					throw new RuntimeException(e);
				}
            }

            return element;
        } else if (particle instanceof de.tudortmund.cs.bonxai.common.AnyPattern) {
            // Replace all occurrences of "," in the namespace attribute with " " spaces.
            AnyPattern anyPattern = (de.tudortmund.cs.bonxai.common.AnyPattern) particle;
            String namespaceList = anyPattern.getNamespace().replaceAll(" ", "");
            namespaceList = namespaceList.replaceAll(",", " ");
            particle = new AnyPattern(anyPattern.getProcessContentsInstruction(), namespaceList);
            return particle;
        } else if (particle instanceof de.tudortmund.cs.bonxai.bonxai.Element) {
            // For elements in foreign namespaces we just create an element
            // reference
            return new ElementRef(elementSymbolTable.getReference(
                    "{" + ((de.tudortmund.cs.bonxai.bonxai.Element) particle).getNamespace() + "}"
                    + ((de.tudortmund.cs.bonxai.bonxai.Element) particle).getName()));
        } else if (particle instanceof GroupRef) {
                return new GroupRef(
                        groupSymbolTable.getReference(
                        "{" + namespace + "}" + ((GroupRef) particle).getGroup().getName()));
        } else if (particle instanceof ParticleContainer) {
            ParticleContainer container = null;
            if (particle instanceof SequencePattern) {
                container = new SequencePattern();
            } else if (particle instanceof ChoicePattern) {
                container = new ChoicePattern();
            } else if (particle instanceof AllPattern) {
                container = new AllPattern();
            } else if (particle instanceof CountingPattern) {
                container = new CountingPattern(
                        ((CountingPattern) particle).getMin(),
                        ((CountingPattern) particle).getMax());
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
    public LinkedList<AttributeParticle> convertParticle(String namespace, AttributePattern pattern) {
        LinkedList<AttributeParticle> list = new LinkedList<AttributeParticle>();

        // Append any attribute, if available
        if (pattern.getAnyAttribute() != null) {
            list.add(pattern.getAnyAttribute());
        }

        if (pattern.getAttributeList() != null) {
            for (AttributeListElement attribute : pattern.getAttributeList()) {
                list.add(this.convertAttribute(namespace, attribute));
            }
        }

        return list;
    }

    /**
     * Convert a type into a XSD type reference.
     *
     * @return type
     */
    protected SymbolTableRef<Type> convertType(BonxaiType type) {
    	SimpleType simpleType = new SimpleType(type.getFullQualifiedName(),null);
    	simpleType.setIsAnonymous(true);
        typeSymbolTable.updateOrCreateReference(type.getFullQualifiedName(), simpleType);
        return typeSymbolTable.getReference(type.getFullQualifiedName());
    }

    /**
     * Convert a single attribute.
     *
     * @return attributeParticle
     */
    protected AttributeParticle convertAttribute(String namespace, AttributeListElement attribute) {
        if (attribute instanceof de.tudortmund.cs.bonxai.bonxai.Attribute) {
            return new de.tudortmund.cs.bonxai.xsd.Attribute(
                    "{" + ((de.tudortmund.cs.bonxai.bonxai.Attribute) attribute).getNamespace() + "}"
                    + ((de.tudortmund.cs.bonxai.bonxai.Attribute) attribute).getName(),
                    this.convertType(((de.tudortmund.cs.bonxai.bonxai.Attribute) attribute).getType()));
        } else if (attribute instanceof AttributeGroupReference) {
            return new AttributeGroupRef(
                    attributeGroupSymbolTable.getReference(
                    "{" + namespace + "}" + ((AttributeGroupReference) attribute).getName()));
        } else {
        	throw new RuntimeException("Attribute class not supported: " + attribute.getClass());
        }
    }
}

