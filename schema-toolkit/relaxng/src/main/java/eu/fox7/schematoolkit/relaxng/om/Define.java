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
 * Class representing the define element of RelaxNG
 * @author Lars Schmidt
 */
public class Define {

    private String name; // NCName
    /**
     * In the Simply XML Syntax of RELAX NG there is only one pattern allowed under a define XML element.
     * This pattern has to be an element definition.
     *
     * The Full Syntax allows one or more patterns as the content of a define element.
     * The type of these patterns is not restricted.
     */
    private LinkedList<Pattern> patterns;
    /**
     * Holds the value of the enumeration CombineMethod for this define element.
     *
     * It is important, that all elements with the same name share the same
     * combine method.
     */
    private CombineMethod combineMethod;

    /**
     * Constructor of class Define
     *
     * The name has the type "NCName".
     *
     * @param name 
     */
    public Define(String name) {
        this.name = name;
        this.patterns = new LinkedList<Pattern>();
    }

    /**
     * Getter for the name of this definition
     * @return String   name (NCName) of this definition
     */
    public String getName() {
        return name;
    }

    /**
     * Setter for the name (NCName) of this definition
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Getter for the CombineMethod of this definition
     * @return CombineMethod
     */
    public CombineMethod getCombineMethod() {
        return combineMethod;
    }

    /**
     * Setter for the CombineMethod of this definition
     * @param combineMethod 
     */
    public void setCombineMethod(CombineMethod combineMethod) {
        this.combineMethod = combineMethod;
    }

    /**
     * Getter for the contained patterns in this definition
     * @return LinkedList<Pattern>
     */
    public LinkedList<Pattern> getPatterns() {
        return new LinkedList<Pattern>(patterns);
    }

    /**
     * Method for adding a pattern to this definition
     * @param pattern
     */
    public void addPattern(Pattern pattern) {
        this.patterns.add(pattern);
    }

    /**
     * Setter for the patterns
     * @param patterns  patterns
     */
    public void setPatterns(LinkedList<Pattern> patterns) {
        this.patterns = patterns;
    }
}
