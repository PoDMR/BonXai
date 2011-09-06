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
package eu.fox7.bonxai.common;

import java.util.Hashtable;
import java.util.TreeSet;

/**
 * This utility class conists of two Hashtables so that both key and value are searchable.
 * In package bonxai it is only used for DataTypes and IdentifiedNamespaces, which both implement interface DualHashtableElement
 */
public class DualHashtable {

    /**
     * Hashtable for identifier search
     * The first String is the key, in this case the Identifier, and the second String contains the value, in this case the Uri.

     */
    private Hashtable<String, String> byIdentifier = new Hashtable<String, String>();
    /**
     * Hashtable for uri search
     * The first String is the key, in this case the Uri, and the second String contains the value, in this case the Identifier.
     */
    private Hashtable<String, String> byUri = new Hashtable<String, String>();

    /**
     * Returns an uri for the identifier
     * @param identifier
     * @return uri
     */
    public String getByIdentifier(String identifier) {
        return byIdentifier.get(identifier);
    }

    /**
     * Returns an identifier for the uri
     * @param uri
     * @return identifier
     */
    public String getByUri(String uri) {
        return byUri.get(uri);
    }

    /**
     * Adds dual-hashtable-element to the dualHashtable
     * @param dualHashtableElement
     */
    public void addElement(DualHashtableElement dualHashtableElement) {
        byIdentifier.put(dualHashtableElement.getIdentifier(), dualHashtableElement.getUri());
        byUri.put(dualHashtableElement.getUri(), dualHashtableElement.getIdentifier());
    }

    /**
     * Returns true if uri existis in the dualHashtable
     * @param uri
     * @return boolean
     */
    public boolean containsUri(String uri){
        return byUri.containsKey(uri);
    }

    /**
     * Returns true if identifier existis in the dualHashtable
     * @param identifier
     * @return boolean
     */
    public boolean containsIdentifier(String identifier){
        return byIdentifier.containsKey(identifier);
    }

    /**
     * Returns an ordered list of URIs inside the table.
     */
    public TreeSet<String> getUris(){
        return new TreeSet<String>(byUri.keySet());
    }
}
