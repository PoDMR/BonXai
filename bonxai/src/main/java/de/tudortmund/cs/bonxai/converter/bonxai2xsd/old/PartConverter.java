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
package de.tudortmund.cs.bonxai.converter.bonxai2xsd.old;

import java.util.HashMap;

import de.tudortmund.cs.bonxai.xsd.*;

/**
 * Basic converter for parts of an Bonxai schema into the respective XSD schemas.
 */
public abstract class PartConverter {

    /**
     * Hashmap for all created schemas identified by their target namespace.
     */
    protected HashMap<String,XSDSchema> schemas;

    /**
     * Base constructor for part converters.
     *
     * @param schemas
     */
    public PartConverter(HashMap<String,XSDSchema> schemas) {
        this.schemas = schemas;
    }

    /**
     * Get schema for given namespace.
     *
     * Return the XSD schema for the given namespace. Creates a new schema
     * object, if the namespace does not yet exist.
     *
     * @param namespace
     */
    protected XSDSchema getSchema( String namespace )
    {
        if ( this.schemas.containsKey(namespace) )
        {
            return this.schemas.get(namespace);
        }

        XSDSchema xsd = new XSDSchema();
        this.schemas.put(namespace, xsd);
        return xsd;
    }
}

