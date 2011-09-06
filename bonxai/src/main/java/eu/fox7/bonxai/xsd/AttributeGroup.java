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

import java.util.LinkedList;

/**
 * The AttributeGroup has a unique name and stores attribute particles in a
 * LinkedList.
 */
public class AttributeGroup extends AttributeParticle {

    protected boolean dummy;

    public boolean isDummy() {
        return dummy;
    }

    public void setDummy(boolean dummy) {
        this.dummy = dummy;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    


    /**
     * The name of the attribute group.
     *
     * This name is full qualified in the format "{<namespace>}<localName>",
     * where "<" and ">" are only meant as placeholder indicators and do not
     * belong to the qualified name itself.
     */
    protected String name;
    /**
     * List which stores the attribute particles.
     */
    protected LinkedList<AttributeParticle> attributeParticles;

    /**
     * Creates a new attribute group with an assigned name and an empty list of
     * attribute particles.
     *
     * This constructor should ONLY be used with fully qualified names in the
     * form "{namespace}name".
     */
    public AttributeGroup(String name) {
        attributeParticles = new LinkedList<AttributeParticle>();
        if ((name.length() < 2)
                || (!name.startsWith("{"))
                || (!name.contains("}"))) {
            throw new RuntimeException("Only fully qualified names are allowed.");
        }
        this.name = name;
    }

    /**
     * Returns a clone of the list of attribute particles.
     */
    public LinkedList<AttributeParticle> getAttributeParticles() {
        return new LinkedList<AttributeParticle>(attributeParticles);
    }

    /**
     * Get namespace.
     *
     * Get namespace URI from stored fully qualified name.
     *
     * @return string
     */
    public String getNamespace() {
        return this.name.substring(1, this.name.lastIndexOf("}"));
    }

    /**
     * Get local name.
     *
     * Get local name from stored fully qualified name.
     *
     * @return string
     */
    public String getLocalName() {
        return this.name.substring(this.name.lastIndexOf("}") + 1);
    }

    /**
     * Returns the name of the attribute group.
     *
     * This name is full qualified in the format "{<namespace>}<localName>",
     * where "<" and ">" are only meant as placeholder indicators and do not
     * belong to the qualified name itself.
     */
    public String getName() {
        return name;
    }

    /**
     * Adds an attribute particle to the group.
     */
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

