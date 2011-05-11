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
package de.tudortmund.cs.bonxai.bonxai;

import java.util.Vector;

/**
 * List of all constrains
 */
public class ConstraintList {

    /**
     * List of constraints
     */
    private Vector<Constraint> constraints = new Vector<Constraint>();

    /**
     * Returns the list of constraints
     * @return constraints
     */
    public Vector<Constraint> getConstraints() {
        return constraints;
    }

    /**
     * Adds a constraint to the list of constraints
     * @param constraint
     */
    public void addConstraint(Constraint constraint) {
        this.constraints.add(constraint);
    }
}

