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

/**
 * Klass representing the <keyRef> constraint from XSD.
 *
 * Note the overridden implementations of hashCode() and equals() in the {@link
 * SimpleConstraint} base class!
 *
 * @TODO Missing docs.
 */
public class KeyRef extends SimpleConstraint {
    private QualifiedName refer;

    public KeyRef() {
    	super();
    }
    
	public KeyRef (QualifiedName name, String selector, QualifiedName refer) {
         super(name, selector);
         this.refer = refer;
    }

	/**
	 * @return the refer
	 */
	public QualifiedName getRefer() {
		return refer;
	}

	public void setRefer(QualifiedName refer) {
		this.refer=refer;
		
	}
}
