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

import eu.fox7.schematoolkit.common.*;

/**
 * Class representing content of ComplexType
 *
 * Reflects XMLSchema ComplexContentType as well as the pure ComplexType structure where no inheritance is defined at all.
 * This implies that in this only a particle reference is stored.
 */
public class ComplexContentType extends Content implements PContainer {

    /**
     * Variable for the ComplexContentInheritance description of the ComplexContentType
     */
    private ComplexContentInheritance inheritance;
    /**
     * Variable for the particle description of the ComplexContentType
     */
    private eu.fox7.schematoolkit.common.Particle particle;
    /**
     * Variable to determine if the ComplexContent is a mixed content or not
     */
    private boolean mixed = false;

    /**
     * Default constructor.
     *
     * @TODO Should this be removed in favor of ctors that expect a particle
     * always?
     */
    public ComplexContentType() {

    }

    /**
     * Creates a new complex content with the given particle.
     */
    public ComplexContentType(Particle particle) {
        this.particle = particle;
    }

    /**
     * Creates a new complex content with the given particle and mixed flag.
     */
    public ComplexContentType(Particle particle, boolean mixed) {
        this(particle);
        this.mixed = mixed;
    }

    /**
     * Creates a new complex content with the given particle and inheritance.
     */
    public ComplexContentType(Particle particle, ComplexContentInheritance inheritance) {
        this(particle);
        this.inheritance = inheritance;
    }

    /**
     * Creates a new complex content with the given particle, inheritance and mixed flag.
     */
    public ComplexContentType(Particle particle, ComplexContentInheritance inheritance, boolean mixed) {
        this(particle, inheritance);
        this.mixed = mixed;
    }

    /**
     * Gets the inheritance of ComplexContentType
     * @return returnCopy
     */
    public ComplexContentInheritance getInheritance() {
        return this.inheritance;
    }

    /**
     * Sets the inheritance of ComplexContentType
     * @param inheritance
     */
    public void setInheritance(Inheritance inheritance) {
        this.inheritance = (ComplexContentInheritance) inheritance;
    }

    /**
     * Returns the value of mixed
     * @return mixed
     */
    public Boolean getMixed() {
        return this.mixed;
    }

    /**
     * Sets the value of the variable mixed
     * @param mixedContent
     */
    public void setMixed(boolean mixedContent) {
        this.mixed = mixedContent;
    }

    /**
     * Returns a copy of the particle describing this content
     * @return returnCopy
     */
    public eu.fox7.schematoolkit.common.Particle getParticle() {
        return this.particle;
    }

    /**
     * Sets the value of particle
     * @param val
     */
    public void setParticle(eu.fox7.schematoolkit.common.Particle particle) {
        this.particle = particle;
    }

    /**
     * Sets the value of particle
     * @param val
     */
    public void addParticle(eu.fox7.schematoolkit.common.Particle particle) {
    	this.particle = particle;
    }
}
