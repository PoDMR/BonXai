package eu.fox7.schematoolkit.relaxng.om;

import java.util.LinkedList;

/**
 * Class representing the optional element of RelaxNG.
 * @author Lars Schmidt
 */
public class Optional extends Pattern {

    /**
     * In the Full XML Syntax there has to be at least one pattern defined in an
     * optional element, but there is no upper bound.
     */
    private LinkedList<Pattern> patterns;

    /**
     * Constructor of class Optional
     */
    public Optional() {
        this.patterns = new LinkedList<Pattern>();
    }

    /**
     * Getter for the contained patterns in this optional element
     * @return LinkedList<Pattern>
     */
    public LinkedList<Pattern> getPatterns() {
        return new LinkedList<Pattern>(patterns);
    }

    /**
     * Method for adding a pattern to this optional element
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
