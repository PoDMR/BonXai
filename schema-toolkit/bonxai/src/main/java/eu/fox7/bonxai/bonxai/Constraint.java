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
package eu.fox7.bonxai.bonxai;

import java.util.LinkedList;
import java.util.List;

/**
 * Class representing Constraints in Bonxai
 */
abstract public class Constraint {

    /**
     * The ancestor-pattern of this constraint
     */
    protected AncestorPattern ancestorPattern;

    /**
     * The constraint selector of this constraint
     */
    protected AncestorPattern constraintSelector;

    /**
     * The constraint fields of this constraint
     */
    protected List<AncestorPattern> constraintFields;

    /**
     * Returns the ancestor-pattern of this constraint
     *
     * @return ancestorPattern
     */
    public AncestorPattern getAncestorPattern() {
        return ancestorPattern;
    }

    /**
     * Returns the constraint selector of this constraint
     *
     * @return constraintSelector
     */
    public AncestorPattern getConstraintSelector() {
        return constraintSelector;
    }

    /**
     * Returns the constraint fields of this constraint
     *
     * @return constraintFields
     */
    public List<AncestorPattern> getConstraintFields() {
        return new LinkedList<AncestorPattern>(constraintFields);
    }
}

