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

package eu.fox7.schematoolkit.bonxai.om;

import eu.fox7.schematoolkit.common.AbstractAttribute;
import eu.fox7.schematoolkit.common.AttributeUse;
import eu.fox7.schematoolkit.common.QualifiedName;

/**
 * Attribute-Element of an AttributeList
 */
public class Attribute extends AbstractAttribute {
    /**
     * The attribute type of Attribute contains the Bonxaitype of this attribute
     */
    private BonxaiType type;

    /**
     * Constructor for the class Attribute
     *
     * @param name
     * @param type
     */
    public Attribute(QualifiedName name, BonxaiType type) {
    	super(name);
    	this.type = type;
        this.setDefault(type.getDefaultValue());
        this.setFixed(type.getFixedValue());
    }

    /**
     * Constructor for the class Attribute
     *
     * @param name
     * @param type
     * @param required
     */
    public Attribute(QualifiedName name, BonxaiType type, boolean required) {
        this(name, type);
        this.setUse(required?AttributeUse.required:AttributeUse.optional);
    }


    /**
     * Returns the Bonxaitype of this attribute
     * @return type
     */
    public BonxaiType getType() {
        return type;
    }

    /**
     * Returns if the attribute is required.
     */
    public boolean isRequired() {
		return this.getUse()==AttributeUse.required;
    }

    /**
     * Makes the attribute required.
     */
    public void setRequired() {
        this.setUse(AttributeUse.required);
    }

    /**
     * Makes the attribute optional (not required).
     */
    public void setOptional() {
        this.setUse(AttributeUse.optional);
    }

    /**
     * Dump the object as a string.
     */
    @Override
    public String toString() {
        String retVal = "{ "
            + super.toString()
            + ", required: " + this.isRequired()
            + ", Type: " + type
            + "}";
        return retVal;
    }
}

