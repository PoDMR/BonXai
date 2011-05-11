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
package de.tudortmund.cs.bonxai.xsd;

import de.tudortmund.cs.bonxai.common.SymbolTableRef;

/**
 * The AttributeGroupRef class stores a common.SymbolTableRef object to represent a
 * reference to an attribute group.
 */
public class AttributeGroupRef extends AttributeParticle {

    /**
     * Reference to the attribute group in the symbol table.
     */
    protected de.tudortmund.cs.bonxai.common.SymbolTableRef<AttributeGroup> attributeGroupRef;

    /**
     * Creates an AttributeGroupRef object with the passed symbol table
     * reference.
     */
    public AttributeGroupRef (de.tudortmund.cs.bonxai.common.SymbolTableRef<AttributeGroup> attributeGroupRef) {
        this.attributeGroupRef = attributeGroupRef;
    }

    /**
     * Returns the dereferenced attribute group.
     */
    public AttributeGroup getAttributeGroup () {
        return attributeGroupRef.getReference();
    }

    /**
     * Setter for the attributeGroupRef
     * @param attributeGroupRef
     */
    public void setAttributeGroupRef(SymbolTableRef<AttributeGroup> attributeGroupRef) {
        this.attributeGroupRef = attributeGroupRef;
    }


}

