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
package de.tudortmund.cs.bonxai.bonxai;

import de.tudortmund.cs.bonxai.common.SymbolTableRef;

/**
 * AttributeGroupReference-Element of an AttributeList
 */
public class AttributeGroupReference extends AttributeListElement {

    /**
     * SymbolTableRef to the group referenced by this attribute group reference.
     */
    private SymbolTableRef<AttributeGroupElement> groupReference;

    /**
     * Empty constructor for the class AttributeListElement
     */
    public AttributeGroupReference(SymbolTableRef<AttributeGroupElement> groupReference) {
        super(null);
        this.groupReference = groupReference;
    }

    /**
     * Get attribute group reference name.
     *
     * @return name
     */
    @Override
    public String getName() {
        if (groupReference.getReference() == null) {
            // @TODO: Should this be an exception?
            return null;
        }
        return groupReference.getReference().getName();
    }

    /**
     * Returns the referenced group element.
     */
    public AttributeGroupElement getGroupElement() {
        return groupReference.getReference();
    }
}
