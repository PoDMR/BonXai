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

package eu.fox7.schematoolkit.xsd.parser.exceptions.attribute.countingpattern;

/**
 * This exception will be thrown, when a minOccurs-value is greater than a
 * maxOccurs-value. This is not allowed per definition.
 * Example: minOccurs="5" maxOccurs="2"
 *
 * minOccurs > maxOccurs
 *
 * @author Lars Schmidt, Dominik Wolff
 */
public class CountingPatternMinOccursGreaterThanMaxOccursException extends eu.fox7.schematoolkit.xsd.parser.exceptions.XSDParseException {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public CountingPatternMinOccursGreaterThanMaxOccursException(String caller) {
        super("The value of minOccurs is greater than (>) the value of maxOccurs in the following CountingPattern: " + caller);
    }
}
