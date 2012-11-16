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

package eu.fox7.schematoolkit.bonxai.om;

import java.util.List;

import eu.fox7.schematoolkit.common.QualifiedName;

/**
 * Class representing key constraints in Bonxai
 */
public class KeyRefConstraint extends Constraint {

    /**
     * Name of the referenced key constraint
     */
    protected QualifiedName reference;

    /**
     * Create KeyRefConstraint from required properties.
     */
    public KeyRefConstraint(QualifiedName reference, AncestorPattern ancestorPattern, AncestorPattern constraintSelector, List<AncestorPattern> constraintFields) {
        this.reference          = reference;
        this.ancestorPattern    = ancestorPattern;
        this.constraintSelector = constraintSelector;
        this.constraintFields   = constraintFields;
    }

    /**
     * Get name of KeyRefConstraint reference.
     *
     * @return reference
     */
    public QualifiedName getReference() {
        return this.reference;
    }
}

