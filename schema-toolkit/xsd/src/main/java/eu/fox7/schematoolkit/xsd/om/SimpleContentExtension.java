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

package eu.fox7.schematoolkit.xsd.om;

import java.util.LinkedList;

import eu.fox7.schematoolkit.common.AttributeParticle;
import eu.fox7.schematoolkit.common.QualifiedName;

/**
 * The {@code SimpleContentExtension} class only defines a
 * base class which is inherited. No further information is
 * needed for this type of inheritance.
 */
public class SimpleContentExtension extends SimpleContentInheritance {

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
    public SimpleContentExtension (QualifiedName baseType) {
        super(baseType);
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


    /**
     * Sets the AttributeParticles LinkedList
     * @param attributeParticles
     */
    public void setAttributes(LinkedList<AttributeParticle> attributeParticles) {
        this.attributes = attributeParticles;
    }
}

