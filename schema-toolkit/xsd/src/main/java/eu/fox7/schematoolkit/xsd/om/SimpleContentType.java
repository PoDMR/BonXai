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
package eu.fox7.schematoolkit.xsd.om;

/**
 * Class representing content of SimpleType
 */
public class SimpleContentType extends Content {

    /**
     * Variable for the SimpleContentInheritance description of the SimpleContentType
     */
    private SimpleContentInheritance inheritance;

    /**
     * Gets the inheritance of SimpleContentType
     * @return SimpleContentInheritance
     */
    @Override
    public SimpleContentInheritance getInheritance() {
        return this.inheritance;
    }

    /**
     * Sets the inheritance of SimpleContentType
     * @param inheritance
     */
    public void setInheritance(Inheritance inheritance) {
        this.inheritance = (SimpleContentInheritance) inheritance;
    }
}
