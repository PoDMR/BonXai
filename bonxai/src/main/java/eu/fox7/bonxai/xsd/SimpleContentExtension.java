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
package eu.fox7.bonxai.xsd;

import eu.fox7.bonxai.common.SymbolTableRef;

import java.util.LinkedList;

/**
 * The {@code SimpleContentExtension} class only defines a
 * base class which is inherited. No further information is
 * needed for this type of inheritance.
 */
public class SimpleContentExtension extends Inheritance implements SimpleContentInheritance {

    /**
     * LinkedList holding the AttributeParticle of the SimpleContentInheritance
     *
     */
    private LinkedList<AttributeParticle> attributes = new LinkedList<AttributeParticle>();

    /**
     * Creates an instance of Simple Content Extension
     * with the passed SymbolTable reference base.
     * @param base common.SymbolTableRef<Type>
     */
    public SimpleContentExtension (eu.fox7.bonxai.common.SymbolTableRef<Type> base) {
        super(base);
    }

    /**
     * Returns a copy of the List holding the attributes of this SimpleContentExtension
     * @return returnCopy
     */
    public LinkedList<AttributeParticle> getAttributes() {
        return new LinkedList<AttributeParticle>(this.attributes);
    }

    /**
     * Adds an attribute to the List of attributes
     * @param attributeParticle
     */
    public void addAttribute(AttributeParticle attributeParticle) {
        this.attributes.add(attributeParticle);
    }

    public void setBase(SymbolTableRef<Type> symbolTableRef) {
        this.base = symbolTableRef;
    }

    /**
     * Sets the AttributeParticles LinkedList
     * @param attributeParticles
     */
    public void setAttributes(LinkedList<AttributeParticle> attributeParticles) {
        this.attributes = attributeParticles;
    }
}

