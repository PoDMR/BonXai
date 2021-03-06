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
 * Class for representing sequences expressions in Bonxai
 */
public class CardinalityParticle extends AncestorPattern {

    /**
     * Child, the cardinality constraints are applied to.
     */
    protected AncestorPattern child;

    /**
     * Minimum number of occurences
     */
    protected Integer min;

    /**
     * Maximum number of occurences.
     *
     * Might be zero, indicating unlimited maximum number.
     */
    protected Integer max;

    /**
     * Constructor for the class CardinalityParticle
     *
     * @param child
     * @param min
     */
    public CardinalityParticle(AncestorPattern child, Integer min) {
        this.child = child;
        this.min   = min;
        this.max   = null;
    }

    /**
     * Constructor for the class CardinalityParticle
     *
     * @param child
     * @param min
     * @param max
     */
    public CardinalityParticle(AncestorPattern child, Integer min, Integer max) {
        this.child = child;
        this.min   = min;
        this.max   = max;
    }

    /**
     * Return child
     */
    public AncestorPattern getChild()
    {
        return this.child;
    }

    /**
     * Return minimum number of occurences.
     */
    public Integer getMin()
    {
        return this.min;
    }

    /**
     * Return maximum number of occurences.
     */
    public Integer getMax()
    {
        return this.max;
    }

    @Override
    public String toString() {
        return "(" + child + ")[" + min + "," + max + "]";
    }
}

