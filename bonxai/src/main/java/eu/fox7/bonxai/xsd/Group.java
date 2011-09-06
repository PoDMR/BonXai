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
package eu.fox7.bonxai.xsd;

import eu.fox7.bonxai.common.*;

/*
 * implements class Group
 */
public class Group extends AnnotationElement implements eu.fox7.bonxai.common.Group {

    protected boolean dummy;

    public boolean isDummy() {
        return dummy;
    }

    public void setDummy(boolean dummy) {
        this.dummy = dummy;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setContainer(ParticleContainer container) {
        this.container = container;
    }

    
    

    protected ParticleContainer container;
    /**
     * The name of the group.
     *
     * This name is full qualified in the format "{<namespace>}<localName>",
     * where "<" and ">" are only meant as placeholder indicators and do not
     * belong to the qualified name itself.
     */
    protected String name;

    /**
     * Constructor with name and particle container.
     *
     * This constructor should ONLY be used with fully qualified names in the
     * form "{namespace}name".
     */
    public Group(String name, ParticleContainer container) {
        if ((name.length() < 2)
                || (!name.startsWith("{"))
                || (!name.contains("}"))) {
            throw new RuntimeException("Only fully qualified names are allowed.");
        }
        this.name = name;
        this.container = container;
    }

    /*
     * Method getContainer returns a copy of the whole ParticleContainer of the
     * group, consisting of particles.
     *
     * @TODO Can be removed in favor of {@link getParticle()}.
     */
    public ParticleContainer getParticleContainer() {
        return container;
    }

    /**
     * Returns the name of the group.
     *
     * This name is full qualified in the format "{<namespace>}<localName>",
     * where "<" and ">" are only meant as placeholder indicators and do not
     * belong to the qualified name itself.
     */
    public String getName() {
        return name;
    }

    /**
     * Get namespace.
     *
     * Get namespace URI from stored fully qualified name.
     *
     * @return string
     */
    public String getNamespace() {
        return this.name.substring(1, this.name.lastIndexOf("}"));
    }

    /**
     * Get local name.
     *
     * Get local name from stored fully qualified name.
     *
     * @return string
     */
    public String getLocalName() {
        return this.name.substring(this.name.lastIndexOf("}") + 1);
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

    public void setParticleContainer(ParticleContainer container) {
        this.container = container;
    }
}

