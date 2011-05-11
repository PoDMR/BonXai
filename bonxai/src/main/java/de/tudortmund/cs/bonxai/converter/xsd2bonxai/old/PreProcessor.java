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

import java.util.LinkedList;

import de.tudortmund.cs.bonxai.common.*;
import de.tudortmund.cs.bonxai.xsd.*;

/**
 * PreProcessor used to run certain PreProcessVisitors on a given XSDSchema
 *
 * The PreProcessor is capable of running a series of PreProcessVisitors on a
 * given XSDSchema. After the preprocessor has been executed the given XSDSchema may
 * have been altered to reflect the changes made by the registered visitors.
 */
public class PreProcessor {
    /**
     * Linked list of registered visitors stored in the provided order
     */
    private LinkedList<PreProcessorVisitor> visitors;

    public PreProcessor() {
        this.visitors = new LinkedList<PreProcessorVisitor>();
    }

    /**
     * Add a visitor to the PreProcessor.
     *
     * All provided visitors are executed in the same order they have been
     * registered.
     *
     * Execution is done sequentially on the complete XSDSchema object. Therefore
     * manipulations of processors may already be uitlized by successor.
     */
    public void addVisitor( PreProcessorVisitor visitor ) {
        this.visitors.add( visitor );
    }

    /**
     * Process the given XSDSchema using all of the registered PreProcessors.
     *
     * The provided XSDSchema may be modified by the Visitors during this run.
     *
     * All processors are run sequentially on the full schema object. Therefore
     * manipulations of processors may already be uitlized by successor.
     */
    public void process( XSDSchema schema ) {
        // Process the schema once for every visitor registered
        for( PreProcessorVisitor visitor: this.visitors ) {
            this.processWithVisitor( schema, visitor );
        }
    }

    /**
     * Process the given XSDSchema with a defined visitor.
     *
     * The visitComplexType method will be called once on the visitor for each
     * complex type defined in the XSDSchema.
     */
    protected void processWithVisitor( XSDSchema schema, PreProcessorVisitor visitor ) {
        // Tell the visitor which XSDSchema he is visiting currently
        visitor.visitSchema( schema );

        // All Types are stored in the type symboltable of the schema.
        // Therefore an iteration over all complex types in this symboltable
        // does the trick.
        SymbolTableFoundation<Type> typeSymbolTable = schema.getTypeSymbolTable();
        for( Type type: typeSymbolTable.getAllReferencedObjects() ) {
            if ( type instanceof ComplexType ) {
                visitor.visitComplexType( (ComplexType)type );
            }
        }
    }
}
