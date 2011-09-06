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
package eu.fox7.bonxai.converter.xsd2bonxai;

/**
 * Listener interface for listeners to be attached to {@link
 * ParticleProcessor}.
 */
interface ParticleProcessListener {
    /**
     * Notify on each processed Particle.
     *
     * This method is called for every Particle converted by the {@link
     * ParticleProcessor}. The method implementation must decide itself on
     * which particle to react in which way. For example an implementation
     * looking for {@link Constraint}s should only react on {@link
     * eu.fox7.bonxai.xsd.Element}.
     */
    public void notify(eu.fox7.bonxai.common.Particle particle);
}
