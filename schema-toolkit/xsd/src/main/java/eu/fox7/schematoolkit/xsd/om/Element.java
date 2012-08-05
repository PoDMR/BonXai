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

import eu.fox7.schematoolkit.common.*;
import eu.fox7.schematoolkit.xsd.saxparser.NamedXSDElement;

import java.util.HashSet;

/**
 * XSD Element.
 */
public class Element extends eu.fox7.schematoolkit.common.Element implements TypedXSDElement, NamedXSDElement {

	
    public boolean getNillable() {
        return nillable;
    }

    /**
     * The type of the element.
     *
     * Must be a valid {@link SymbolTableRef} pointing to a {@link SimpleType}
     * or {@link ComplexType}.
     */
    protected QualifiedName typeName;
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
    protected QualifiedName substitutionGroup;
    private XSDSchema.Qualification form = null;

	protected ElementProperties elementProperties = new ElementProperties();

    /**
     * Set default value.
     *
     * Can be null for no default.
     */
    public void setDefault(String defaultValue) {
        this.elementProperties.setDefaultValue(defaultValue);
    }

    /**
     * Get default value.
     *
     * Can be null for no default.
     */
    public String getDefault() {
        return this.elementProperties.getDefaultValue();
    }

    /**
     * Set fixed value.
     *
     * Can be null for no fixed value.
     */
    public void setFixed(String fixedValue) {
        this.elementProperties.setFixedValue(fixedValue);
    }

    /**
     * Get fixed value.
     *
     * Can be null for no fixed value.
     */
    public String getFixed() {
        return this.elementProperties.getFixedValue();
    }

    public void setProperties(ElementProperties elementProperties) {
    	this.elementProperties = elementProperties;
    }
    
    public ElementProperties getProperties() {
    	return this.elementProperties;
    }

    
    public QualifiedName getSubstitutionGroup() {
        return substitutionGroup;
    }

    public void setSubstitutionGroup(QualifiedName substitutionGroup) {
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
    
    public Element() {
    }

    /**
     * Creates an element with only a name.
     */
    public Element(QualifiedName name) {
        this.name = name;
    }

    /**
     * Constructor with name and type.
     */
    public Element(QualifiedName name, QualifiedName typeName) {
        this(name);
        setTypeName(typeName);
    }

    /**
     * Method getType returns the type of an element, saved in the
     * typeConnectorRef.
     */
    public QualifiedName getTypeName() {
        return this.typeName;
    }
    
    /**
     * method setType sets the elements type to typeConnectorRef
     */
    public void setTypeName(QualifiedName typeName) {
        this.typeName = typeName;
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
	public void setName(QualifiedName name) {
		this.name = name;
	}

}
