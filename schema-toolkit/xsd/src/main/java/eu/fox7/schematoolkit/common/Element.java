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
package eu.fox7.schematoolkit.common;

/**
 * Abstract base class for XSD and Bonxai Element realizations.
 */
abstract public class Element extends Particle {

	protected ElementProperties elementProperties = new ElementProperties();
	
    /**
     * Name of the element.
     *
     * Will be the full qualified name in the format "{namespace}localName"
     * in xsd.Element extension, and the local name in bonxai.Element.
     */
    protected QualifiedName name;
    
    /**
     * Returns the name of the element.
     */
    public QualifiedName getName () {
        return name;
    }

    /**
     * Set default value.
     *
     * Can be null for no default.
     */
    public void setDefault(String defaultValue) {
        this.elementProperties.setDefaultValue(defaultValue);
    }

    /**
     * Get default value.
     *
     * Can be null for no default.
     */
    public String getDefault() {
        return this.elementProperties.getDefaultValue();
    }

    /**
     * Set fixed value.
     *
     * Can be null for no fixed value.
     */
    public void setFixed(String fixedValue) {
        this.elementProperties.setFixedValue(fixedValue);
    }

    /**
     * Get fixed value.
     *
     * Can be null for no fixed value.
     */
    public String getFixed() {
        return this.elementProperties.getFixedValue();
    }

    /**
     * Return element string representation for debugging
     */
    public String toString() {
        return this.getClass().getName() + ": " + this.name;
    }
    
    public void setProperties(ElementProperties elementProperties) {
    	this.elementProperties = elementProperties;
    }
    
    public ElementProperties getProperties() {
    	return this.elementProperties;
    }
}
