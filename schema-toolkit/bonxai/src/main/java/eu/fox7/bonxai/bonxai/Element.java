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
package eu.fox7.bonxai.bonxai;

import eu.fox7.bonxai.common.Particle;
import eu.fox7.bonxai.common.QualifiedName;

/**
 * Bonxai Element realization.
 */
public class Element extends Particle{
    /**
     * The Type of this element.
     */
    protected BonxaiType type;

    /**
     * The Name of this element
     */
    protected QualifiedName name;
    
    /**
     * True if the element might be missing (nillable in XSD).
     */
    protected boolean missing = false;

    /**
     * Creates an element with namespace and name.
     */
    public Element (QualifiedName name) {
        this.name      = name;
    }

    /**
     * Creates an element with namespace, name and type.
     */
    public Element (QualifiedName name, BonxaiType type) {
    	this(name);
        this.type = type;
    }

    /**
     * Creates an element with namespace, name, type and missing flag.
     */
    public Element (QualifiedName name, BonxaiType type, boolean missing) {
        this(name, type);
        this.missing = missing;
    }

    /**
     * Creates an element with namespace, name and missing flag.
     */
    public Element (QualifiedName name, boolean missing) {
        this(name);
        this.missing = missing;
    }

    /**
     * Returns the type of the element.
     */
    public BonxaiType getType() {
        return type;
    }

    /**
     * If the element might be left empty (nillable in XSD).
     */
    public boolean isMissing() {
        return missing;
    }

	public void setType(eu.fox7.bonxai.bonxai.BonxaiType type, boolean missing) {
		this.type=type;
		this.missing=missing;
	}
	
	public QualifiedName getName() {
		return name;
	}
}
