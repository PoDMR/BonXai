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
package de.tudortmund.cs.bonxai.bonxai;

/**
 * Attribute-Element of an AttributeList
 */
public class Attribute extends AttributeListElement {

    /**
     * The attribute type of Attribute contains the Bonxaitype of this attribute
     */
    private BonxaiType type;

    /**
     * If the attribute is required.
     */
    private boolean required = false;

    /**
     * Default value of the attribute.
     */
    private String defaultValue;

    /**
     * Fixed value of the attribute.
     */
    private String fixedValue;

    /**
     * Namespace of the attribute
     */
    private String namespace;

    /**
     * Constructor for the class Attribute
     *
     * @param namespace
     * @param name
     * @param type
     */
    public Attribute(String namespace, String name, BonxaiType type) {
        super(name);
        this.namespace = namespace;
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
    public Attribute(String namespace, String name, BonxaiType type, boolean required) {
        this(namespace, name, type);
        this.required = required;
    }

    /**
     * Constructor to set all attributes.
     *
     * @param namespace
     * @param name
     * @param type
     * @param required
     * @param defaultValue
     * @param fixedValue
     */
    public Attribute(String namespace, String name, BonxaiType type, boolean required, String defaultValue, String fixedValue) {
        this(namespace, name, type, required);
        this.defaultValue = defaultValue;
        this.fixedValue = fixedValue;
    }

    /**
     * Returns the namespace of the attribute
     *
     * @return namespace
     */
    public String getNamespace() {
        return this.namespace;
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
     * Returns the default value.
     *
     * Might be null if Attribute has not default value.
     */
    public String getDefault() {
        return defaultValue;
    }

    /**
     * Sets the default value.
     *
     * Might be null if Attribute has not default value.
     */
    public void setDefault(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    /**
     * Returns the fixed value.
     *
     * Might be null if Attribute has not fixed value.
     */
    public String getFixed() {
        return fixedValue;
    }

    /**
     * Sets the fixed value.
     *
     * Might be null if Attribute has not fixed value.
     */
    public void setFixed(String fixedValue) {
        this.fixedValue = fixedValue;
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
}

