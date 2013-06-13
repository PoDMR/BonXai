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
 * Class representing types in Bonxai
 */
public class BonxaiType {
	/**
     * Type name (XSD simple type).
     */
    private QualifiedName typename;

    /**
     * Default value for the element.
     */
    private String defaultValue;

    /**
     * Fixed value for the element.
     */
    private String fixedValue;
    
    private boolean nillable=false;

    /**
     * Constructor for the class BonxaiType which sets namespace and name
     */
    public BonxaiType(QualifiedName typename) {
        this.typename = typename;
    }

    /**
	 * @return the defaultValue
	 */
	public String getDefaultValue() {
		return defaultValue;
	}

	/**
	 * @param defaultValue the defaultValue to set
	 */
	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	/**
	 * @return the fixedValue
	 */
	public String getFixedValue() {
		return fixedValue;
	}

	/**
	 * @param fixedValue the fixedValue to set
	 */
	public void setFixedValue(String fixedValue) {
		this.fixedValue = fixedValue;
	}

    /**
     * Returns the name of this type
     * @return type
     */
    public QualifiedName getTypename() {
        return typename;
    }

	/**
	 * @return the nillable
	 */
	public boolean isNillable() {
		return nillable;
	}

	/**
	 * @param nillable the nillable to set
	 */
	public void setNillable(boolean nillable) {
		this.nillable = nillable;
	}
    
    
}

