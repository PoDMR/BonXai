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
 * Class representing the <parentRef name="NCName"/> element of RelaxNG
 * @author Lars Schmidt
 */
public class ParentRef extends AbstractRef {
    /**
     * Constructor of class ParentRef
     * @param definedPattern
     * @param grammar
     */
    public ParentRef(String refName, Grammar grammar) {
        super(refName, grammar);
    }

    /**
     * Getter for the pattern of the defined and referenced pattern
     * @return <LinkedList<Define>>
     */
    @Override
    public List<Define> getDefineList() {
        return this.grammar.getParentGrammar().getDefinedPattern(refName);
    }

    /**
     * Getter for the unique ID of the current parentRef
     * @return String   The unique ID of the current parentRef
     */
    @Override
    public String getUniqueRefID() {
        return this.getRefName() + "_" + Integer.toHexString(System.identityHashCode(this.grammar.getParentGrammar()));
    }

}
