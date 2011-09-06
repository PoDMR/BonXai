package eu.fox7.bonxai.relaxng;

import java.util.LinkedList;

/**
 * Class representing the mixed element of RelaxNG.
 *  
 * @author Lars Schmidt
 */
public class Mixed extends Pattern {

    /**
     * In the Full XML Syntax there has to be at least one pattern defined in a
     * mixed element, but there is no upper bound.
     */
    private LinkedList<Pattern> patterns;

    /**
     * Constructor of class Mixed
     */
    public Mixed() {
        this.patterns = new LinkedList<Pattern>();
    }

    /**
     * Getter for the contained patterns in this mixed element
     * @return LinkedList<Pattern>
     */
    public LinkedList<Pattern> getPatterns() {
        return new LinkedList<Pattern>(patterns);
    }

    /**
     * Method for adding a pattern to this mixed element
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
