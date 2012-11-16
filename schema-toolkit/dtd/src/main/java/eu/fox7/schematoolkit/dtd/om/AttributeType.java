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
 * An enumeration of possible values for an DTD attribute type.
 * @author Lars Schmidt
 */
public enum AttributeType {

    // A unique ID or name. Must be a valid XML name.
    ID,
    // Represents the value of an ID attribute of another element.
    IDREF,
    // Represents multiple IDs of elements, separated by whitespace.
    IDREFS,
    // The name of an entity (which must be declared in the DTD)
    ENTITY,
    // A list of entity names, separated by whitespaces. (All entities must be declared in the DTD)
    ENTITIES,
    // A valid XML name.
    NMTOKEN,
    // A list of valid XML names separated by whitespaces.
    NMTOKENS,
    // Character Data (text that doesn't contain markup)
    CDATA,
    // A list of notation names (which must be declared in the DTD) seperated by the pipe operator (x|y).
    NOTATION,
    // A list of values seperated by the pipe operator (x|y). The value of the attribute must be one from this list.
    ENUMERATION;
}
