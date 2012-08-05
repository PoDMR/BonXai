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

import eu.fox7.schematoolkit.common.AttributeParticle;
import eu.fox7.schematoolkit.common.QualifiedName;

public class SimpleContentRestriction extends SimpleContentInheritance implements SimpleTypeInheritance {

    protected SimpleContentFixableRestrictionProperty<String> minExclusive;
    protected SimpleContentFixableRestrictionProperty<String> maxExclusive;
    protected SimpleContentFixableRestrictionProperty<String> minInclusive;
    protected SimpleContentFixableRestrictionProperty<String> maxInclusive;
    protected SimpleContentFixableRestrictionProperty<Integer> totalDigits;
    protected SimpleContentFixableRestrictionProperty<Integer> fractionDigits;
    protected SimpleContentFixableRestrictionProperty<Integer> length;
    protected SimpleContentFixableRestrictionProperty<Integer> minLength;
    protected SimpleContentFixableRestrictionProperty<Integer> maxLength;
    protected SimpleContentRestrictionProperty<String> pattern;
    protected SimpleContentFixableRestrictionProperty<SimpleContentPropertyWhitespace> whitespace;
    protected LinkedList<String> enumeration;
    private LinkedList<AttributeParticle> attributes = new LinkedList<AttributeParticle>();

    public SimpleContentRestriction(QualifiedName baseType) {
        super(baseType);
        enumeration = new LinkedList<String>();
        this.attributes = new LinkedList<AttributeParticle>();
    }

    public SimpleContentRestriction() {
        super();
        enumeration = new LinkedList<String>();
        this.attributes = new LinkedList<AttributeParticle>();
    }

    public LinkedList<String> getEnumeration() {
        return this.enumeration;
    }

    public void addEnumeration(LinkedList<String> val) {
        enumeration.addAll(val);
    }

    public SimpleContentFixableRestrictionProperty<Integer> getFractionDigits() {
        return this.fractionDigits;
    }

    public void setFractionDigits(SimpleContentFixableRestrictionProperty<Integer> val) {
        this.fractionDigits = val;
    }

    public SimpleContentFixableRestrictionProperty<String> getMaxExclusive() {
        return this.maxExclusive;
    }

    public void setMaxExclusive(SimpleContentFixableRestrictionProperty<String> val) {
        this.maxExclusive = val;
    }

    public SimpleContentFixableRestrictionProperty<String> getMaxInclusive() {
        return this.maxInclusive;
    }

    public void setMaxInclusive(SimpleContentFixableRestrictionProperty<String> val) {
        this.maxInclusive = val;
    }

    public SimpleContentFixableRestrictionProperty<Integer> getMaxLength() {
        return this.maxLength;
    }

    public void setMaxLength(SimpleContentFixableRestrictionProperty<Integer> val) {
        this.maxLength = val;
    }

    public SimpleContentFixableRestrictionProperty<String> getMinExclusive() {
        return this.minExclusive;
    }

    public void setMinExclusive(SimpleContentFixableRestrictionProperty<String> val) {
        this.minExclusive = val;
    }

    public SimpleContentFixableRestrictionProperty<String> getMinInclusive() {
        return this.minInclusive;
    }

    public void setMinInclusive(SimpleContentFixableRestrictionProperty<String> val) {
        this.minInclusive = val;
    }

    public SimpleContentFixableRestrictionProperty<Integer> getLength() {
        return this.length;
    }

    public void setLength(SimpleContentFixableRestrictionProperty<Integer> length) {
        this.length = length;
    }

    public SimpleContentFixableRestrictionProperty<Integer> getMinLength() {
        return this.minLength;
    }

    public void setMinLength(SimpleContentFixableRestrictionProperty<Integer> val) {
        this.minLength = val;
    }

    public SimpleContentRestrictionProperty<String> getPattern() {
        return this.pattern;
    }

    public void setPattern(SimpleContentRestrictionProperty<String> val) {
        this.pattern = val;
    }

    public SimpleContentFixableRestrictionProperty<Integer> getTotalDigits() {
        return this.totalDigits;
    }

    public void setTotalDigits(SimpleContentFixableRestrictionProperty<Integer> val) {
        this.totalDigits = val;
    }

    public SimpleContentFixableRestrictionProperty<SimpleContentPropertyWhitespace> getWhitespace() {
        return this.whitespace;
    }

    public void setWhitespace(SimpleContentFixableRestrictionProperty<SimpleContentPropertyWhitespace> val) {
        this.whitespace = val;
    }

    /**
     * Returns a copy of the List holding the attributes of this SimpleContentRestriction
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

    public void setEnumeration(LinkedList<String> enumeration) {
        this.enumeration = enumeration;
    }

	public void addEnumeration(String value) {
		enumeration.add(value);
	}


}
