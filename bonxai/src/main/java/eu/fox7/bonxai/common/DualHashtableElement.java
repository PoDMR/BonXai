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

/**
 * Element of a DualHashtable
 */
public interface DualHashtableElement {

    /**
     * uri of the element
     */
    String uri = "";
    /**
     * Identifier of the element
     */
    String identifier = "";

    /**
     * Returns uri of element
     * @return uri
     */
    public String getUri();

    /**
     * Returns identifier of element
     * @return identifier
     */
    public String getIdentifier();

    /**
     * Returns a string representation of the element, for string comparison.
     */
    @Override
    public String toString();
}
