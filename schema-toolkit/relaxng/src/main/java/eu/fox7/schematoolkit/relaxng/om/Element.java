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

package eu.fox7.schematoolkit.relaxng.om;

import java.util.LinkedList;

/**
 * Class representing the <element/> element of RelaxNG
 * @author Lars Schmidt
 */
public class Element extends Pattern {

    /**
     * Name of the element: QName, this is an alternative to the nameClass content element
     */
    private String name;
    /**
     * NameClass of the element
     */
    private NameClass nameClass;
    /**
     * In the Simple XML Syntax of RELAX NG there is only one pattern
     * allowed in an element element. This can also be the notAllowed or empty
     * pattern.
     * In the Full XML Syntax there has to be at least one pattern defined in an
     * element element, but there is no upper bound.
     */
    private LinkedList<Pattern> patterns;

    /**
     * Constructor of class Element
     */
    public Element() {
        this.patterns = new LinkedList<Pattern>();
    }

    /**
     * Getter for the nameclass attribute of this element
     * @return NameClass
     */
    public NameClass getNameClass() {
        return this.nameClass;
    }

    /**
     * Setter for the nameclass attribute of this element
     * @param nameClass 
     */
    public void setNameClass(NameClass nameClass) {
        this.nameClass = nameClass;
    }

    /**
     * Getter for the contained patterns in this element
     * @return LinkedList<Pattern>
     */
    public LinkedList<Pattern> getPatterns() {
        return new LinkedList<Pattern>(patterns);
    }

    /**
     * Method for adding a pattern to this element
     * @param pattern
     */
    public void addPattern(Pattern pattern) {
        this.patterns.add(pattern);
    }

    /**
     * Getter for the name attribute of this element
     * @return NameClass    NameClass as Name for the element
     */
    public String getNameAttribute() {
        return this.name;
    }

    /**
     * Setter for the name attribute of this element
     * @param name      String for the name
     */
    public void setNameAttribute(String name) {
        this.name = name;
    }
    
    /**
     * Setter for the patterns of this element
     * @param patterns  Patterns contained in this element
     */
    public void setPatterns(LinkedList<Pattern> patterns) {
        this.patterns = patterns;
    }
}
