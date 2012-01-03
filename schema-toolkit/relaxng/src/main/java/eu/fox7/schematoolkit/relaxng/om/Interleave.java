package eu.fox7.schematoolkit.relaxng.om;

import java.util.LinkedList;

/**
 * Class representing the Interleave element of RelaxNG.
 * @author Lars Schmidt
 */
public class Interleave extends Pattern {

    /**
     * In the Simple XML Syntax of RELAX NG there are only two patterns
     * allowed in an Interleave element. All the patterns aren't allowed to be empty
     * in this case.
     * In the Full XML Syntax there has to be at least one pattern defined in an
     * Interleave element, but there is no upper bound.
     */
    private LinkedList<Pattern> patterns;

    /**
     * Constructor of class Interleave
     */
    public Interleave() {
        this.patterns = new LinkedList<Pattern>();
    }

    /**
     * Getter for the contained patterns in this Interleave element
     * @return LinkedList<Pattern>
     */
    public LinkedList<Pattern> getPatterns() {
        return new LinkedList<Pattern>(patterns);
    }

    /**
     * Method for adding a pattern to this Interleave element
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
