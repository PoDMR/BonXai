package eu.fox7.schematoolkit.relaxng.om;

import java.util.LinkedList;

public abstract class RelaxNGCountingPattern extends Pattern {
    /**
     * In the Simple XML Syntax of RELAX NG there is only one pattern
     * allowed in an Counting element. The pattern is not allowed to be empty
     * in this case.
     * In the Full XML Syntax there has to be at least one pattern defined in an
     * Counting element, but there is no upper bound.
     */
    private LinkedList<Pattern> patterns = new LinkedList<Pattern>();

    /**
     * Getter for the contained patterns in this Counting element
     * @return LinkedList<Pattern>
     */
    public LinkedList<Pattern> getPatterns() {
        return new LinkedList<Pattern>(patterns);
    }

    /**
     * Method for adding a pattern to this Counting element
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

    public abstract int getMinOccurs();
    public abstract Integer getMaxOccurs();
}
