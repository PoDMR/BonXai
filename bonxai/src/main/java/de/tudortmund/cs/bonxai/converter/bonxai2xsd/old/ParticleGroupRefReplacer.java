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
package de.tudortmund.cs.bonxai.converter.bonxai2xsd.old;

import de.tudortmund.cs.bonxai.common.*;

/**
 * Class to clone a particle and replace group references in the cloned particle.
 *
 * This is required to reflect group modifications / extraction in the particles
 * of the element referencing the group.
 */
public class ParticleGroupRefReplacer {

    /**
     *
     */
    public ParticleGroupRefReplacer() {
    }

    /**
     * Replace groups references in particle
     *
     * @param regexp
     * @param groupRef
     * @param groupRefToGroup
     */
    Particle replaceGroupRefInParticle(Particle particle, GroupRef groupRef, GroupRef groupRefToGroup) {

        if (particle instanceof GroupRef) {

            if (((GroupRef) particle).getGroup() != null &&
                 groupRef.getGroup() != null &&
                ((GroupRef) particle).getGroup().getName().equals(groupRef.getGroup().getName())) {
                return groupRefToGroup;
            } else {
                return particle;
            }

        } else if (particle instanceof de.tudortmund.cs.bonxai.common.ParticleContainer) {

            ParticleContainer container = null;

            if (particle instanceof SequencePattern) {
                container = new SequencePattern();
            } else if (particle instanceof ChoicePattern) {
                container = new ChoicePattern();
            } else if (particle instanceof AllPattern) {
                container = new AllPattern();
            } else if (particle instanceof CountingPattern) {
                container = new CountingPattern(
                    ((CountingPattern) particle).getMin(),
                    ((CountingPattern) particle).getMax()
                );
            }

            /* traverse through the List of particles in this sequence to add
             * the elements to the container
             */
            if (!((ParticleContainer) particle).getParticles().isEmpty()) {
                for (Particle currentParticle : ((ParticleContainer) particle).getParticles()) {
                    container.addParticle(replaceGroupRefInParticle(currentParticle, groupRef, groupRefToGroup));
                }
            }

            return container;

        } else {
            return particle;
        }
    }
}

