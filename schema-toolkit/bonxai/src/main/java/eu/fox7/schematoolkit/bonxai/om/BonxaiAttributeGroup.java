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
package eu.fox7.schematoolkit.bonxai.om;

import eu.fox7.schematoolkit.common.QualifiedName;

/**
 * Attribute-Element of a Group
 */
public class BonxaiAttributeGroup extends BonxaiAbstractGroup {

    /**
     * The attribute attributePattern of this AttributeGroupElement contains the attribute pattern
     */
    private AttributePattern attributePattern;

    /**
     * Constructor for the class AttributeGroupElement
     * @param name
     * @param attributePattern
     */
    public BonxaiAttributeGroup(QualifiedName name, AttributePattern attributePattern) {
        super(name);
        this.attributePattern = attributePattern;
    }

    /**
     * Returns the attribute attributePattern of this AttributeGroupElement
     * @return attributePattern
     */
    public AttributePattern getAttributePattern() {
        return attributePattern;
    }
}

