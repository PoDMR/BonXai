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
import eu.fox7.schematoolkit.common.PContainer;
import eu.fox7.schematoolkit.common.Particle;
import eu.fox7.schematoolkit.common.QualifiedName;

/**
 * ComplexContentInheritance may be of the types
 * Restriction or Extension.
 */

public abstract class ComplexContentInheritance extends Inheritance implements AContainer {

    /**
     * LinkedList holding the AttributeParticle of the ComplexContentInheritance
     *
     * @TODO This attribute is not part of the XSD data structure. Do not use
     * it, since it will be removed again! Use {@link ComplexType.attributes}.
     */
    private LinkedList<AttributeParticle> attributes = new LinkedList<AttributeParticle>();

    public ComplexContentInheritance () {}

    /**
     * Creates an instance of Complex Content Inheritance
     * with the passed symboltable reference base.
     * @param base common.SymbolTableRef<Type>
     */
    public ComplexContentInheritance (QualifiedName baseType) {
        super(baseType);
    }

    /**
     * Returns a copy of the List holding the attributes of this ComplexContentInheritance
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
        this.addAttributeParticle(attributeParticle);
    }
    
    public void addAttributeParticle(AttributeParticle attributeParticle) {
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

