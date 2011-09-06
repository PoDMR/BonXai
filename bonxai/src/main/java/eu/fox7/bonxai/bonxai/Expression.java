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

import java.util.Vector;

/**
 * Class representing an expression/rule consisting of an annotation, ancestor-pattern and child-pattern
 */
public class Expression {

    /**
     * Annotation of the rule
     */
    private Vector<Annotation> annotations = new Vector<Annotation>();
    /**
     * Ancestor-pattern of the rule
     */
    private AncestorPattern ancestorPattern;
    /**
     * Child-pattern of the rule
     */
    private ChildPattern childPattern;

    /**
     * Returns the ancestor-pattern of this expression
     * @return ancestorPattern
     */
    public AncestorPattern getAncestorPattern() {
        return ancestorPattern;
    }

    /**
     * Sets the ancestor-pattern of this expression
     * @param ancestorPattern
     */
    public void setAncestorPattern(AncestorPattern ancestorPattern) {
        this.ancestorPattern = ancestorPattern;
    }

    /**
     * Returns the list of annotations
     * @return annotations
     */
    public Vector<Annotation> getAnnotations() {
        return annotations;
    }

    /**
     * Adds an annotation to the list of annotations
     * @param annotation
     */
    public void addAnnotation(Annotation annotation) {
        this.annotations.add(annotation);
    }

    /**
     * Returns the child-pattern of this expression
     * @return childPattern
     */
    public ChildPattern getChildPattern() {
        return childPattern;
    }

    /**
     * Sets the child-pattern of this expression
     * @param childPattern
     */
    public void setChildPattern(ChildPattern childPattern) {
        this.childPattern = childPattern;
    }
}