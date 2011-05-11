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
package de.tudortmund.cs.bonxai.common;

/**
 * Class for representing namespaces with identifiers
 */
public class IdentifiedNamespace extends Namespace implements DualHashtableElement, Comparable<IdentifiedNamespace> {

    /**
     * Identifier of namespace
     */
    private String identifier;
    /**
     * Uri of namespace
     */
    private String uri;

    /**
     * Constructor for class IdentifiedNamespace
     * @param identifier
     * @param uri
     */
    public IdentifiedNamespace(String identifier, String uri) {
        super(uri);
        this.uri = super.uri;
        this.identifier = identifier;
    }

    /**
     * Returns uri of namespace
     * @return uri
     */
    public String getUri() {
        return uri;
    }

    /**
     * Returns identifier of namespace
     * @return identifier
     */
    public String getIdentifier() {
        return identifier;
    }

    /**
     * String serialization of namespace, for string comparison.
     */
    @Override
    public String toString() {
        return identifier + "=" + uri;
    }

    public int compareTo(IdentifiedNamespace o) {
        return uri.compareTo(o.uri);
    }
}

