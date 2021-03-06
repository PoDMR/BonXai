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
/*
 * implements class Constraint
 */

public class Constraint {
    // Attribute "id" represents the ID attribute type from [XML 1.0 (Second Edition)].
    private String id;
    
    public Constraint() {
    }
    
    public Constraint(Constraint other) {
    	this.id = other.id;
    }
    
    /**
     * Getter for the attribute id
     * @return
     */
    public String getId() {
        return id;
    }
    /**
     * Setter for the attribute id
     * @param id
     */
    public void setId(String id) {
        this.id = id;
    }
}
