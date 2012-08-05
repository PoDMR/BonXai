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
package eu.fox7.schematoolkit.xsd.om;

import eu.fox7.schematoolkit.common.*;

/*
 * implements class Group
 */
public class Group extends AnnotationElement implements eu.fox7.schematoolkit.common.Group, PContainer {
    public void setName(QualifiedName name) {
        this.name = name;
    }

    public void setParticle(Particle particle) {
        this.particle = particle;
    }

    protected Particle particle;
    /**
     * The name of the group.
     *
     * This name is full qualified in the format "{<namespace>}<localName>",
     * where "<" and ">" are only meant as placeholder indicators and do not
     * belong to the qualified name itself.
     */
    protected QualifiedName name;

    public Group(){}
    
    /**
     * Constructor with name and particle container.
     *
     * This constructor should ONLY be used with fully qualified names in the
     * form "{namespace}name".
     */
    public Group(QualifiedName name, Particle particle) {
        this.name = name;
        this.particle = particle;
    }

    /*
     * Method getContainer returns a copy of the whole ParticleContainer of the
     * group, consisting of particles.
     */
    public Particle getParticle() {
        return particle;
    }

    public void addParticle(Particle particle) {
        this.particle = particle;
    }

    
    /**
     * Returns the name of the group.
     *
     * This name is full qualified in the format "{<namespace>}<localName>",
     * where "<" and ">" are only meant as placeholder indicators and do not
     * belong to the qualified name itself.
     */
    public QualifiedName getName() {
        return name;
    }

    /**
     * Compare the object with that object.
     *
     * This is a specialized implementation of equals(), which only checks the
     * name of the group. This is sensible since groups in XSD must have a
     * unique name there must not exist 2 groups with the same name.
     */
    @Override
    public boolean equals(Object that) {
        return ((that instanceof Group)
                && this.name.equals(((Group) that).name));
    }
}

