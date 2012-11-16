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

package eu.fox7.schematoolkit.dtd.common.exceptions;

/**
 * Attribute Default value is not allowed in the given enumeration
 * @author Lars Schmidt
 */
public class AttributeEnumerationTypeIllegalDefaultValueException extends DTDException {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public AttributeEnumerationTypeIllegalDefaultValueException(String name, String typeString, String value) {
        super("An attribute default value is not allowed in the given enumeration: Attribute:" + "\"" + name + "\"" + " Type: \"" + typeString + "\"" + " default: \"" + value + "\"");
    }

}
