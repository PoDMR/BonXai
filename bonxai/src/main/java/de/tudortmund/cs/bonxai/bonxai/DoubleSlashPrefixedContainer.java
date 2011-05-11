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
 * Class for representing a container that is prefixed with a double slash in Bonxai.
 * It contains just a single AncestorPatternParticle.
 */
public class DoubleSlashPrefixedContainer extends AncestorPatternParticle {

    /**
     * Child, that is prefixed with an double-slash in Bonxai-file.
     */
    protected AncestorPatternParticle child;

    /**
     * Constructor for the class DoubleSlashPrefixedContainer
     * @param children
     */
    public DoubleSlashPrefixedContainer(AncestorPatternParticle child) {
        this.child = child;

    }
    
    public AncestorPatternParticle getChild() {
    	return child;
    }

    @Override
    public String toString() {
        /**
         * @TODO: Is the generated String ok?
         */
        return child.toString();
    }
}