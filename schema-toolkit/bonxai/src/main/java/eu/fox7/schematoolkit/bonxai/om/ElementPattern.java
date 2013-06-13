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

import eu.fox7.schematoolkit.common.Particle;

/**
 * Class for representing the element pattern in Bonxai
 */
public class ElementPattern {

    /**
     * Type of this element
     */
    private BonxaiType bonxaiType;
    /**
     * Default value for the element.
     */
    protected String defaultValue;
    /**
     * Fixed value for the element.
     */
    protected String fixedValue;
    /**
     * Regular Expression of this element pattern
     */
    private eu.fox7.schematoolkit.common.Particle regexp;

    /**
     * Constructor for the class ElementPattern.
     * Parameterised with an bonxai-type it defines a single element.
     * @param type
     */
    public ElementPattern(BonxaiType type) {
        this.bonxaiType = type;
    }

    /**
     * Constructor for the class ElementPattern.
     * Parameterised with a regular expression it defines an element pattern.
     * @param regexp
     */
    public ElementPattern(eu.fox7.schematoolkit.common.Particle regexp) {
        this.regexp = regexp;
    }

    /**
     * Returns the type of this element
     * @return
     */
    public BonxaiType getBonxaiType() {
        return bonxaiType;
    }

    /**
     * Returns the regular expression of this element pattern
     * @return
     */
    public eu.fox7.schematoolkit.common.Particle getRegexp() {
        return regexp;
    }

    /**
     * Set child pattern regexp.
     *
     * This is required for replacing cloned particles because of group
     * replacing. See ParticleGroupRefReplacer for details.
     *
     * @param regexp
     */
    public void setRegexp(Particle regexp) {
        this.regexp = regexp;
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

}

