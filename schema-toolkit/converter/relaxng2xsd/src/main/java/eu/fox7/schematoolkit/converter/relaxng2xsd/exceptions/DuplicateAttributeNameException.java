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

package eu.fox7.schematoolkit.converter.relaxng2xsd.exceptions;

/**
 * There are duplicate attribute-names
 * @author Lars Schmidt
 */
public class DuplicateAttributeNameException extends Exception {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public DuplicateAttributeNameException(String attributeName, String elementName) {
        super("There are duplicate attribute-names: \"" + attributeName + "\" under the following element: \"" + elementName + "\"");
    }

}
