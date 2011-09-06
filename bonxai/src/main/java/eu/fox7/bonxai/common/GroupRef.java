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
/*
 * implements class GroupRef
 */

public class GroupRef extends Particle {

    protected SymbolTableRef<? extends eu.fox7.bonxai.common.Group> groupRef;

    public GroupRef (SymbolTableRef<? extends eu.fox7.bonxai.common.Group> groupRef) {
        this.groupRef = groupRef;
    }

    /*
     * Method getGroup returns the Group, dereferenced by
     * Method getReference from class SymbleTableRef.
     */
    public eu.fox7.bonxai.common.Group getGroup () {
        return groupRef.getReference();
    }

    /**
     * Compare two objects of this type to check if they represent the same
     * content
     */
    public boolean equals( GroupRef that ) {
        return (
            super.equals( that )
            && this.groupRef.equals( that.groupRef )
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
        hash = hash * multiplier + this.groupRef.hashCode();
        return hash;
    }

    /**
     * Setter for the groupRef
     * @param groupRef
     */
    public void setGroupRef(SymbolTableRef<? extends Group> groupRef) {
        this.groupRef = groupRef;
    }


}
