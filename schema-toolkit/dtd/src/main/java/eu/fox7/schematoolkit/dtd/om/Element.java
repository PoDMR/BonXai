/**
 * Copyright 2009-2012 TU Dortmund
 *
 * This file is part of FoXLib.
 *
 * FoXLib is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * FoXLib is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with BonXai.  If not, see <http://www.gnu.org/licenses/>.
 */

package eu.fox7.schematoolkit.dtd.om;

import eu.fox7.schematoolkit.common.Particle;
import eu.fox7.schematoolkit.common.QualifiedName;

import java.util.LinkedList;

/**
 * Class for a DTD element declaration
 * DTD attributes will be attached to the private "attributes" list
 * @author Lars Schmidt
 */
public class Element extends eu.fox7.schematoolkit.common.Element {

    /**
     * Representation of the content model structure of the current Element
     */
    private Particle particle = null;
    /**
     * List of attached attributes of the current element.
     */
    private LinkedList<Attribute> attributes = null;
    /**
     * "mixed" is true, if the element has a mixed content (#PCDATA) or (#PCDATA|..|..)*
     */
    private boolean mixed = false;
    /**
     * "mixedStar" is true, if the element content model has excactly the form: (#PCDATA)*
     */
    private boolean mixedStar = false;

    /**
     * Constructor of class Element.
     * This class is a respresentation of the DTD element specified by
     * the "<!ELEMENT ... !>" tag.
     * @param name
     */
    public Element(QualifiedName name) {
        super();
        this.name = name;
        attributes = new LinkedList<Attribute>();
    }

    /**
     * Getter for the list of the attributes of this element
     * @return attributes -  LinkedList<Attribute>
     */
    public LinkedList<Attribute> getAttributes() {
        return new LinkedList<Attribute>(attributes);
    }

    /**
     * Method for adding an attribute to this element
     * @param attribute
     */
    public void addAttribute(Attribute attribute) {
        this.attributes.add(attribute);
    }

    /**
     * Method isEmpty. If the particle of this element is null, then this method
     * returns the boolean value true.
     * @return boolean
     */
    public boolean isEmpty() {
        return this.particle == null;
    }

    /**
     * Method for checking if the particle of this element is of type
     * eu.fox7.schematoolkit.common.AnyPattern
     * @return boolean
     */
    public boolean hasAnyType() {
        return this.particle != null && this.particle instanceof eu.fox7.schematoolkit.common.AnyPattern;
    }

    /**
     * Setter for the boolean attribute mixed. This is set to true if the
     * content of the current element has a mixed content.
     * (#PCDATA is allowed in the contentModel)
     * @param mixedValue
     */
    public void setMixed(boolean mixedValue) {
        this.mixed = mixedValue;
        if (mixedValue == false) {
            this.mixedStar = false;
        }
    }

    /**
     * Getter for the boolean attribute mixed. Determine if the current element
     * has a mixed content.
     * @return
     */
    public boolean getMixed() {
        return this.mixed;
    }

    /**
     * Setter for the boolean attribute mixedStar. This is set to true if the
     * content of the current element has a mixed content and the particle is
     * empty, but the PCDATA can occur zero or more times.
     * ((#PCDATA)* is allowed in the contentModel)
     * @param mixedValue
     */
    public void setMixedStar(boolean mixedValue) {
        this.mixed = mixedValue;
        this.mixedStar = mixedValue;
    }

    /**
     * Getter for the boolean attribute mixedStar. Determine if the current element
     * has a mixed content of the form (#PCDATA)*.
     * @return
     */
    public boolean getMixedStar() {
        return this.mixed && mixedStar;
    }

    /**
     * Method for adding a particle to the current element
     * @param particle
     */
    public void setParticle(Particle particle) {
        this.particle = particle;
    }

    /**
     * Getter for the particle of the current element
     * @return particle - Particle
     */
    public Particle getParticle() {
        return this.particle;
    }
}
