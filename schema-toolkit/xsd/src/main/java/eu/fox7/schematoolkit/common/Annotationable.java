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
 * This interfaces enforces the implementation of the XSD-annotation-tag in all
 * implementing classes.
 *
 * Annotations can help human readers to understand the structure of a XSD.
 *
 * @author Lars Schmidt, Dominik Wolff
 */
public interface Annotationable {
    /**
     * This is the method-definition for the getter of an annotation.
     * @return Annotation   An annotation attribute has to be hold in the implementing class
     */
    public Annotation getAnnotation() ;
    /**
     * This is the method-definition for the getter of an annotation.
     * @param annotation
     */
    public void setAnnotation(Annotation annotation);
}
