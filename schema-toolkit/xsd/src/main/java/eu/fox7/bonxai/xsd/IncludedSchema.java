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
package eu.fox7.bonxai.xsd;

/**
 * The IncludedSchema class represents a schema which should be included in the current schema.
 * The imported schema must have the same namespace as the current schema.
 */
public class IncludedSchema extends ForeignSchema {

    /**
     * Creates a new Included XSDSchema with a schemaLocation
     * @param schemaLocation 
     */
    public IncludedSchema (String schemaLocation) {
        super(schemaLocation);
    }


}
