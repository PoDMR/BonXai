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

package eu.fox7.schematoolkit.bonxai.om;

import eu.fox7.schematoolkit.common.QualifiedName;

/**
 * Bonxai Element realization.
 */
public class Element extends eu.fox7.schematoolkit.common.Element implements Locatable {
    /**
     * The Type of this element.
     */
    protected BonxaiType type;
    
    /**
     * Location in the schema
     */
    private BonxaiLocation location;

    /**
     * The Name of this element
     */
    protected QualifiedName name;
    
    /**
     * Creates an element with namespace and name.
     */
    public Element (QualifiedName name) {
        this.name      = name;
    }

    /**
     * Creates an element with namespace and name.
     */
    public Element (QualifiedName name, BonxaiLocation location) {
        this.name      = name;
        this.location = location;
    }

    /**
     * Creates an element with namespace, name and type.
     */
    public Element (QualifiedName name, BonxaiType type) {
    	this(name);
        this.type = type;
    }


    /**
     * Returns the type of the element.
     */
    public BonxaiType getType() {
        return type;
    }

	public void setType(eu.fox7.schematoolkit.bonxai.om.BonxaiType type) {
		this.type=type;
	}
	
	public QualifiedName getName() {
		return name;
	}

	@Override
	public BonxaiLocation getLocation() {
		return location;
	}
}
