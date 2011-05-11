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
package de.tudortmund.cs.bonxai.xsd;

/**
 * Klass representing the <keyRef> constraint from XSD.
 *
 * Note the overridden implementations of hashCode() and equals() in the {@link
 * SimpleConstraint} base class!
 *
 * @TODO Missing docs.
 */
public class KeyRef extends SimpleConstraint {

    protected de.tudortmund.cs.bonxai.common.SymbolTableRef<SimpleConstraint> keyRef;

    public KeyRef (String name, String selector, de.tudortmund.cs.bonxai.common.SymbolTableRef<SimpleConstraint> keyRef) {
         super(name, selector);
         this.keyRef = keyRef;
    }

    /*
     * Method getKeyOrUnique returns a copy of the key, dereferenced by
     * Method getReference from class SymbleTableRef.
     */
    public SimpleConstraint getKeyOrUnique () {
        return keyRef.getReference();
    }
}
