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

package eu.fox7.schematoolkit.dtd.om;

/**
 * Class for an internal entity defined in a DTD
 * @author Lars Schmidt
 */
public class InternalEntity extends Entity {

    private String value;

    /**
     * Constructor of class InternalEntity
     * @param name - String
     * @param value - String
     */
    public InternalEntity(String name, String value) {
        super(name);
        this.value = value;
    }

    /**
     * Getter for the value attribute of the current internal entity
     * @return value - String
     */
    public String getValue() {
        return value;
    }

    /**
     * Setter for the value attribute of the current internal entity
     * @param value - String
     */
    public void setValue(String value) {
        this.value = value;
    }
    


}
