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

import eu.fox7.bonxai.common.AnnotationElement;
import eu.fox7.bonxai.common.QualifiedName;

/**
 * Abstract Type Class
 */
public abstract class Type extends AnnotationElement {

    protected boolean isAnonymous = false;

    /**
     * The name of the type.
     *
     * This name is full qualified in the format "{<namespace>}<localName>",
     * where "<" and ">" are only meant as placeholder indicators and do not
     * belong to the qualified name itself.
     */
    protected QualifiedName name;

    /**
     * Constructor with namespace and name passed in one string .
     *
     * This constructor should ONLY be used with fully qualified names in the
     * form "{namespace}name".
     * @param name
     */
    public Type(QualifiedName name) {
        this.name = name;
        this.isAnonymous = false;
    }

    /**
     * Returns the name of the type.
     *
     * This name is full qualified in the format "{<namespace>}<localName>",
     * where "<" and ">" are only meant as placeholder indicators and do not
     * belong to the qualified name itself.
     * @return String
     */
    public QualifiedName getName() {
        return this.name;
    }

    /**
     * Replaces the name of the type.
     *
     * This setter should ONLY be used with fully qualified names in the
     * form "{namespace}name".
     * @param name
     */
    public void setName(QualifiedName name) {
        this.name = name;
    }

    /**
     * Compare the object with that object.
     *
     * This is a specialized implementation of equals(), which only checks the
     * name of the type. This is sensible since types in XSD must have a unique
     * name there must not exist 2 types with the same name.
     * @param that
     */
    @Override
    public boolean equals(Object that) {
        return ((that instanceof Type)
                && this.name.equals(((Type) that).name));
    }

    public boolean isAnonymous() {
        return this.isAnonymous;
    }

    public void setIsAnonymous(boolean isAnonymous) {
        this.isAnonymous = isAnonymous;
    }
    
    @Override
    public String toString() {
        return this.name.toString();
    }
}
