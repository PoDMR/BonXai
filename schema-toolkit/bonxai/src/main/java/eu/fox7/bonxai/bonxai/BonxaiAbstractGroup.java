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

import eu.fox7.bonxai.common.QualifiedName;

/**
 * Element which represents a Group
 */
public abstract class BonxaiAbstractGroup {
    /**
     * Name of the element
     */
    private QualifiedName name;

    /**
     * Constructor of the class BonxaiAbstractGroup
     * @param name2
     */
    public BonxaiAbstractGroup(QualifiedName name) {
        this.name = name;
    }

    /**
     * Returns the name of the element
     * @return name
     */
    public QualifiedName getName() {
        return name;
    }
}

