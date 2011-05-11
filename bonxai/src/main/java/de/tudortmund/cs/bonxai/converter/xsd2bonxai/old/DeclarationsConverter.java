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

import de.tudortmund.cs.bonxai.bonxai.Bonxai;
import de.tudortmund.cs.bonxai.bonxai.Declaration;
import de.tudortmund.cs.bonxai.bonxai.ImportList;
import de.tudortmund.cs.bonxai.bonxai.DataTypeList;

import de.tudortmund.cs.bonxai.xsd.XSDSchema;

/**
 * Bonxai2XSDConverter for declarations.
 *
 * This converter creates declaration in the given {@link de.tudortmund.cs.bonxai.bonxai.Bonxai} reflecting
 * the situation in the given {@link de.tudortmund.cs.bonxai.xsd.XSDSchema}. Declarations consist of
 * namespaces declarations, default namespace declarations, type library
 * inclusion and importing of external Bonxai and/or XML XSDSchema files.
 *
 * @TODO How do other converters know about the namespace declarations here?
 * @TODO Type library inclusion cannot be handled here directly, but must be
 *       added later in the conversion step.
 *
 */
class DeclarationsConverter implements Converter {

    /**
     * Creates a new declaration converter.
     *
     */
    public DeclarationsConverter() {
        // to do
    }

    /**
     * Creates declarations according to the given {@link de.tudortmund.cs.bonxai.xsd.XSDSchema} in the
     * given {@link de.tudortmund.cs.bonxai.bonxai.Bonxai}.
     *
     * This method receives the schema object to extract declarations for the
     * given bonxai object. It is responsible for namespaces, the default
     * namespace and inclusion declarations.
     *
     * For convenience reasons, the method resturns the given bonxai parameter
     * again, although it manipulates it directly.
     *
     * @see Bonxai2XSDConverter
     */
    public Bonxai convert( XSDSchema schema, TypeAutomaton automaton, Bonxai bonxai ) {
        bonxai.setDeclaration(
            new Declaration(
                new ImportList(),
                new DataTypeList(),
                schema.getNamespaceList()
            )
        );
        return bonxai;
    }
}
