package de.tudortmund.cs.bonxai.relaxng;

import java.util.LinkedList;

/**
 * Class representing the ZeroOrMore element of RelaxNG.
 * @author Lars Schmidt
 */
public class ZeroOrMore extends Pattern {

    /**
     * In the Full XML Syntax there has to be at least one pattern defined in an
     * ZeroOrMore element, but there is no upper bound.
     */
    private LinkedList<Pattern> patterns;

    /**
     * Constructor of class zeroOrMore
     */
    public ZeroOrMore() {
        this.patterns = new LinkedList<Pattern>();
    }

    /**
     * Getter for the contained patterns in this zeroOrMore element
     * @return LinkedList<Pattern>
     */
    public LinkedList<Pattern> getPatterns() {
        return new LinkedList<Pattern>(patterns);
    }

    /**
     * Method for adding a pattern to this zeroOrMore element
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
