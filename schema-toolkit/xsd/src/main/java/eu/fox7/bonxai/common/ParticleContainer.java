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
/*
 * implements class ParticleContainer
 */

import java.util.Collection;
import java.util.LinkedList;


public abstract class ParticleContainer extends Particle {

    protected LinkedList<Particle> particles;

    public ParticleContainer () {
        this.particles = new LinkedList<Particle>();
    }

    public ParticleContainer (Collection<Particle> childParticles) {
        this.particles = new LinkedList<Particle>();
       	particles.addAll(childParticles);
    }

    
    /*
     * Method getParticles returns a copy of the LinkedList of particles of
     * the particleContainer.
     */

    public LinkedList<Particle> getParticles () {
        return new LinkedList<Particle>(particles);
    }

    /**
     * Update a single particle in the particle container.
     *
     * @param i
     * @param particle
     */
    public void setParticle(int i, Particle particle) {
        this.particles.set(i, particle);
    }

    /*
     * Method addParticle adds a particle the particleContainer's LinkedList of
     * Particles.
     */

    public void addParticle (Particle particle) {
        particles.add(particle);
    }

    /**
     * Compare two objects of this type to check if they represent the same
     * content
     */
    public boolean equals( ParticleContainer that ) {
        return (
            super.equals( that )
            && this.particles.equals( that.particles )
        );
    }

    /**
     * Return particle container string representation for debugging
     */
    public String toString()
    {
        String returnValue = this.getClass().getName() + "( ";
        for ( Particle particle: this.particles )
        {
            returnValue += particle + ", ";
        }

        return returnValue.substring( 0, returnValue.length() - 2 ) + " )";
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
        hash = hash * multiplier + this.particles.hashCode();
        return hash;
    }

    public void setParticles(LinkedList<Particle> particles) {
        this.particles = particles;
    }

    

}

