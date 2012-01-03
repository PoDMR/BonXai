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

import java.util.HashSet;

import eu.fox7.schematoolkit.common.QualifiedName;

/**
 * Class for the representation of the XSD Element SimpleType.
 *
 * Note the overridden implementations of hashCode() and equals() in the {@link
 * Type} base class!
 */
public class SimpleType extends Type {

    /**
     * Set of final inheritance Modifiers (list, union, restriction)
     */
    private HashSet<SimpleTypeInheritanceModifier> finalModifiers;
    /**
     * Variable for the inheritance description of the SimpleType
     */
    private SimpleTypeInheritance inheritance = null;

    /**
     * Contstructor of SimpleType
     * @param name
     * @param inheritance
     */
    public SimpleType(QualifiedName name, SimpleTypeInheritance inheritance) {
        super(name);
        this.inheritance = inheritance;
    }

    /**
     * Contstructor of SimpleType
     * @param name
     * @param inheritance
     * @param isAnonymous
     */
    public SimpleType(QualifiedName name, SimpleTypeInheritance inheritance, boolean isAnonymous) {
        super(name);
        this.inheritance = inheritance;
        this.isAnonymous = isAnonymous;
    }

    /**
     * Returns a copy of the final Modifiers of the SimpleType
     * @return returnCopy
     */
    public HashSet<SimpleTypeInheritanceModifier> getFinalModifiers() {
        if (finalModifiers == null){
            return null;
        }
        return new HashSet<SimpleTypeInheritanceModifier>(finalModifiers);
    }

    /**
     * Adds an Final-Modifier to the HashSet of Final-Modifiers
     * @param simpleTypeInheritanceModifier
     */
    public void addFinalModifier(SimpleTypeInheritanceModifier simpleTypeInheritanceModifier) {
        if (finalModifiers == null){
            finalModifiers = new HashSet<SimpleTypeInheritanceModifier>();
        }
        this.finalModifiers.add(simpleTypeInheritanceModifier);
    }

    /**
     * Sets the FinalModifiers Set
     * @param simpleTypeInheritanceModifierSet
     */
    public void setFinalModifiers(HashSet<SimpleTypeInheritanceModifier> simpleTypeInheritanceModifierSet) {
        this.finalModifiers = simpleTypeInheritanceModifierSet;
    }

    /**
     * Gets the Inheritance
     * @return returnCopy
     */
    public SimpleTypeInheritance getInheritance() {
        return this.inheritance;
    }

    /**
     * Sets the inheritance of SimpleType
     * @param inheritance
     */
    protected void setInheritance(SimpleTypeInheritance inheritance) {
        this.inheritance = inheritance;
    }

    public void setIsAnonymous(boolean isAnonymous) {
        this.isAnonymous = isAnonymous;
    }
}
