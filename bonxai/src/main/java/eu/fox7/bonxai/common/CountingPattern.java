/**
 * This file is part of BonXai.
 *
 * BonXai is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * BonXai is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with BonXai.  If not, see <http://www.gnu.org/licenses/>.
 */
package eu.fox7.bonxai.common;

import java.util.LinkedList;

/*
 * implements class CountingPattern
 */

/**
 * Counting pattern.
 *
 * Represents minOccurs/maxOccurs values from XSD.
 */
public class CountingPattern extends ParticleContainer {

    /**
     * Value of minOccurs.
     */
    private Integer min = 1;

    /**
     * Value of maxOccurs, null if "unbounded".
     */
    private Integer max = 1;

    /**
     * Creates a new CountingPattern with min = minOccurs, max = maxOccurs.
     *
     * Use null for maxOccurs, if the XSD value is "unbounded";
     */
    public CountingPattern (Integer min, Integer max) {
        this.min = min;
        this.max = max;
    }

    public CountingPattern(Particle childParticle, Integer min, Integer max) {
    	this.particles = new LinkedList<Particle>();
    	this.particles.add(childParticle);
		this.min = min;
		this.max = max;
	}

	/*
     * Method getMax return the maximal count of an element.
     */
    public Integer getMax () {
        return this.max;
    }

    /*
     * Method getMin returns the minimal count of an element.
     */
    public Integer getMin () {
        return this.min;
    }

    public void setMax(Integer max) {
        this.max = max;
    }

    public void setMin(Integer min) {
        this.min = min;
    }

    /**
     * Compare two objects of this type to check if they represent the same
     * content
     */
    public boolean equals( CountingPattern that ) {
        return (
            super.equals( that )
            && this.min.equals( that.min )
            && this.max.equals( that.max )
        );
    }

    /**
     * Return a hashCode for this object
     *
     * This needs to be overwritten to fullfill the hashCode/equals contract
     * enforced by java
     */
    public int hashCode() {
        int hash       = super.hashCode();
        int multiplier = 13;
        hash = hash * multiplier + this.min.hashCode();

        // max is explicitely declared with null as a possible value.
        if ( this.max != null )
        {
            hash = hash * multiplier + this.max.hashCode();
        }
        return hash;
    }

}

