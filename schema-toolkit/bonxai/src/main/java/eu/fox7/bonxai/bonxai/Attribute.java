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
package eu.fox7.bonxai.bonxai;

import eu.fox7.bonxai.common.AttributeParticle;
import eu.fox7.bonxai.common.QualifiedName;

/**
 * Attribute-Element of an AttributeList
 */
public class Attribute extends AttributeParticle {

    /**
     * The attribute type of Attribute contains the Bonxaitype of this attribute
     */
    private BonxaiType type;

    /**
     * If the attribute is required.
     */
    private boolean required = false;

    private QualifiedName name;
    /**
     * Constructor for the class Attribute
     *
     * @param namespace
     * @param name
     * @param type
     */
    public Attribute(QualifiedName name, BonxaiType type) {
        this.name = name;
        this.type      = type;
    }

    /**
     * Constructor for the class Attribute
     *
     * @param namespace
     * @param name
     * @param type
     * @param required
     */
    public Attribute(QualifiedName name, BonxaiType type, boolean required) {
        this(name, type);
        this.required = required;
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
        return required;
    }

    /**
     * Makes the attribute required.
     */
    public void setRequired() {
        required = true;
    }

    /**
     * Makes the attribute optional (not required).
     */
    public void setOptional() {
        required = false;
    }

    /**
     * Dump the object as a string.
     */
    @Override
    public String toString() {
        String retVal = "{ "
            + super.toString()
            + ", required: " + required
            + ", Type: " + type
            + "}";
        return retVal;
    }

	public QualifiedName getName() {
		return name;
	}
}

