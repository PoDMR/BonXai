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
 * This special SymbolTable is capable of storing multiplie SymbolTableRef
 * objects assigned to one key.  This functionallity is needed to allow
 * tracking and mass updating SymbolTableRefs from multiple Schemas.
 *
 * @see SymbolTableRef
 */
public class MultiReferenceSymbolTable<T> implements SymbolTableFoundation<T> {

    /**
     * Hash map containing all reference objects
     */
    protected HashMap<String, LinkedList<SymbolTableRef<T>>> refTable;

    /**
     * Create a new empty symbol table
     */
    public MultiReferenceSymbolTable() {
        this.refTable = new HashMap<String, LinkedList<SymbolTableRef<T>>>();
    }

    /**
     * Create a new MultiReferenceSymbolTable based on a provided SymbolTable.
     *
     * This allows the easy "evolution" of normal SymbolTable objects into a
     * state where they are capable of handling multiple references per key.
     */
    public MultiReferenceSymbolTable(SymbolTable<T> origin) {
        this.refTable = new HashMap<String, LinkedList<SymbolTableRef<T>>>();
        for (SymbolTableRef<T> reference : origin.getReferences()) {
            LinkedList<SymbolTableRef<T>> list = new LinkedList<SymbolTableRef<T>>();
            list.add(reference);
            this.refTable.put(reference.getKey(), list);
        }
    }

    /**
     * Get symbol table reference for given key
     *
     * If the provided key is registered the first associated reference will be
     * returned.
     *
     * If the provided key is not registered an empty (aka. referencing null)
     * reference will be created stored and returned.
     */
    public SymbolTableRef<T> getReference(String key) {
        if (!this.refTable.containsKey(key)) {
            // The key is not stored in the refTable. Therefore a new empty
            // reference will be created and linked to the key.
            LinkedList<SymbolTableRef<T>> list = new LinkedList<SymbolTableRef<T>>();
            list.add(new SymbolTableRef<T>(key));
            this.refTable.put(key, list);
        }

        return this.refTable.get(key).peek();
    }

    /**
     * Create a new symbol table reference
     *
     * If the key is already registered an SymbolAlreadyRegisteredException is
     * thrown.
     */
    public SymbolTableRef<T> createReference(String key, T target)
            throws SymbolAlreadyRegisteredException {
        if (this.refTable.containsKey(key)) {
            throw new SymbolAlreadyRegisteredException(key);
        }

        LinkedList<SymbolTableRef<T>> list = new LinkedList<SymbolTableRef<T>>();
        list.add(new SymbolTableRef<T>(key, target));
        this.refTable.put(key, list);

        return this.refTable.get(key).peek();
    }

    /**
     * Update an already existing reference or create a new one assigned to a
     * certain value.
     */
    public SymbolTableRef<T> updateOrCreateReference(String key, T target) {
        if (this.refTable.containsKey(key)) {
            // Update the already existing references
            for (SymbolTableRef<T> ref : this.refTable.get(key)) {
                ref.setReference(target);
            }
        } else {
            // Create a new reference for the provided key, value pair.
            try {
                this.createReference(key, target);
            } catch (SymbolAlreadyRegisteredException e) {
                // Can never occur since we already checked that the symbol is
                // not registered. Just encapsulated to not declare throws.
            }
        }

        return this.refTable.get(key).peek();
    }

    /**
     * Return a list of all objects referenced by the stored SymbolTableRefs
     */
    public LinkedHashSet<T> getAllReferencedObjects() {
        LinkedHashSet<T> set = new LinkedHashSet<T>();
        for (LinkedList<SymbolTableRef<T>> refList : this.refTable.values()) {
            set.add(refList.peek().getReference());
        }
        return set;
    }

    /**
     * Returns if at lease one reference with the given key exists.
     */
    public boolean hasReference(String key) {
        return refTable.containsKey(key);
    }

    /**
     * Add an already created SymbolTableRef to this SymbolTable and register
     * it.
     *
     * If corresponding references are already registered the provided one will
     * be added to the list and all already registered references will be
     * updated with the value of the newly provided one.
     */
    public void addReference(SymbolTableRef<T> reference) {
        if (this.refTable.containsKey(reference.getKey())) {
            // Update all already registered references with the new value.
            this.updateOrCreateReference(reference.getKey(), reference.getReference());
            // Add the new reference to the list
            this.refTable.get(reference.getKey()).add(reference);
        } else {
            LinkedList<SymbolTableRef<T>> list = new LinkedList<SymbolTableRef<T>>();
            list.add(reference);
            this.refTable.put(reference.getKey(), list);
        }
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
        for (LinkedList<SymbolTableRef<T>> refList : this.refTable.values()) {
            for (SymbolTableRef<T> ref : refList) {
                if (ref.getReference() == null) {
                    throw new SymbolNotResolvableException(ref.getKey());
                }
            }
        }
    }

    public String toString() {
        return "MultiReferenceSymbolTable{ " + this.refTable + " }";
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    /**
     * Return a LinkedList of all reference lists stored in the table at the
     * time of calling.
     */
    public LinkedList<LinkedList<SymbolTableRef<T>>> getAllReferences() {
        LinkedList<LinkedList<SymbolTableRef<T>>> list = new LinkedList<LinkedList<SymbolTableRef<T>>>(
                this.refTable.values());
        return list;
    }

    @Override
    public LinkedList<SymbolTableRef<T>> getReferences() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setReference(String key, SymbolTableRef<T> symbolTableRef) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void removeReference(String key) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

	@Override
	public Set<String> getAllKeys() {
		return this.refTable.keySet();
	}


}

