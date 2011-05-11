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
 * Class representing types in Bonxai
 */
public class BonxaiType {

    /**
     * Type name (XSD simple type).
     */
    private String type;

    /**
     * Namespace the type resides in.
     *
     * Usually the XSD namespace, but can also be different, for imported
     * custom sumple types.
     */
    private String namespace;

    /**
     * Constructor for the class BonxaiType which sets namespace and name
     */
    public BonxaiType(String namespace, String type) {
        this.namespace = namespace;
        this.type = type;
    }

    /**
     * Returns the name of this type
     * @return type
     */
    public String getType() {
        return type;
    }

    /**
     * Returns the namespace this type resides in.
     */
    public String getNamespace() {
        return namespace;
    }

    /**
     * Returns the full qualified name of the type in {<namespace>}<name> format.
     */
    public String getFullQualifiedName() {
        return "{" + namespace + "}" + type;
    }


    /**
     * Dumps the object as a string.
     */
    public String toString() {
        String retVal = "{ "
            + "type: " + type + ", "
            + "}";
        return retVal;
    }
}

