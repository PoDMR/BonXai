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

import de.tudortmund.cs.bonxai.common.IdentifiedNamespace;
import de.tudortmund.cs.bonxai.common.NamespaceList;
import de.tudortmund.cs.bonxai.bonxai.*;
import de.tudortmund.cs.bonxai.xsd.*;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Bonxai2XSDConverter for the declaration block of Bonxai to the corresponding XML Schemas
 */
public class DeclarationConverter extends PartConverter {

    /**
     * The resulting xml schemas have to exist before using them in this
     * converter
     *
     * @param schemas
     */
    public DeclarationConverter(HashMap<String, XSDSchema> schemas) {
        super(schemas);
    }

    /**
     * convert.
     * Method to convert a declaration block of a given bonxai-schema into xsd
     * schemas
     *
     * @param schema
     */
    public void convert(de.tudortmund.cs.bonxai.bonxai.Bonxai bonxai) {

        Declaration bonxaiDeclaration = bonxai.getDeclaration();

        /**
         * The conversion of the parts of the bonxai declaration block hase to be
         * done for each XML XSDSchema
         *
         */
        for (String currentSchemaKey : this.schemas.keySet()) {

            // conversion of the namespace list
            if (bonxaiDeclaration != null && bonxaiDeclaration.getNamespaceList() != null) {

                NamespaceList newNamespaceList = new NamespaceList(bonxaiDeclaration.getNamespaceList().getDefaultNamespace());

                Iterator<IdentifiedNamespace> identifiedNamespaceIterator = bonxaiDeclaration.getNamespaceList().getIdentifiedNamespaces().iterator();

                while (identifiedNamespaceIterator.hasNext()) {
                    IdentifiedNamespace currentIdentifiedNamespace = (IdentifiedNamespace) identifiedNamespaceIterator.next();

                    /**
                     * @TODO: What more has to be done here? Are there any
                     * constraints for the addition of a namespace to a schema?
                     */
                    newNamespaceList.addIdentifiedNamespace(currentIdentifiedNamespace);
                }

                this.schemas.get(currentSchemaKey).setNamespaceList(newNamespaceList);

            }

            // conversion of the import list
            // This might be complete here.
            if (bonxaiDeclaration != null && bonxaiDeclaration.getImportList() != null) {
                for (String currentImportUri : bonxaiDeclaration.getImportList().getImportList().getUris()) {
                    Import currentImport = bonxaiDeclaration.getImportList().getImportByUri(currentImportUri);
                    ImportedSchema importedSchema = new ImportedSchema(currentImport.getIdentifier(), currentImport.getUri());
                    this.schemas.get(currentSchemaKey).addForeignSchema(importedSchema);
                }
            }

            // conversion of the datatype list
            // This might be complete here.
            if (bonxaiDeclaration != null && bonxaiDeclaration.getDataTypeList() != null) {
                for (String currentDataTypeUri : bonxaiDeclaration.getDataTypeList().getUris()) {
                    DataType currentDataType = bonxaiDeclaration.getDataTypeList().getDataTypeByUri(currentDataTypeUri);
                    ImportedSchema importedSchema = new ImportedSchema(currentDataType.getIdentifier(), currentDataType.getUri());
                    this.schemas.get(currentSchemaKey).addForeignSchema(importedSchema);
                // @TODO: What has to be done here?
                }
            }

        }
    }
}

