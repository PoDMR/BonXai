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
package eu.fox7.schematoolkit.xsd.om;

import eu.fox7.schematoolkit.common.Namespace;

/**
 * The ImportedSchema class represents the namespace of an imported schema which
 * elements can be used within the current schema.
 *
 */
public class ImportedSchema extends ForeignSchema {

    protected Namespace namespace;

    /**
     * Creates a new Foreignschema with namespace and location of the imported
     * schema.
     *
     * @TODO: <imported...> may be defined using a namespace OR a schema
     * location. Therefore the parser has to take care of possibly resolving
     * the missing namespace or schemalocation and setting the correct mapping.
     *
     * @param namespace
     * @param schemaLocation
     **/
    public ImportedSchema (Namespace namespace, String schemaLocation) {
        super(schemaLocation);
        this.namespace = namespace;
    }

    /**
     * Returns the namespace of the imported schema.
     * @return namespace    String
     */
    public Namespace getNamespace () {
        return namespace;
    }
}
