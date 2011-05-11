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
/**
 * Symbol table reference.
 *
 * Template base class for symbol tables references.
 *
 * @see SymbolTable
 */
public class SymbolTableRef<T> {

    /**
     * Container for the referenced item
     */
    protected T reference;

    /**
     * Key of the referenced item
     */
    protected String key;

    /**
     * Create a new empty symbol table reference just from the name
     */
    public SymbolTableRef (String key) {
        this.key       = key;
        this.reference = null;
    }

    /**
     * Create a new empty symbol table reference its name and the references
     * item
     */
    public SymbolTableRef (String key, T reference) {
        this.key       = key;
        this.reference = reference;
    }

    /**
     * Get the key of the symbol table reference
     */
    public String getKey () {
        return key;
    }

    /**
     * Get the dereferenced contained item
     */
    public T getReference () {
        return reference;
    }

    /**
     * Set the referenced item in the symbol table reference
     *
     * This method should only be called by the SymbolTable class.
     * If you want to update the reference of a SymbolTable association use the
     * methods provided by SymbolTable instead.
     */
    public void setReference (T value) {
        this.reference = value;
    }

    public void setKey(String key) {
        this.key = key;
    }


    /**
     * Compare the object with that object.
     *
     * This is a specialized implementation of equals(), which only checks the
     * key of the {@link SymbolTableRef} and their generic type. This is
     * sensible since {@link SymbolTableRef}s are intended to by identified
     * uniqly by their name.
     */
    @Override
    public boolean equals( Object that ) {
        if (that == null) {
            return false;
        }
        if (this.getClass() != that.getClass()) {
            return false;
        }
        if (!(that instanceof SymbolTableRef<?>)) {
            return false;
        }

        SymbolTableRef<?> otherRef = (SymbolTableRef<?>)that;

        return (
            this.key.equals(otherRef.key)
        );
    }

    /**
     * Return a hash code for this object.
     *
     * This is a special implementation of hashCode() to identify
     * SymbolTableRefs. SymbolTableRefs are not distinguished by their
     * contents, but only by their name, since every SymbolTableRef in a
     * SymbolTable must have a unique name! This implementation ensures that 2
     * SymbolTableRefs with the same name are identified to be the same in
     * HashTable and similar.
     */
    @Override
    public int hashCode() {
        return 23 * 13 + this.key.hashCode();
    }

    public String toString() {
        return "SymbolTableRef{" + this.key + " => " + this.reference + "}";
    }
}

