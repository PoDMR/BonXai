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
package de.tudortmund.cs.bonxai.xsd;

import java.util.LinkedList;

import de.tudortmund.cs.bonxai.common.*;
import de.tudortmund.cs.bonxai.typeautomaton.ElementProperties;

import java.util.HashSet;

/**
 * XSD Element.
 */
public class Element extends de.tudortmund.cs.bonxai.common.Element {

    public boolean getNillable() {
        return nillable;
    }

    protected boolean dummy;

    public boolean isDummy() {
        return dummy;
    }

    public void setDummy(boolean dummy) {
        this.dummy = dummy;
    }
    
    /**
     * The type of the element.
     *
     * Must be a valid {@link SymbolTableRef} pointing to a {@link SimpleType}
     * or {@link ComplexType}.
     */
    protected SymbolTableRef<Type> typeRef;
    /**
     * List of {@link Constraint}s defined in this element.
     */
    protected LinkedList<Constraint> constraints = new LinkedList<Constraint>();
    /**
     * Boolean variable for determining if the Element is abstract
     */
    private boolean Abstract = false;
    /**
     * Nillable flag.
     */
    protected boolean nillable = false;

    /**
     * Public enumeration for the Values of block
     **/
    public enum Block {

        extension,
        restriction,
        substitution;
    }

    /**
     * Public enumeration for the Values of final
     **/
    public enum Final {

        extension,
        restriction;
    }
    /**
     * Set of final inheritance Modifiers (restriction, extension)
     */
    private HashSet<Final> finalModifiers;
    /**
     * Set of block inheritance Modifiers (restriction, extension, substitution)
     */
    private HashSet<Block> blockModifiers;
    /**
     * substitutionGroup
     */
    protected SymbolTableRef<Element> substitutionGroup;
    /**
     * Is set true if the type was set in the type-attribute.
     */
    protected boolean typeAttr = false;
    private XSDSchema.Qualification form = null;

    public boolean getTypeAttr() {
        return typeAttr;
    }

    public void setTypeAttr(boolean typeAttr) {
        this.typeAttr = typeAttr;
    }

    public SymbolTableRef<de.tudortmund.cs.bonxai.xsd.Element> getSubstitutionGroup() {
        return substitutionGroup;
    }

    public void setSubstitutionGroup(SymbolTableRef<de.tudortmund.cs.bonxai.xsd.Element> substitutionGroup) {
        this.substitutionGroup = substitutionGroup;
    }

    /**
     * Returns a copy of the HashSet holding the Block-Modifiers of this Element
     * @return returnCopy
     */
    public HashSet<Block> getBlockModifiers() {
        if (blockModifiers == null){
            return null;
        }
        return new HashSet<Block>(blockModifiers);
    }

    /**
     * Adds an Block-Modifier to the HashSet of Block-Modifiers
     * @param blockModifier
     */
    public void addBlockModifier(Block blockModifier) {
        if (blockModifiers == null){
            blockModifiers = new HashSet<Block>();
        }
        this.blockModifiers.add(blockModifier);
    }

    /**
     * Returns a copy of the HashSet holding the Final-Modifiers of this ComplexType
     * @return returnCopy
     */
    public HashSet<Final> getFinalModifiers() {
        if (finalModifiers == null){
            return null;
        }
        return new HashSet<Final>(finalModifiers);
    }

    /**
     * Adds an Final-Modifier to the HashSet of Final-Modifiers
     * @param finalModifier
     */
    public void addFinalModifier(Final finalModifier) {
        if (finalModifiers == null){
            finalModifiers = new HashSet<Final>();
        }
        this.finalModifiers.add(finalModifier);
    }

    /**
     * Sets the FinalModifiers HashSet
     * @param finalModifierSet
     */
    public void setFinalModifiers(HashSet<Final> finalModifierSet) {
        this.finalModifiers = finalModifierSet;
    }

    /**
     * Sets the BlockModifiers HashSet
     * @param blockModifierSet
     */
    public void setBlockModifiers(HashSet<Block> blockModifierSet) {
        this.blockModifiers = blockModifierSet;
    }

