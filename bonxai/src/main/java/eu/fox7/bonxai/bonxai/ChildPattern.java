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
package eu.fox7.bonxai.bonxai;

/**
 * Class representing the child-string in Bonxai
 */
public class ChildPattern {

    /**
     * Representing the pattern-part of the child-string consisting of Attributes
     */
    private AttributePattern attributePattern;
    /**
     * Representing the pattern-part of the child-string consisting of Elements
     */
    private ElementPattern elementPattern;

    /**
     * Constructor for the class ChildPattern
     * @param attributePattern
     * @param elementPattern
     */
    public ChildPattern(AttributePattern attributePattern, ElementPattern elementPattern) {
        this.attributePattern = attributePattern;
        this.elementPattern = elementPattern;
    }

    /**
     * Returns the AttributePattern of this ChildPattern
     * @return attributePattern
     */
    public AttributePattern getAttributePattern() {
        return attributePattern;
    }

    /**
     * Returns the ElementPattern of this ChildPattern
     * @return elementPattern
     */
    public ElementPattern getElementPattern() {
        return elementPattern;
    }
}

