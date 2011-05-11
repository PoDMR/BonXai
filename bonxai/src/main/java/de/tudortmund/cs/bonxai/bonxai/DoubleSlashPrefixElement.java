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

/**
 * Element representing the double slash at the beginning of a pattern
 */
public class DoubleSlashPrefixElement extends AncestorPatternElement {

    /**
     * Constructor for class DoubleSlashPrefixElement
     * @param namespace
     * @param name
     */
    public DoubleSlashPrefixElement(String namespace, String name) {
        super(namespace, name);
    }

    @Override
    public String toString() {
        return "//" + getNamespace() + ":" + getName();
    }
}