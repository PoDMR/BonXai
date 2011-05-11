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

import de.tudortmund.cs.bonxai.common.AnnotationElement;
import de.tudortmund.cs.bonxai.common.SymbolTableRef;

/**
 * The whole XSD inheritance model is represented by
 * assigning an appropriate Inheritance object to the
 * defined type, which inherits from another one. Every XSD
 * inheritance has to define a base type. The reference to
 * the base type is stored in the Inheritance base class.
 */

public class Inheritance extends AnnotationElement{
    protected de.tudortmund.cs.bonxai.common.SymbolTableRef<Type> base;

    /**
     * Default constructor for inheritances which do have more than one base.
     */
    public Inheritance() {

    }

    /**
     * This constructor creates an instance of inheritance
     * with the passed symboltable reference base.
     *
     * @param base common.SymbolTableRef<Type>
     */
    public Inheritance (SymbolTableRef<Type> base) {
        this.base = base;
    }


    /**
     * Returns the symboltable reference
     * @return common.SymbolTableRef
     *
     */
    public Type getBase () {
        if (base != null) {
            return base.getReference();
        } else {
            return null;
        }
    }
}
