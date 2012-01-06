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
package eu.fox7.schematoolkit.bonxai.om;

import java.util.List;

import eu.fox7.schematoolkit.common.QualifiedName;

/**
 * Class representing key constraints in Bonxai
 */
public class KeyConstraint extends Constraint {

    /**
     * Name of the key constraint
     */
    protected QualifiedName name;

    /**
     * Create KeyConstraint from required properties.
     */
    public KeyConstraint(QualifiedName name, AncestorPattern ancestorPattern, AncestorPattern constraintSelector, List<AncestorPattern> constraintFields) {
        this.name               = name;
        this.ancestorPattern    = ancestorPattern;
        this.constraintSelector = constraintSelector;
        this.constraintFields   = constraintFields;
    }

    /**
     * Get name of KeyConstraint.
     *
     * @return name
     */
    public QualifiedName getName() {
        return this.name;
    }
}

