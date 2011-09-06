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
package eu.fox7.bonxai.common;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Symbol tables are used as a container for sets of similar objects created
 * during the parsing process.
 *
 * A specific object of this class handles all named items of the former named
 * classes. References to these items are encapsulated by SymbolTableRef
 * objects, which are created and hold in the symbol table. Referencing items
 * just store the SymbolTableRef to the item they are referencing. If they are
 * requested to return a certain referenced item, they need to perform the
 * de-referencing through SymbolTableRef.
 *
 * @param <T>
 * @see SymbolTableRef
 */
public class SymbolTable<T> implements SymbolTableFoundation<T> {
    /**
     * Hash map containing all reference objects
     */
    protected HashMap<String,SymbolTableRef<T>> refTable;

    /**
     * Create a new empty symbol table
     */
    public SymbolTable () {
        this.refTable = new HashMap<String,SymbolTableRef<T>>();
    }

    /**
     * Get symbol table reference for given key
     *
     * If the provided key is registered the associated reference will be
     * returned.
     *
     * If the provided key is not registered an empty (aka. referencing null)
     * reference will be created stored and returned.
     */
    public SymbolTableRef<T> getReference ( String key ) {
        if (!this.refTable.containsKey(key))
        {
            // The key is not stored in the refTable. Therefore a new empty
            // reference will be created and linked to the key.
            this.refTable.put(
                key,
                new SymbolTableRef<T>( key )
            );
        }

        return this.refTable.get( key );
    }

    /**
     * Setter for a SymbolTableRef
     *
     * @param key
     * @param symbolTableRef
     */
    public void setReference(String key, SymbolTableRef<T> symbolTableRef) {
        this.refTable.put(key, symbolTableRef);
    }

    /**
     * Removes a SymbolTableRef from SymbolTable
     *
     * @param key
     */
    public void removeReference(String key) {
        this.refTable.remove(key);
    }

    /**
     * Create a new symbol table reference
     *
     * If the key is already registered an SymbolAlreadyRegisteredException is
     * thrown.
     */
    public SymbolTableRef<T> createReference ( String key, T target )
    throws SymbolAlreadyRegisteredException {
        if ( this.refTable.containsKey( key ) ) {
            throw new SymbolAlreadyRegisteredException( key );
        }

        this.refTable.put(
            key,
            new SymbolTableRef<T>( key, target )
        );

        return this.refTable.get( key );
    }

    /**
     * Update an already existing reference or create a new one assigned to a
     * certain value.
     */
    public SymbolTableRef<T> updateOrCreateReference( String key, T target ) {
        if ( this.refTable.containsKey( key ) ) {
            // Update the already existing references
            this.refTable.get( key ).setReference( target );
        }
        else {
            // Create a new reference for the provided key, value pair.
            try {
                this.createReference( key, target );
            }
            catch ( SymbolAlreadyRegisteredException e ) {
                // Can never occur since we already checked that the symbol is
                // not registered. Just encapsulated to not declare throws.
            }
        }

        return this.refTable.get( key );
    }

    /**
     * Return a list of all objects referenced by the stored SymbolTableRefs
     */
    public LinkedHashSet<T> getAllReferencedObjects() {
        LinkedHashSet<T> set = new LinkedHashSet<T>();
        for( SymbolTableRef<T> ref: this.refTable.values() ) {

            if(ref.getReference() != null){
                set.add( ref.getReference() );
            }
        }
        return set;
    }

    /**
     * Returns if a reference with the given key exists.
     */
    public boolean hasReference(String key) {
        return refTable.containsKey(key);
    }

    /**
     * Return a LinkedList of all references stored in the table at the time of
     * calling.
     * @return LinkedList<SymbolTableRef<T>>
     */
    @Override
    public LinkedList<SymbolTableRef<T>> getReferences() {
        LinkedList<SymbolTableRef<T>> list = new LinkedList<SymbolTableRef<T>>(
            this.refTable.values()
        );

        return list;
    }

    /**
     * Checks every SymbolTableRef if a reference is assigned.
     *
     * This methods is used to check the consistency of the SymbolTable. Each
     * hosted SymbolTableRef is checked. If no valid reference is assigned to
     * any of them, a SymbolNotResolvableException is thrown. This is mainly
     * used to check the consistency after flattening a schema.
     */
    public void checkReferences() {
        for (SymbolTableRef<T> ref : this.refTable.values()) {
            if (ref.getReference() == null) {
                throw new SymbolNotResolvableException(ref.getKey());
            }
        }
    }

    public String toString() {
    	return "SymbolTable{ " + this.refTable + " }";
   
    }

	@Override
	public Set<String> getAllKeys() {
		return this.refTable.keySet();
	}


}

