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
import java.util.HashSet;

/**
 * Class for the representation of the XSD Element ComplexType.
 *
 * Not the overridden implementations of hashCode() and equals() in the {@link
 * Type} base class!
 */
public class ComplexType extends Type {

    /**
     * Boolean variable for determining if the ComplexType is abstract
     */
    private boolean isAbstract = false;
    /**
     * Set of final inheritance Modifiers (restriction, extension)
     */
    private HashSet<ComplexTypeInheritanceModifier> finalModifiers;
    /**
     * Set of block inheritance Modifiers (restriction, extension)
     */
    private HashSet<ComplexTypeInheritanceModifier> blockModifiers;
    /**
     * LinkedList holding the AttributeParticle of the ComplexType
     */
    private LinkedList<AttributeParticle> attributes = new LinkedList<AttributeParticle>();
    /**
     * Content of the ComplexType
     */
    private Content content;
    /**
     * Variable to determine if the ComplexType has a mixed content or not.
     * If there is a ComplexContent defined in this ComplexType, than the value
     * of the "mixed" property of the ComplexContent semantically overwrites
     * this local property.
     */
    private boolean mixed = false;

    /**
     * Contstructor of ComplexType
     * @param name
     * @param content
     */
    public ComplexType(String name, Content content) {
        super(name);
        this.content = content;
    }

    /**
     * Contstructor of ComplexType
     * @param name
     * @param content
     * @param isAnonymous 
     */
    public ComplexType(String name, Content content, boolean isAnonymous) {
        super(name);
        this.content = content;
        this.isAnonymous = isAnonymous;
    }

    /**
     * Returns a copy of the List holding the attributes of this ComplexType
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
     * Returns a copy of the HashSet holding the Block-Modifiers of this ComplexType
     * @return returnCopy
     */
    public HashSet<ComplexTypeInheritanceModifier> getBlockModifiers() {

        if (blockModifiers == null){
            return null;
        }
        return new HashSet<ComplexTypeInheritanceModifier>(blockModifiers);
    }

    /**
     * Adds an Block-Modifier to the HashSet of Block-Modifiers
     * @param complexTypeInheritanceModifier
     */
    public void addBlockModifier(ComplexTypeInheritanceModifier complexTypeInheritanceModifier) {
        if (blockModifiers == null){
            blockModifiers = new HashSet<ComplexTypeInheritanceModifier>();
        }
        this.blockModifiers.add(complexTypeInheritanceModifier);
    }

    /**
     * Returns a the content
     * @return returnCopy
     */
    public Content getContent() {
        return this.content;
    }

    /**
     * Returns a copy of the HashSet holding the Final-Modifiers of this ComplexType
     * @return returnCopy
     */
    public HashSet<ComplexTypeInheritanceModifier> getFinalModifiers() {
        if (finalModifiers == null){
            return null;
        }
        return new HashSet<ComplexTypeInheritanceModifier>(finalModifiers);
    }

    /**
     * Adds an Final-Modifier to the HashSet of Final-Modifiers
     * @param complexTypeInheritanceModifier
     */
    public void addFinalModifier(ComplexTypeInheritanceModifier complexTypeInheritanceModifier) {
        if (finalModifiers == null){
            finalModifiers = new HashSet<ComplexTypeInheritanceModifier>();
        }
        this.finalModifiers.add(complexTypeInheritanceModifier);
    }

    /**
     * Sets the FinalModifiers HashSet
     * @param complexTypeInheritanceModifierSet
     */
    public void setFinalModifiers(HashSet<ComplexTypeInheritanceModifier> complexTypeInheritanceModifierSet) {
        this.finalModifiers = complexTypeInheritanceModifierSet;
    }

    /**
     * Sets the BlockModifiers HashSet
     * @param complexTypeInheritanceModifierSet
     */
    public void setBlockModifiers(HashSet<ComplexTypeInheritanceModifier> complexTypeInheritanceModifierSet) {
        this.blockModifiers = complexTypeInheritanceModifierSet;
    }

    /**
     * Sets the AttributeParticles LinkedList
     * @param attributeParticles
     */
    public void setAttributes(LinkedList<AttributeParticle> attributeParticles) {
        this.attributes = attributeParticles;
    }

    /**
     * Sets the Content
     * @param content
     */
    public void setContent(Content content) {
        this.content = content;
    }

    /**
     * Returns the value of isAbstract
     * @return isAbstract
     */
    public Boolean isAbstract() {
        return this.isAbstract;
    }

    /**
     * Sets the value of isAbstract
     * @param isAbstract
     */
    public void setAbstract(boolean isAbstract) {
        this.isAbstract = isAbstract;
    }

    /**
     * Returns the value of mixed
     * @return mixed
     */
    public Boolean getMixed() {
        return this.mixed;
    }

    /**
     * Sets the value of the variable mixed
     * @param mixedContent
     */
    public void setMixed(boolean mixedContent) {
        this.mixed = mixedContent;
    }
   
}
