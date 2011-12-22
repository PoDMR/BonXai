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

import eu.fox7.bonxai.common.Annotation;
import eu.fox7.bonxai.common.AttributeParticle;
import eu.fox7.bonxai.common.QualifiedName;

/**
 * The AttributeRef class stores a common.SymbolTableRef object to represent a
 * reference to an attribute.
 */
public class AttributeRef extends AttributeParticle {

    /**
     * Reference to the attribute in the symbol table.
     */
    protected QualifiedName attributeName;

    /**
     * Use flag.
     */
    protected AttributeUse use = AttributeUse.Optional;

    /**
     * Default value.
     */
    String defaultValue;

    /**
     * Fixed value.
     */
    String fixedValue;
    

    public String getDefault() {
        return defaultValue;
    }

    public String getFixed() {
        return fixedValue;
    }

    public AttributeUse getUse() {
        return use;
    }

    public void setDefault(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public void setFixed(String fixedValue) {
        this.fixedValue = fixedValue;
    }

    public void setUse(AttributeUse use) {
        this.use = use;
    }

    public AttributeRef(QualifiedName attributeName, String defaultValue, String fixedValue, AttributeUse use, Annotation annotation) {
        this.attributeName = attributeName;
        this.defaultValue = defaultValue;
        this.fixedValue = fixedValue;
        this.use = use;
        this.setAnnotation(annotation);
    }

    /**
     * Creates an AttributeRef object with the passed symbol table reference.
     */
    public AttributeRef (QualifiedName attributeName) {
        this.attributeName = attributeName;
    }

    /**
     * Returns the dereferenced attribute.
     */
    public QualifiedName getAttributeName () {
        return attributeName;
    }
}
