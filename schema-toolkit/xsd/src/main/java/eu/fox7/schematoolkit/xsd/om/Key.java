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

package eu.fox7.schematoolkit.xsd.om;

import eu.fox7.schematoolkit.common.QualifiedName;

/*
 * implements class Key
 */

/**
 *
 * Note the overridden implementations of hashCode() and equals() in the {@link
 * SimpleConstraint} base class!
 *
 * @TODO Incorrect ctor and missing docs.
 */
public class Key extends SimpleConstraint {

	public Key () {}
	
    /**
     * Constructor with name and selector.
     *
     * This constructor should ONLY be used with fully qualified names in the
     * form "{namespace}name".
     * @param name
     * @param selector
     */
    public Key (QualifiedName name, String selector) {
         super(name, selector);
    }
    
    public Key (Key other) {
    	super(other);
    }
}