    /**
     * Getter for form
     * @return void
     */
    public XSDSchema.Qualification getForm() {
        return form;
    }

    /**
     * Setter for form
     * This describes if there has to be the namespace abbreviation infront of
     * elements.
     * @param form
     */
    public void setForm(XSDSchema.Qualification elementForm) {
        this.form = elementForm;
    }

    /**
     * Creates an element with only a name.
     *
     * The use of this ctor is highly discouraged, since a namespace is
     * mandatory anyway.
     *
     * This constructor should ONLY be used with fully qualified names in the
     * form "{namespace}name".
     */
    public Element(String name) {
        if ((name.length() < 2)
                || (!name.startsWith("{"))
                || (!name.contains("}"))) {
            throw new RuntimeException("Only fully qualified names are allowed.");
        }

        this.name = name;
    }

    /**
     * Creates an element with namespace and name.
     *
     * This should be the usual ctor to use, although it makes only sense in
     * the Bonxai realization.
     *
     * If this constructor with a explicit namespace is used, the name should
     * be a local name and NO fully qualified name, while the namespace should
     * be the full namespace URL.
     */
    public Element(String namespace, String name) {
        this("{" + namespace + "}" + name);
    }

    /**
     * Constructor with name and type.
     *
     * Usage of this ctor is discouraged, since it does not ship a namespace.
     *
     * This constructor should ONLY be used with fully qualified names in the
     * form "{namespace}name".
     */
    public Element(String name, SymbolTableRef<Type> typeRef) {
        this(name);
        setType(typeRef);
    }

    /**
     * Constructor providing namespace, name and type.
     *
     * The use of this ctor is highly encouraged over the constructors in the
     * base type.
     *
     * If this constructor with a explicit namespace is used, the name should
     * be a local name and NO fully qualified name, while the namespace should
     * be the full namespace URL.
     */
    public Element(String namespace, String name, SymbolTableRef<Type> typeRef) {
        this("{" + namespace + "}" + name);
        setType(typeRef);
    }

    /**
     * Method getType returns the type of an element, saved in the
     * typeConnectorRef.
     */
    public Type getType() {
        if (this.typeRef == null) {
            return null;
        }
        return this.typeRef.getReference();
    }
    
    /** Returns a reference to the type of the element.
     * 
     * @return
     */
    public SymbolTableRef<Type> getTypeReference() {
    	return this.typeRef;
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
     * method setType sets the elements type to typeConnectorRef
     */
    public void setType(SymbolTableRef<Type> type) {
        this.typeRef = type;
    }

    /**
     * Sets this element to be nillable.
     */
    public void setNillable() {
        nillable = true;
    }

    /**
     * Sets this element to be not nillable. Default.
     */
    public void setNotNillable() {
        nillable = false;
    }

    /**
     * Returns if the element is nillable or not. Default is false.
     */
    public boolean isNillable() {
        return nillable;
    }

    /**
     * Adds a {@link Constraint} to this element.
     */
    public void addConstraint(Constraint constraint) {
        constraints.add(constraint);
    }

    /**
     * Returns a list of {@link Constraint}s, defined in this element.
     */
    public LinkedList<Constraint> getConstraints() {
        return new LinkedList<Constraint>(constraints);
    }

    /**
     * Sets the element is abstract.
     * @param bAbstract
     */
    public void setAbstract(boolean Abstract) {
        this.Abstract = Abstract;
    }

    public Boolean getAbstract() {
        return Abstract;
    }

    @Override
    public String toString() {
        return getLocalName();
    }

	public void setProperties(ElementProperties elementProperties) {
		this.nillable = elementProperties.isNillable();
		this.defaultValue = elementProperties.getDefaultValue();
		this.fixedValue = elementProperties.getFixedValue();
	}

	public ElementProperties getProperties() {
		return new ElementProperties(defaultValue, fixedValue, nillable);
	}
}
