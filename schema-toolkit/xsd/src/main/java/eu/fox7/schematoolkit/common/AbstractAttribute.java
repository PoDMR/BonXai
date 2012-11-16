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

package eu.fox7.schematoolkit.common;


public abstract class AbstractAttribute extends AttributeParticle {
    public AbstractAttribute(QualifiedName name, String defaultValue, String fixedValue, AttributeUse use) {
        this.name = name;
        this.defaultValue = defaultValue;
        this.fixedValue = fixedValue;
        this.use = use;
    }
    
    public AbstractAttribute() {
    }

    /**
     * The name of the attribute.
     *
     * This name is full qualified in the format "{<namespace>}<localName>",
     * where "<" and ">" are only meant as placeholder indicators and do not
     * belong to the qualified name itself.
     */
    private QualifiedName name;
    /**
     * Use flag.
     */
    private AttributeUse use = AttributeUse.optional;

    /**
     * Default value.
     */
    private String defaultValue;
    
    /**
     * Fixed value.
     */
    private String fixedValue;

    /**
     * Creates a new attribute with the passed name.
     *
     * This constructor should ONLY be used with fully qualified names in the
     * form "{namespace}name".
     *
     * @TODO: This seems to be an unsafe constructor - Attribute should always
     * have a type associated.
     */
    public AbstractAttribute(QualifiedName name) {
        this.name = name;
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

	public void setName(QualifiedName name) {
		this.name = name;
	}
}
