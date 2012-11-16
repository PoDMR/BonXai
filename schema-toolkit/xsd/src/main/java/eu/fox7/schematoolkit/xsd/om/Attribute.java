/**
 * Copyright 2009-2012 TU Dortmund
 *
 * This file is part of FoXLib.
 *
 * FoXLib is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * FoXLib is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with BonXai.  If not, see <http://www.gnu.org/licenses/>.
 */

package eu.fox7.schematoolkit.xsd.om;

import eu.fox7.schematoolkit.common.*;
import eu.fox7.schematoolkit.xsd.saxparser.NamedXSDElement;

/**
 * The Attribute class represents the basic XSD <attribute> tag, which has a
 * name and may have an optional connection to a SimpleType.
 */
public class Attribute extends AbstractAttribute implements NamedXSDElement, TypedXSDElement {

    private XSDSchema.Qualification form = XSDSchema.Qualification.unqualified;

    public Attribute(QualifiedName name, QualifiedName simpleTypeName, String defaultValue, String fixedValue, AttributeUse use, XSDSchema.Qualification form, Annotation annotation) {
        super(name, defaultValue, fixedValue, use);
        this.simpleTypeName = simpleTypeName;
        this.form = form;
        this.setAnnotation(annotation);
    }

    
    /**
     * Creates a new attribute with the passed name and type.
     *
     * This constructor should ONLY be used with fully qualified names in the
     * form "{namespace}name".
     */
    public Attribute(QualifiedName name, QualifiedName simpleTypeName) throws UnexpectedTypeException {
        super(name);
        setSimpleTypeName(simpleTypeName);
    }
    
    public Attribute() {}
    
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
     * A reference to a simple type.
     */
    protected QualifiedName simpleTypeName;

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
    
    public void setTypeName(QualifiedName typeName) {
    	this.setSimpleTypeName(typeName);
    }

}
