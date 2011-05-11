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
package de.tudortmund.cs.bonxai.converter.xsd2bonxai.old;

import de.tudortmund.cs.bonxai.bonxai.Bonxai;
import de.tudortmund.cs.bonxai.bonxai.GroupList;
import de.tudortmund.cs.bonxai.bonxai.ElementGroupElement;
import de.tudortmund.cs.bonxai.bonxai.AttributeGroupElement;
import de.tudortmund.cs.bonxai.converter.xsd2bonxai.AttributeProcessor;
import de.tudortmund.cs.bonxai.converter.xsd2bonxai.ParticleProcessor;

import de.tudortmund.cs.bonxai.xsd.XSDSchema;

/**
 * Bonxai2XSDConverter for groups and attribute groups.
 *
 * This converter converts {@link de.tudortmund.cs.bonxai.common.Group}s and {@link
 * de.tudortmund.cs.bonxai.xsd.AttributeGroup}s elements from a {@link xsd.chema} to their
 * corresponding {@link de.tudortmund.cs.bonxai.common.Group}s and ((missing!)) in the given {@link
 * de.tudortmund.cs.bonxai.bonxai.Bonxai}.
 *
 */
class GroupsConverter implements Converter {

    private ParticleProcessor particleProcessor;

    private AttributeProcessor attributeProcessor;

    /**
     * Creates a new {@link de.tudortmund.cs.bonxai.bonxai.Group} and {@link de.tudortmund.cs.bonxai.xsd.AttributeGroup}
     * converter.
     *
     */
    public GroupsConverter() {

    }

    /**
     * Converts {@link de.tudortmund.cs.bonxai.bonxai.Group}s and {@link de.tudortmund.cs.bonxai.xsd.AttributeGroup}s from the
     * given schema to {@link Bonxai.Group}s and ((missing!)) in the given bonxai.
     *
     * This method receives the schema object to convert to the given bonxai
     * object. It is responsible to read all {@link de.tudortmund.cs.bonxai.bonxai.Group}s and {@link
     * de.tudortmund.cs.bonxai.xsd.AttributeGroup}s from schema and to create corresponding {@link
     * de.tudortmund.cs.bonxai.bonxai.Group}s and ((missing!)) in bonxai.
     *
     * For convenience reasons, the method resturns the given bonxai parameter
     * again, although it manipulates it directly.
     *
     * @see Bonxai2XSDConverter
     */
    public Bonxai convert( XSDSchema schema, TypeAutomaton automaton, Bonxai bonxai ) {
        bonxai.setGroupList(new GroupList());

        particleProcessor = new ParticleProcessor(bonxai.getGroupSymbolTable());
        attributeProcessor = new AttributeProcessor(bonxai.getAttributeGroupElementSymbolTable());

        for (de.tudortmund.cs.bonxai.xsd.Group xsdElemGroup : schema.getGroups()) {
            if (xsdElemGroup == null) {
                throw new RuntimeException("Cannot resolve element group reference. Group does not exist.");
            }
            de.tudortmund.cs.bonxai.bonxai.ElementGroupElement bonxaiElemGroup
                = convertElementGroup(xsdElemGroup);

            // Sanity check
            if (bonxai.getGroupSymbolTable().getReference(bonxaiElemGroup.getName()).getReference() != null) {
                throw new RuntimeException("Duplicate element group '" + bonxaiElemGroup.getName() + "'.");
            }

            bonxai.getGroupSymbolTable().updateOrCreateReference(
                bonxaiElemGroup.getName(),
                bonxaiElemGroup
            );

            bonxai.getGroupList().addGroupElement(bonxaiElemGroup);
        }

        for (de.tudortmund.cs.bonxai.xsd.AttributeGroup xsdAttrGroup : schema.getAttributeGroups()) {
            if (xsdAttrGroup == null) {
                throw new RuntimeException("Cannot resolve attribute group reference. Group does not exist.");
            }
            de.tudortmund.cs.bonxai.bonxai.AttributeGroupElement bonxaiAttrGroup
                = convertAttributeGroup(xsdAttrGroup);

//            if (bonxai.getAttributeGroupElementSymbolTable().getReference(bonxaiAttrGroup.getName()).getReference() != null) {
//                throw new RuntimeException("Duplicate attribute group '" + bonxaiAttrGroup.getName() + "'.");
//            }
            bonxai.getAttributeGroupElementSymbolTable().updateOrCreateReference(
                bonxaiAttrGroup.getName(),
                bonxaiAttrGroup
            );

            bonxai.getGroupList().addGroupElement(bonxaiAttrGroup);
        }

        return bonxai;
    }

    protected ElementGroupElement convertElementGroup(de.tudortmund.cs.bonxai.xsd.Group elementGroup ) {
        return new ElementGroupElement(
            elementGroup.getLocalName(),
            particleProcessor.convertParticleContainer(elementGroup.getParticleContainer())
        );
    }

    protected AttributeGroupElement convertAttributeGroup(de.tudortmund.cs.bonxai.xsd.AttributeGroup attributeGroup) {
        return new AttributeGroupElement(
            attributeGroup.getLocalName(),
            attributeProcessor.convertAttributes(attributeGroup.getAttributeParticles())
        );
    }
}
