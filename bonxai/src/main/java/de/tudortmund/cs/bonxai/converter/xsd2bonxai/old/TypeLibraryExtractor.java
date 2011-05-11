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
package de.tudortmund.cs.bonxai.converter.xsd2bonxai.old;

import java.util.HashMap;

import de.tudortmund.cs.bonxai.common.SymbolAlreadyRegisteredException;

import de.tudortmund.cs.bonxai.xsd.XSDSchema;
import de.tudortmund.cs.bonxai.xsd.Type;
import de.tudortmund.cs.bonxai.xsd.SimpleType;

/**
 * Class to extract XSD {@link SimpleType} library.
 */
class TypeLibraryExtractor {

    /**
     * Extracts {@link SimpleType} libraries from originalSchema.
     *
     * This method creates a set of new {@link XSDSchema}s which contain custom
     * {@link SimpleType}s defined in originalSchema. These type libraries are
     * meant to be imported into a converted {@link Bonxai} to make custom {@link
     * SimpleType}s available. The returned set might contain 0, 1 or more
     * {@link XSDSchema} objects, one for each namespace contained in the
     * originalSchema.
     *
     * Note: If this method returns null, no type library is needed.
     *
     * @return XSDSchema The type library.
     */
    public HashMap<String, XSDSchema> extractTypeLibraries(XSDSchema originalSchema) {
        HashMap<String, XSDSchema> result = new HashMap<String, XSDSchema>();

        for (Type type : originalSchema.getTypes()) {
            if (type instanceof SimpleType && !type.getNamespace().equals(XSDSchema.XSD_NAMESPACE)) {
                if (!result.containsKey(type.getNamespace())) {
                    this.createResultSchema(result, originalSchema, type.getNamespace());
                }

                try {
                    result.get(type.getNamespace()).addType(
                        result.get(type.getNamespace()).getTypeSymbolTable().createReference(
                            type.getName(),
                            type
                        )
                    );
                } catch (SymbolAlreadyRegisteredException e) {
                    throw new RuntimeException("Duplicate type in source schema " + type.getName(), e);
                }
            }
        }

        return result;
    }

    private void createResultSchema(HashMap<String, XSDSchema> result, XSDSchema originalSchema, String namespace) {
        XSDSchema newSchema = new XSDSchema(namespace);

        if (originalSchema.getNamespaceList().getNamespaceByUri(namespace) != null) {
            newSchema.getNamespaceList().addIdentifiedNamespace(
                originalSchema.getNamespaceList().getNamespaceByUri(namespace)
            );
        }

        result.put(namespace, newSchema);
    }
}
