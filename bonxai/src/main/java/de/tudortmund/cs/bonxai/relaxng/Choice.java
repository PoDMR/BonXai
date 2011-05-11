package de.tudortmund.cs.bonxai.relaxng;

import java.util.LinkedList;

/**
 * Class representing the Choice element of RelaxNG.
 * @author Lars Schmidt
 */
public class Choice extends Pattern {

    /**
     * In the Simple XML Syntax of RELAX NG there are only two patterns
     * allowed in a Choice element. Only one pattern is allowed to be empty
     * in this case.
     * In the Full XML Syntax there has to be at least one pattern defined in a
     * Choice element, but there is no upper bound.
     */
    private LinkedList<Pattern> patterns;

    /**
     * Constructor of class Choice
     */
    public Choice() {
        this.patterns = new LinkedList<Pattern>();
    }

    /**
     * Getter for the contained patterns in this Choice element
     * @return LinkedList<Pattern>
     */
    public LinkedList<Pattern> getPatterns() {
        return new LinkedList<Pattern>(patterns);
    }

    /**
     * Method for adding a pattern to this Choice element
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
