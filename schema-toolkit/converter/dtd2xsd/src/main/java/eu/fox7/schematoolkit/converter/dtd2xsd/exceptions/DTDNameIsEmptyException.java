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

package eu.fox7.schematoolkit.converter.dtd2xsd.exceptions;

import eu.fox7.schematoolkit.exceptions.ConversionFailedException;

/**
 * This exception will be thrown if there is an empty DTD name string
 * @author Lars Schmidt
 */
public class DTDNameIsEmptyException extends ConversionFailedException {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public DTDNameIsEmptyException() {
        super("There is an empty DTD name!");
    }

}