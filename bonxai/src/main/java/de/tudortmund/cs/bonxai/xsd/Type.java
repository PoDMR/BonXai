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

import de.tudortmund.cs.bonxai.common.AnnotationElement;

/**
 * Abstract Type Class
 */
public abstract class Type extends AnnotationElement {

    protected boolean isAnonymous = false;
    protected boolean dummy;

    public boolean isDummy() {
        return dummy;
    }

    public void setDummy(boolean dummy) {
        this.dummy = dummy;
    }

    /**
     * The name of the type.
     *
     * This name is full qualified in the format "{<namespace>}<localName>",
     * where "<" and ">" are only meant as placeholder indicators and do not
     * belong to the qualified name itself.
     */
    protected String name;

    /**
     * Constructor with namespace and name passed in one string .
     *
     * This constructor should ONLY be used with fully qualified names in the
     * form "{namespace}name".
     * @param name
     */
    public Type(String name) {
        if ((name.length() < 2)
                || (!name.startsWith("{"))
                || (!name.contains("}"))) {
            throw new RuntimeException("Only fully qualified names are allowed.");
        }
        this.name = name;
        this.isAnonymous = false;
    }

    /**
     * Creates a type with namespace and name passed in two strings.
     *
     * The constructor sets the name string in the form "{namespace}name".
     *
     * If this constructor with a explicit namespace is used, the name should
     * be a local name and NO fully qualified name, while the namespace should
     * be the full namespace URL.
     * @param namespace
     * @param name
     */
    public Type(String namespace, String name) {
        this("{" + namespace + "}" + name);
        this.isAnonymous = false;
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
     * Returns the name of the type.
     *
     * This name is full qualified in the format "{<namespace>}<localName>",
     * where "<" and ">" are only meant as placeholder indicators and do not
     * belong to the qualified name itself.
     * @return String
     */
    public String getName() {
        return this.name;
    }

    /**
     * Replaces the name of the type.
     *
     * This setter should ONLY be used with fully qualified names in the
     * form "{namespace}name".
     * @param name
     */
    public void setName(String name) {
        if ((name.length() < 2)
                || (!name.startsWith("{"))
                || (!name.contains("}"))) {
            throw new RuntimeException("Only fully qualified names are allowed.");
        }
        this.name = name;
    }

    /**
     * Compare the object with that object.
     *
     * This is a specialized implementation of equals(), which only checks the
     * name of the type. This is sensible since types in XSD must have a unique
     * name there must not exist 2 types with the same name.
     * @param that
     */
    @Override
    public boolean equals(Object that) {
        return ((that instanceof Type)
                && this.name.equals(((Type) that).name));
    }

    public boolean isAnonymous() {
        return this.isAnonymous;
    }

    public void setIsAnonymous(boolean isAnonymous) {
        this.isAnonymous = isAnonymous;
    }


    @Override
    public String toString() {
        return this.name;
    }
}
