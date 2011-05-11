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

import java.util.Vector;
import de.tudortmund.cs.bonxai.common.AnyAttribute;

/**
 * Class for representing the attribute pattern in Bonxai
 */
public class AttributePattern {

    /**
     * Contains the any attribut of this AttributePattern if exists
     */
    private AnyAttribute anyAttribute = null;

    /**
     * List of AttributeListElements
     */
    private Vector<AttributeListElement> attributeList = new Vector<AttributeListElement>();

    /**
     * Default (empty) construction, use setters to fill!
     */
    public AttributePattern() {

    }

    /**
     * Main constructor, pre-filling with attributeList.
     */
    public AttributePattern(Vector<AttributeListElement> attributeList) {
        this.attributeList = attributeList;
    }

    /**
     * Full constructor.
     */
    public AttributePattern(Vector<AttributeListElement> attributeList, AnyAttribute anyAttribute) {
        this(attributeList);
        this.anyAttribute = anyAttribute;
    }

    /**
     * Constructor with anyAttribute only, use setter to add attributeList.
     */
    public AttributePattern(AnyAttribute anyAttribute) {
        this.anyAttribute = anyAttribute;
    }

    /**
     * Returns the attribute anyAttribute of this AttributeGroupElement.
     *
     * @return anyAttribute
     */
    public AnyAttribute getAnyAttribute() {
        return anyAttribute;
    }

    /**
     * Sets the attribute anyAttribute for this AttributePattern.
     *
     * @param anyAttribute
     */
    public void setAnyAttribute(AnyAttribute anyAttribute) {
        this.anyAttribute = anyAttribute;
    }

    /**
     * Returns the List of AttributeListElements of this AttributePattern.
     *
     * @return attributeList
     */
    public Vector<AttributeListElement> getAttributeList() {
        return this.attributeList;
    }

    /**
     * Sets the List of AttributeListElements of this AttributePattern.
     *
     * @param attributeList
     */
    public void setAttributeList(Vector<AttributeListElement> attributeList) {
        this.attributeList = attributeList;
    }

    /**
     * Append a single attribute to the attribute list
     *
     * @param attribute
     */
    public void addAttribute(AttributeListElement attribute) {
        this.attributeList.add(attribute);
    }
}

