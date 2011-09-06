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

/**
 * Bonxai Element realization.
 */
public class Element extends eu.fox7.bonxai.common.Element {

    /**
     * Namespace of the element.
     *
     * Must be a fully qualified URI.
     */
    protected String namespace;

    /**
     * The Type of this element.
     */
    protected BonxaiType type;

    /**
     * True if the element might be missing (nillable in XSD).
     */
    protected boolean missing = false;

    /**
     * Creates an element with namespace and name.
     */
    public Element (String namespace, String name) {
        this.namespace = namespace;
        this.name      = name;
    }

    /**
     * Creates an element with namespace, name and type.
     */
    public Element (String namespace, String name, BonxaiType type) {
        this(namespace, name);
        this.type = type;
    }

    /**
     * Creates an element with namespace, name, type and missing flag.
     */
    public Element (String namespace, String name, BonxaiType type, boolean missing) {
        this(namespace, name, type);
        this.missing = missing;
    }

    /**
     * Creates an element with namespace, name and missing flag.
     */
    public Element (String namespace, String name, boolean missing) {
        this(namespace, name);
        this.missing = missing;
    }

    /**
     * Returns the namespace of the element.
     *
     * The namespace is fully qualified. In the seldom case an element does not
     * reside in a namespace, null is returned.
     */
    public String getNamespace () {
        return namespace;
    }

    /**
     * Returns the type of the element.
     */
    public BonxaiType getType() {
        return type;
    }

    /**
     * If the element might be left empty (nillable in XSD).
     */
    public boolean isMissing() {
        return missing;
    }
}
