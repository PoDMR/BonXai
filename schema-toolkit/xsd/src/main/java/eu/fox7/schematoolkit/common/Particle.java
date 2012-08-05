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
package eu.fox7.schematoolkit.common;

import java.util.Collection;
import java.util.LinkedList;

/*
 * implements class Particle
 */

public abstract class Particle extends AnnotationElement{
    public Particle () {
    }
    
    /**
     * Return particle string representation for debugging
     */
    @Override
    public String toString()
    {
        return this.getClass().getName();
    }
    
	public static Collection<Particle> getAllElementlikeParticles(Particle particle) {
		Collection<Particle> particles = new LinkedList<Particle>();
		if ((particle instanceof Element) || (particle instanceof ElementRef))
			particles.add(particle);
		else if (particle instanceof ParticleContainer)
			for (Particle child: ((ParticleContainer) particle).getParticles())
				particles.addAll(getAllElementlikeParticles(child));
		else if (particle instanceof CountingPattern)
			particles.addAll(getAllElementlikeParticles(((CountingPattern) particle).getParticle()));
		
		return particles;
	}
}

