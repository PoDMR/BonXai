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
 * Abstract class for a prefix element implemented either as SingleSlashPrefixElement or as DoubleSlashPrefixElement
 */
public abstract class AncestorPatternElement extends AncestorPatternParticle {

    /**
     * Name of the element
     */
    private String name;

    /**
     * Fully qualified namespace of the element
     */
    private String namespace;

    /**
     * Constructor for the class Element
     * @param namespace
     * @param name
     */
    public AncestorPatternElement(String namespace, String name) {
        this.namespace = namespace;
        this.name      = name;
    }

    /**
     * Returns namespace of the element
     * @return namespace
     */
    public String getNamespace() {
        return namespace;
    }

    /**
     * Returns name of the element
     * @return name
     */
    public String getName() {
        return name;
    }
}

