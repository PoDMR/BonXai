package de.tudortmund.cs.bonxai.relaxng;

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
