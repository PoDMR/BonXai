/**
 * Copyright 2009-2012 TU Dortmund
 *
 * This file is part of FoXLib.
 *
 * FoXLib is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * FoXLib is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with BonXai.  If not, see <http://www.gnu.org/licenses/>.
 */

package eu.fox7.schematoolkit.relaxng.om;

import java.util.List;

/**
 * Class representing the <ref name="NCName"/> element of RelaxNG
 * @author Lars Schmidt
 */
public class Ref extends AbstractRef {
    /**
     * Constructor of class Ref
     * @param definedPattern        Defined pattern as target of the parentRef
     * @param grammar               Grammar object of the parentRef
     */
    public Ref(String refName, Grammar grammar) {
    	super(refName, grammar);
    }

    /**
     * Getter for the pattern of the defined and referenced pattern
     * @return <LinkedList<Define>>
     */
    public List<Define> getDefineList() {
        return this.grammar.getDefinedPattern(refName);
    }

    /**
     * ToString method of the current class
     * @return String   return the refName
     */
    public String toString() {
        return "Ref (" + this.getRefName() + ")";
    }

    /**
     * Getter for the unique ID of the Ref object
     * @return String       The unique ID of the Ref object
     */
    @Override
    public String getUniqueRefID() {
        return this.getRefName() + "_" + Integer.toHexString(System.identityHashCode(this.grammar));
    }
}
