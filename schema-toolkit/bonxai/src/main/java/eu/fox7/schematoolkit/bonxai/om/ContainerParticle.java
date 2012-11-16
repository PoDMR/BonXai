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

package eu.fox7.schematoolkit.bonxai.om;

import java.util.LinkedList;
import java.util.List;

/**
 * Container for AncestorPatternParticles
 */
public abstract class ContainerParticle extends AncestorPattern {

    /**
     * List of AncestorPatternParticles called children
     */
    private List<AncestorPattern> children;

    /**
     * Constructor for the class ContainerParticle
     * @param children
     */
    public ContainerParticle(List<AncestorPattern> children) {
        this.children = children;
    }

    public ContainerParticle() {
		this.children = new LinkedList<AncestorPattern>();
	}
    
    public void addChild(AncestorPattern child) {
    	this.children.add(child);
    }

	/**
     * Returns the list of AncestorPatternParticles for this ContainerParticle
     * @return children
     */
    public List<AncestorPattern> getChildren() {
        return children;
    }
}

