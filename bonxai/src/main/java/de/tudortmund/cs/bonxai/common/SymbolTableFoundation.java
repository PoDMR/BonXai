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
package de.tudortmund.cs.bonxai.common;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Set;

/**
 * Simple interface every SymbolTable needs to implement
 *
 * This interface defines the most basic functionallity a SymbolTable needs to
 * take care of.
 *
 * More sophisticated implementations may provide extra functionallity, but
 * this minimal subset of methods needs to be implemented by every SymbolTable.
 */
public interface SymbolTableFoundation<T> {
    /**
     * Get symbol table reference for a given key
     *
     * If the provided key is registered the associated reference needs to be
     * returned.
     *
     * If the provided key is not registered an empty (aka. null referencing)
     * SymbolTableRef needs to be created, stored and returned.
     *
     * In all situations a valid SymbolTableRef objekt needs to be returned.
     */
    public SymbolTableRef<T> getReference( String key );

    /**
     * Create a new symbol table reference and and return it.
     *
     * If an association to the given key is already existant a
     * SymbolAlreadyRegisteredException needs to be thrown.
     */
    public SymbolTableRef<T> createReference ( String key, T target )
    throws SymbolAlreadyRegisteredException;

    /**
     * Update an already existing reference or create a new one assigned to a
     * certain target.
     *
     * The updated reference entry needs to be returned.
     */
    public SymbolTableRef<T> updateOrCreateReference( String key, T target );

    /**
     * Return a list of all objects referenced by this SymbolTable
     * implementation.
     *
     * This list containts the referenced objects of type T itself. It does not
     * contain SymbolTableRef objects referencing these objects any more.
     */
    public LinkedHashSet<T> getAllReferencedObjects();

    /**
     * Return a list of all SymbolTableRefs in this SymbolTable
     * implementation.
     *
     * This list containts the SymbolTableRefs of type T itself.
     */
//    public LinkedList<SymbolTableRef<T>> getAllReferences();

    /**
     * Returns true if at least one reference with the specified key is
     * registered in the SymbolTable. Return false otherwise.
     */
    public boolean hasReference(String key);

    /**
     * Checks every SymbolTableRef if a reference is assigned.
     *
     * This methods is used to check the consistency of the SymbolTable. Each
     * hosted SymbolTableRef is checked. If no valid reference is assigned to
     * any of them, a SymbolNotResolvableException is thrown. This is mainly
     * used to check the consistency after flattening a schema.
     */
    public void checkReferences();

    public LinkedList<SymbolTableRef<T>> getReferences();

    public void setReference(String key, SymbolTableRef<T> symbolTableRef);

    public void removeReference(String key);
    
    public Set<String> getAllKeys();
 
}
