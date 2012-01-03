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

import eu.fox7.schematoolkit.common.*;

/**
 * The Attribute class represents the basic XSD <attribute> tag, which has a
 * name and may have an optional connection to a SimpleType.
 */
public class Attribute extends AttributeParticle {
    public Attribute(QualifiedName name, QualifiedName simpleTypeName, String defaultValue, String fixedValue, AttributeUse use, XSDSchema.Qualification form, Annotation annotation) {
        this.name = name;
        this.simpleTypeName = simpleTypeName;
        this.defaultValue = defaultValue;
        this.fixedValue = fixedValue;
        this.use = use;
        this.form = form;
        this.setAnnotation(annotation);
    }

    private XSDSchema.Qualification form = XSDSchema.Qualification.unqualified;

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
    protected QualifiedName name;
    /**
     * A reference to a simple type.
     */
    protected QualifiedName simpleTypeName;
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
    public Attribute(QualifiedName name) {
        this.name = name;
        simpleTypeName = null;
    }

    /**
     * Creates a new attribute with the passed name and type.
     *
     * This constructor should ONLY be used with fully qualified names in the
     * form "{namespace}name".
     */
    public Attribute(QualifiedName name, QualifiedName simpleTypeName) throws UnexpectedTypeException {
        this.name = name;
        setSimpleTypeName(simpleTypeName);
    }

    /**
     * Returns the name of the attribute.
     *
     * This name is full qualified in the format "{<namespace>}<localName>",
     * where "<" and ">" are only meant as placeholder indicators and do not
     * belong to the qualified name itself.
     */
    public QualifiedName getName() {
        return name;
    }

    /**
     * Returns a clone of the simple type reference or null if no simple type
     * reference has been set.
     */
    public QualifiedName getSimpleTypeName() {
        return simpleTypeName;
    }

    /**
     * Sets the simple type reference.
     */
    public void setSimpleTypeName(QualifiedName simpleTypeName) {
        this.simpleTypeName = simpleTypeName;
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
