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

import java.util.LinkedHashSet;

/**
 * List of Namespaces
 */
public class NamespaceList {

    /**
     * Attribute holding the default namespace
     */
    private DefaultNamespace defaultNameSpace;
    /**
     * Hashtable of all namespaces
     */
    private DualHashtable namespaces = new DualHashtable();

    /**
     * Constructor for the class NamespaceList
     * @param defaultNameSpace
     */
    public NamespaceList(DefaultNamespace defaultNameSpace) {
        this.defaultNameSpace = defaultNameSpace;
    }

    /**
     * Returns the default namespace
     * @return defaultNameSpace
     */
    public DefaultNamespace getDefaultNamespace() {
        return defaultNameSpace;
    }

    /**
     * Setter for the default namespace
     * @param defaultNamespace
     */
    public void setDefaultNamespace(DefaultNamespace defaultNamespace) {
        this.defaultNameSpace = defaultNamespace;
    }

    /**
     * Adds a namespace to the hashtable of namespaces
     * @param identifiedNamespace
     */
    public void addIdentifiedNamespace(IdentifiedNamespace identifiedNamespace) {
        namespaces.addElement(identifiedNamespace);
    }

    /**
     * Return a list of namespace URIs
     * @return HashSet<IdentifiedNamespace>
     */
    public LinkedHashSet<IdentifiedNamespace> getIdentifiedNamespaces() {
        LinkedHashSet<IdentifiedNamespace> newNamespaces = new LinkedHashSet<IdentifiedNamespace>();
        for (String uri: this.namespaces.getUris()) {
            newNamespaces.add(this.getNamespaceByUri(uri));
        }

        return newNamespaces;
    }

    /**
     * Returns namespace with parametrised identifier if exists in hashtable
     * @param identifier
     * @return identifiedNamespace
     */
    public IdentifiedNamespace getNamespaceByIdentifier(String identifier) {
        if (identifier != null) {
            return new IdentifiedNamespace(identifier, namespaces.getByIdentifier(identifier));
        }
        return null;
    }

    /**
     * Returns namespace with parametrised uri if exists in hashtable
     * @param uri
     * @return identifiedNamespace
     */
    public IdentifiedNamespace getNamespaceByUri(String uri) {
        if (uri != null) {
            return new IdentifiedNamespace(namespaces.getByUri(uri), uri);
        }
        return null;
    }
}
