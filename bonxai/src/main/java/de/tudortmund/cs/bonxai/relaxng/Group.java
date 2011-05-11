package de.tudortmund.cs.bonxai.relaxng;

import java.util.LinkedList;

/**
 * Class representing the Group element of RelaxNG.
 * @author Lars Schmidt
 */
public class Group extends Pattern {

    /**
     * In the Simple XML Syntax of RELAX NG there are only two patterns
     * allowed in a Group element. All the patterns aren't allowed to be empty
     * in this case.
     * In the Full XML Syntax there has to be at least one pattern defined in a
     * Group element, but there is no upper bound.
     */
    private LinkedList<Pattern> patterns;

    /**
     * Constructor of class Group
     */
    public Group() {
        this.patterns = new LinkedList<Pattern>();
    }

    /**
     * Getter for the contained patterns in this Group element
     * @return LinkedList<Pattern>
     */
    public LinkedList<Pattern> getPatterns() {
        return new LinkedList<Pattern>(patterns);
    }

    /**
     * Method for adding a pattern to this Group element
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
