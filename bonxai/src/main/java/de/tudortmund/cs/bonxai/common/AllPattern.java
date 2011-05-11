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
package de.tudortmund.cs.bonxai.common;
/*
 * implements class AllPattern
 */

/*
 * @TODO
 * import java.util.LinkedList; only necessary if method getParticles need to be
 * implemented
 *
 * @TODO This class now receives any Particle as its children. It must be
 *   verified in the XSD parser, that only certain particles (Element) are stored
 *   in an AllPattern.
 */


public class AllPattern extends ParticleContainer {

    /*
     * If this LinkedList is of type Particle, it is already implemented in
     * superclass.
     * protected LinkedList<Particle> particles;
     */

    public AllPattern () {
    }
}

