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

import de.tudortmund.cs.bonxai.xsd.*;
import de.tudortmund.cs.bonxai.common.*;

/**
 * PreProcessor splitting all ComplexTypes with SimpleContent into a SimpleType
 * and an optional SimpleContent
 *
 * This PreProcessor will create a SimpleType for each ComplexType with
 * SimpleContentRestriction to reflect its inner content. After that
 * Restriction will be converted to an extension of the newly created type.
 */
class SimpleContentPreProcessorVisitor implements PreProcessorVisitor {
    /**
     * XSDSchema currently being processed by the visitor
     */
    private XSDSchema currentSchema = null;

    /**
     * Called whenever the visiting of new schema starts.
     *
     * To be able to properly manipulate the schema structure during complex
     * type visiting, the schema reference is stored for each processed schema.
     */
    public void visitSchema( XSDSchema schema ) {
        this.currentSchema = schema;
    }

    /**
     * Visit each ComplexType and split up those with a SimpleContentRestriction object
     */
    public void visitComplexType( ComplexType type ) {
        if ( type.getContent() == null ) {
            return;
        }

        if ( !( type.getContent() instanceof SimpleContentType ) ) {
            return;
        }

        SimpleContentType content = ( SimpleContentType )type.getContent();

        if ( !( content.getInheritance() instanceof SimpleContentRestriction ) ) {
            return;
        }

        // At this point it is ensured that we are dealing with a
        // SimpleContentType defining a SimpleContentRestriction inheritance
        // structure.

        // Move all restriction information to a newly defined SimpleType to be
        // extracted to the TypeLibrary later on.

        // Create new unique simpletype name
        String simpleTypeNameBase = type.getName() + "-SimpleType";
        String simpleTypeName = simpleTypeNameBase;
        int uniqueid = 1;
        while( this.currentSchema.getTypeSymbolTable().hasReference( simpleTypeName ) ) {
            simpleTypeName = simpleTypeNameBase + "-" + uniqueid++;
        }

        SymbolTableRef<Type> simpleTypeRef = this.currentSchema.getTypeSymbolTable().updateOrCreateReference(
            simpleTypeName,
            new SimpleType(
                simpleTypeName,
                (SimpleContentRestriction)content.getInheritance()
            )
        );

        // Modify SimpleContent to be an Extension of the newly created type.
        // Attributes stored in the type itself will stay the same.
        content.setInheritance(
            new SimpleContentExtension( simpleTypeRef )
        );
    }

    /**
     * Checks if the given Type is a default XML XSDSchema type.
     *
     * This check is based on the types namespace. Each type having the XML
     * XSDSchema namespace is considered to be a default type.
     */
    private boolean isSchemaDefaultType( Type type ) {
        return type.getNamespace().equals( XSDSchema.XSD_NAMESPACE );
    }
}
