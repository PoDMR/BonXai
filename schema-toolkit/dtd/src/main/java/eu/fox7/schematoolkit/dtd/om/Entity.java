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
 * Abstract base class for DTD entities specified by
 * the "<!ENTITY ... !>" tag.
 *
 * There are two types:
 * internalEntities and
 * externalEntities
 *
 * @author Lars Schmidt
 */
public abstract class Entity {

    /**
     * Attribute holding the name of an entity
     * @param name
     */
    private String name;

    /**
     * Constructor of class Entity
     * @param name - String
     */
    public Entity(String name) {
        this.name = name;
    }

    /**
     * Getter for the name attribute of the current entity
     * @return name - String
     */
    public String getName() {
        return name;
    }
}
