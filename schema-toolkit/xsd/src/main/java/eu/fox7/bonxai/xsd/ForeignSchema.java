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

import eu.fox7.bonxai.common.AnnotationElement;

/**
 * The ForeginSchema class represents a schema that originates from another source.
 * This could be an imported schema, a schema that was included or a redefined
 * schema.
 */
public abstract class ForeignSchema extends AnnotationElement {
    XSDSchema schema;
    XSDSchema parentSchema;
    protected String schemaLocation;

    /**
     * A Foreignschema cannot exist on its own! Use IncludedSchema, ImportedSchema
     * or RedefinedSchema.
     * @param schemaLocation 
     */
    public ForeignSchema(String schemaLocation) {
        this.schemaLocation = schemaLocation;
    }

    public XSDSchema getSchema() {
        return schema;
    }

    public void setSchema(XSDSchema schema) {
        this.schema = schema;
    }

    /**
     * Returns the location of the included schema.
     * @return String   schemaLocation
     */
    public String getSchemaLocation() {
        return this.schemaLocation;
    }

    public XSDSchema getParentSchema() {
        return parentSchema;
    }

    public void setParentSchema(XSDSchema parentSchema) {
        this.parentSchema = parentSchema;
    }

    public void setSchemaLocation(String schemaLocation) {
        this.schemaLocation = schemaLocation;
    }

}

