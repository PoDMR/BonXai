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
/*
 * implements class ElementRef
 */

import de.tudortmund.cs.bonxai.common.Particle;
import de.tudortmund.cs.bonxai.common.SymbolTableRef;

public class ElementRef extends Particle {

    protected SymbolTableRef<Element> reference;

    public ElementRef (SymbolTableRef<Element> reference) {
        this.reference = reference;
    }

    /*
     * Method getElement returns the element, dereferenced by
     * Method getReference from class SymbleTableRef.
     */

    public de.tudortmund.cs.bonxai.xsd.Element getElement () {
       return reference.getReference();
    }

    /**
     * Compare two objects of this type to check if they represent the same
     * content
     */
    public boolean equals( ElementRef that ) {
        return (
            super.equals( that )
            && this.reference.equals( that.reference )
        );
    }

    /**
     * Return a hashCode for this object
     *
     * This needs to be overwritten to fullfill the hashCode/equals contract
     * enforced by java
     */
    public int hashCode() {
        int hash       = super.hashCode();
        int multiplier = 13;
        hash = hash * multiplier + this.reference.hashCode();
        return hash;
    }

}
