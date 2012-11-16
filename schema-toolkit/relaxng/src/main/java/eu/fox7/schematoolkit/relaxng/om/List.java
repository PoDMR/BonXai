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
 * Class representing the list element of RelaxNG.
 * The list pattern matches a whitespace-separated sequence of tokens; it
 * contains a pattern that the sequence of individual tokens must match. The
 * list pattern splits a string into a list of strings, and then matches the
 * resulting list of strings against the pattern inside the list pattern.
 * 
 * @author Lars Schmidt
 */
public class List extends Pattern {

    /**
     * In the Simple XML Syntax of RELAX NG there is only one pattern
     * allowed in a list element.
     * In the Full XML Syntax there has to be at least one pattern defined in a
     * list, but there is no upper bound.
     */
    private LinkedList<Pattern> patterns;

    /**
     * Constructor of class List
     */
    public List() {
        this.patterns = new LinkedList<Pattern>();
    }

    /**
     * Getter for the contained patterns in this list element
     * @return LinkedList<Pattern>
     */
    public LinkedList<Pattern> getPatterns() {
        return new LinkedList<Pattern>(patterns);
    }

    /**
     * Method for adding a pattern to this list element
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
