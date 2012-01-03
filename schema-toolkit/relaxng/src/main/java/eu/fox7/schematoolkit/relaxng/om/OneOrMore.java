package eu.fox7.schematoolkit.relaxng.om;

import java.util.LinkedList;

/**
 * Class representing the OneOrMore element of RelaxNG.
 * @author Lars Schmidt
 */
public class OneOrMore extends Pattern {

    /**
     * In the Simple XML Syntax of RELAX NG there is only one pattern
     * allowed in an OneOrMore element. The pattern is not allowed to be empty
     * in this case.
     * In the Full XML Syntax there has to be at least one pattern defined in an
     * OneOrMore element, but there is no upper bound.
     */
    private LinkedList<Pattern> patterns;

    /**
     * Constructor of class OneOrMore
     */
    public OneOrMore() {
        this.patterns = new LinkedList<Pattern>();
    }

    /**
     * Getter for the contained patterns in this OneOrMore element
     * @return LinkedList<Pattern>
     */
    public LinkedList<Pattern> getPatterns() {
        return new LinkedList<Pattern>(patterns);
    }

    /**
     * Method for adding a pattern to this OneOrMore element
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
