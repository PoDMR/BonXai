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

import de.tudortmund.cs.bonxai.common.*;

/**
 * The Attribute class represents the basic XSD <attribute> tag, which has a
 * name and may have an optional connection to a SimpleType.
 */
public class Attribute extends AttributeParticle {

    protected boolean dummy;

    public boolean isDummy() {
        return dummy;
    }

    public void setDummy(boolean dummy) {
        this.dummy = dummy;
    }

    public Attribute(String name, SymbolTableRef<Type> simpleTypeRef, String defaultValue, String fixedValue, AttributeUse use, boolean typeAttr, XSDSchema.Qualification form, Annotation annotation) {
        this.name = name;
        this.simpleTypeRef = simpleTypeRef;
        this.defaultValue = defaultValue;
        this.fixedValue = fixedValue;
        this.use = use;
        this.typeAttr = typeAttr;
        this.form = form;
        this.setAnnotation(annotation);
    }

    public Attribute(String name, String defaultValue, String fixedValue, AttributeUse use, boolean typeAttr, XSDSchema.Qualification form, Annotation annotation) {
        this.name = name;
        this.defaultValue = defaultValue;
        this.fixedValue = fixedValue;
        this.use = use;
        this.typeAttr = typeAttr;
        this.form = form;
        this.setAnnotation(annotation);
    }
    /**
     * Is set true if the type was set in the type-attribute.
     */
    protected boolean typeAttr = false;
    private XSDSchema.Qualification form = XSDSchema.Qualification.unqualified;

    public boolean getTypeAttr() {
        return typeAttr;
    }

    public void setTypeAttr(boolean typeAttr) {
        this.typeAttr = typeAttr;
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
     * The name of the attribute.
     *
     * This name is full qualified in the format "{<namespace>}<localName>",
     * where "<" and ">" are only meant as placeholder indicators and do not
     * belong to the qualified name itself.
     */
    protected String name;
    /**
     * A reference to a simple type.
     */
    protected SymbolTableRef<Type> simpleTypeRef;
    /**
     * Use flag.
     */
    protected AttributeUse use = AttributeUse.Optional;
    /**
     * Default value.
     */
    String defaultValue;
    /**
     * Fixed value.
     */
    String fixedValue;

    /**
     * Creates a new attribute with the passed name.
     *
     * This constructor should ONLY be used with fully qualified names in the
     * form "{namespace}name".
     *
     * @TODO: This seems to be an unsafe constructor - Attribute should always
     * have a type associated.
     */
    public Attribute(String name) {
        if ((name.length() < 2)
                || (!name.startsWith("{"))
                || (!name.contains("}"))) {
            throw new RuntimeException("Only fully qualified names are allowed.");
        }
        this.name = name;
        simpleTypeRef = null;
    }

    /**
     * Creates a new attribute with the passed name and type.
     *
     * This constructor should ONLY be used with fully qualified names in the
     * form "{namespace}name".
     */
    public Attribute(String name, SymbolTableRef<Type> simpleTypeRef) throws UnexpectedTypeException {
        if ((name.length() < 2)
                || (!name.startsWith("{"))
                || (!name.contains("}"))) {
            throw new RuntimeException("Only fully qualified names are allowed.");
        }
        this.name = name;
        setSimpleType(simpleTypeRef);
    }

    /**
     * Returns the name of the attribute.
     *
     * This name is full qualified in the format "{<namespace>}<localName>",
     * where "<" and ">" are only meant as placeholder indicators and do not
     * belong to the qualified name itself.
     */
    public String getName() {
        return name;
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
     * Returns a clone of the simple type reference or null if no simple type
     * reference has been set.
     */
    public SimpleType getSimpleType() {
        if (simpleTypeRef != null) {
            return (SimpleType) simpleTypeRef.getReference();
        } else {
            return null;
        }
    }

    /**
     * Sets the simple type reference.
     */
    public void setSimpleType(SymbolTableRef<Type> simpleTypeRef) throws UnexpectedTypeException {
        if (simpleTypeRef.getReference() != null
                && !(simpleTypeRef.getReference() instanceof SimpleType)) {
            throw new UnexpectedTypeException(simpleTypeRef.getReference(), "SimpleType");
        }
        this.simpleTypeRef = simpleTypeRef;
    }

    /**
     * Set the use flag.
     */
    public void setUse(AttributeUse use) {
        this.use = use;
    }

    /**
     * Returns the use flag.
     */
    public AttributeUse getUse() {
        return use;
    }

    /**
     * Set default value.
     */
    public void setDefault(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    /**
     * Get default value.
     */
    public String getDefault() {
        return defaultValue;
    }

    /**
     * Set fixed value.
     */
    public void setFixed(String fixedValue) {
        this.fixedValue = fixedValue;
    }

    /**
     * Get fixed value.
     */
    public String getFixed() {
        return fixedValue;
    }
}
