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
package eu.fox7.schematoolkit.bonxai.om;

import eu.fox7.schematoolkit.common.*;

/**
 * Class representing an element group
 */
public class BonxaiGroup extends BonxaiAbstractGroup {

    /**
     * Regular expression describing this element group
     */
    private Particle particle;

    /**
     * Constructor for the class BonxaiGroup
     * @param name
     * @param particle
     */
    public BonxaiGroup(QualifiedName name, Particle particle) {
        super(name);
        this.particle = particle;
    }

    /**
     * Returns the Regular expression of this element group
     * @return
     */
    public Particle getParticle() {
        return this.particle;
    }

    @Override
    public String toString(){
         return this.getClass() + "{name = '" + this.getName() + "', particle = " + this.getParticle() + "}";
    }
}

