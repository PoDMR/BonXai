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

import eu.fox7.schematoolkit.common.QualifiedName;

/**
 * Abstract class for a prefix element implemented either as SingleSlashPrefixElement or as DoubleSlashPrefixElement
 */
public class AncestorPatternElement extends AncestorPattern {

    /**
     * Name of the element
     */
    private QualifiedName name;

    /**
     * Constructor for the class Element
     * @param name
     */
    public AncestorPatternElement(QualifiedName name) {
    	this.name = name;
	}

    /**
     * Returns name of the element
     * @return name
     */
    public QualifiedName getName() {
        return name;
    }
}

