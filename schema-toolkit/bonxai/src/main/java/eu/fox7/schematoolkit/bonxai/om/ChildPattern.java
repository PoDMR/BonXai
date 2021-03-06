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

/**
 * Class representing the child-string in Bonxai
 */
public class ChildPattern {
	/**
     * Representing the pattern-part of the child-string consisting of Attributes
     */
    private AttributePattern attributePattern;
    /**
     * Representing the pattern-part of the child-string consisting of Elements
     */
    private ElementPattern elementPattern;

    /**
     * Can the content be mixed?
     */
    private boolean mixed;
    
	private boolean nillable;
    
    /**
     * Constructor for the class ChildPattern
     * @param attributePattern
     * @param elementPattern
     */
    public ChildPattern(AttributePattern attributePattern, ElementPattern elementPattern, boolean mixed, boolean nillable) {
        this.attributePattern = attributePattern;
        this.elementPattern = elementPattern;
        this.mixed=mixed;
        this.setNillable(nillable);
    }

    /**
     * Returns the AttributePattern of this ChildPattern
     * @return attributePattern
     */
    public AttributePattern getAttributePattern() {
        return attributePattern;
    }

    /**
     * Returns the ElementPattern of this ChildPattern
     * @return elementPattern
     */
    public ElementPattern getElementPattern() {
        return elementPattern;
    }

    /**
	 * @return mixed
	 */
	public boolean isMixed() {
		return mixed;
	}

	/**
	 * @param mixed
	 */
	public void setMixed(boolean mixed) {
		this.mixed = mixed;
	}

	/**
	 * @param nillable the nillable to set
	 */
	public void setNillable(boolean nillable) {
		this.nillable = nillable;
	}

	/**
	 * @return the nillable
	 */
	public boolean isNillable() {
		return nillable;
	}

}

