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
package de.tudortmund.cs.bonxai.bonxai;

/**
 * Class representing ancester-patterns in Bonxai
 */
public class AncestorPattern {

    /**
     * The attribute particle of AncestorPattern contains the ancestor-string
     */
    private AncestorPatternParticle particle;

    /**
     * Constructor for the class AncestorPattern
     * @param particle
     */
    public AncestorPattern(AncestorPatternParticle particle) {
        this.particle = particle;
    }

    /**
     * Returns the attribute particle of this AncestorPattern
     * @return particle
     */
    public AncestorPatternParticle getParticle() {
        return particle;
    }

    @Override
    public String toString() {
        return particle.toString();
    }
}
