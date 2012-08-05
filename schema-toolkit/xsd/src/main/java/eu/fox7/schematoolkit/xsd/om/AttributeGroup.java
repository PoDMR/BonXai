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
package eu.fox7.schematoolkit.xsd.om;

import java.util.LinkedList;

import eu.fox7.schematoolkit.common.AnnotationElement;
import eu.fox7.schematoolkit.common.AttributeParticle;
import eu.fox7.schematoolkit.common.QualifiedName;
import eu.fox7.schematoolkit.xsd.saxparser.NamedXSDElement;

/**
 * The AttributeGroup has a unique name and stores attribute particles in a
 * LinkedList.
 */
public class AttributeGroup extends AnnotationElement implements AContainer, NamedXSDElement {

    protected boolean dummy;

    public boolean isDummy() {
        return dummy;
    }

    public void setDummy(boolean dummy) {
        this.dummy = dummy;
    }

    public void setName(QualifiedName name) {
        this.name = name;
    }
    
    


    /**
     * The name of the attribute group.
     *
     * This name is full qualified in the format "{<namespace>}<localName>",
     * where "<" and ">" are only meant as placeholder indicators and do not
     * belong to the qualified name itself.
     */
    protected QualifiedName name;
    /**
     * List which stores the attribute particles.
     */
    protected LinkedList<AttributeParticle> attributeParticles;

    public AttributeGroup(){
        attributeParticles = new LinkedList<AttributeParticle>();
    }
    
    /**
     * Creates a new attribute group with an assigned name and an empty list of
     * attribute particles.
     *
     * This constructor should ONLY be used with fully qualified names in the
     * form "{namespace}name".
     */
    public AttributeGroup(QualifiedName attributeGroupName) {
        attributeParticles = new LinkedList<AttributeParticle>();
        this.name = attributeGroupName;
    }

    /**
     * Returns a clone of the list of attribute particles.
     */
    public LinkedList<AttributeParticle> getAttributeParticles() {
        return new LinkedList<AttributeParticle>(attributeParticles);
    }


    /**
     * Returns the name of the attribute group.
     *
     * This name is full qualified in the format "{<namespace>}<localName>",
     * where "<" and ">" are only meant as placeholder indicators and do not
     * belong to the qualified name itself.
     */
    public QualifiedName getName() {
        return name;
    }

    /**
     * Adds an attribute particle to the group.
     */
    @Override
    public void addAttributeParticle(AttributeParticle attributeParticle) {
        attributeParticles.add(attributeParticle);
    }

    /**
     * Compare the object with that object.
     *
     * This is a specialized implementation of equals(), which only checks the
     * name of the attribute group. This is sensible since attribute groups in
     * XSD must have a unique name there must not exist 2 attribute groups with
     * the same name.
     */
    public boolean equals(Object that) {
        return ((that instanceof AttributeGroup)
                && this.name.equals(((AttributeGroup) that).name));
    }



}

