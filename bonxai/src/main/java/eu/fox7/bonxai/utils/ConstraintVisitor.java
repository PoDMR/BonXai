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
package eu.fox7.bonxai.utils;
import java.util.LinkedList;

import eu.fox7.bonxai.common.*;
import eu.fox7.bonxai.xsd.*;

/**
 * Package internal base implementation to visit every constraint in a given
 * xsd schema datastructure.
 *
 * This visitor class implements the vistor logic only. Therefore an extending
 * class needs to be written to get any work done on the constraints.
 */

abstract class ConstraintVisitor {
    /**
     * Entry point of this visitor.
     *
     * The method takes a XSDSchema as input and takes care of visiting each
     * Constraint in the given XSDSchema.
     */
    public void visitSchema( XSDSchema schema ) {
        // Vist global elements first
        for( eu.fox7.bonxai.xsd.Element element: schema.getElements() ) {
            this.visitElement( element );
        }

        // Handle the possible SymbolTables seperately
        /*
         * This is some sort of ugly hack. But using the current SymbolTable
         * structure I don't see a better solution. The SymbolTableFoundation
         * interface would need to implement a general method for retrieving
         * all elements from a SymbolTable. This is not trivial, because there
         * is no unique structure of stored SymbolTableRef information.
         */
        if ( schema.getTypeSymbolTable() instanceof SymbolTable<?> ) {
            // Visit all ComplexTypes
            for( SymbolTableRef<Type> typeReference: ((SymbolTable<Type>)schema.getTypeSymbolTable()).getReferences() ) {
                if ( typeReference.getReference() instanceof ComplexType ) {
                    this.visitComplexType( (ComplexType)typeReference.getReference() );
                }
            }
        }
        else if ( schema.getTypeSymbolTable() instanceof MultiReferenceSymbolTable<?> ) {
            // Visit all ComplexTypes
            for( LinkedList<SymbolTableRef<Type>> typeList: ((MultiReferenceSymbolTable<Type>)schema.getTypeSymbolTable()).getAllReferences() ) {
                for( SymbolTableRef<Type> typeReference: typeList ) {
                    if ( typeReference.getReference() instanceof ComplexType ) {
                        this.visitComplexType( (ComplexType)typeReference.getReference() );
                    }
                }
            }
        }
    }

    /**
     * Vist a given Element
     */
    protected void visitElement( eu.fox7.bonxai.xsd.Element element ) {
        // Visit all stored Constraints
        for( Constraint constraint: element.getConstraints() ) {
            this.visitConstraint( constraint );
        }
    }

    /**
     * Vist a given ComplexType
     */
    protected void visitComplexType( ComplexType complexType ) {
        if ( complexType.getContent() instanceof ComplexContentType ) {
            this.visitComplexContent( (ComplexContentType)complexType.getContent() );
        }
    }

    /**
     * Visit a complexContent stored in a ComplexType
     */
    protected void visitComplexContent( ComplexContentType content ) {
        if ( content.getParticle() != null ) {
            this.visitParticle( content.getParticle() );
        }
    }

    /**
     * Vist a Particle
     *
     * This does include the possibility of getting a ParticleContainer as
     * argument
     */
    protected void visitParticle( Particle particle ) {
        if ( particle instanceof eu.fox7.bonxai.xsd.Element ) {
            this.visitElement( (eu.fox7.bonxai.xsd.Element)particle );
        }
        else if ( particle instanceof ParticleContainer ) {
            this.visitParticleContainer( (ParticleContainer)particle );
        }
    }

    /**
     * Visit a ParticleContainer and loop through each of its entries
     */
    protected void visitParticleContainer( ParticleContainer container ) {
        for( Particle particle: container.getParticles() ) {
            this.visitParticle( particle );
        }
    }

    /**
     * Method which needs to be implemented by every ConstraintVisitor
     * subclass.
     *
     * It is guaranteed to be called on each Constraint of the provided XSDSchema
     * once.
     */
    abstract protected void visitConstraint( Constraint constraint );
}
