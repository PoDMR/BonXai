package de.tudortmund.cs.bonxai.relaxng;

import java.util.LinkedList;

/**
 * Class representing the define element of RelaxNG
 * @author Lars Schmidt
 */
public class Define {

    private String name; // NCName
    /**
     * In the Simply XML Syntax of RELAX NG there is only one pattern allowed under a define XML element.
     * This pattern has to be an element definition.
     *
     * The Full Syntax allows one or more patterns as the content of a define element.
     * The type of these patterns is not restricted.
     */
    private LinkedList<Pattern> patterns;
    /**
     * Holds the value of the enumeration CombineMethod for this define element.
     *
     * It is important, that all elements with the same name share the same
     * combine method.
     */
    private CombineMethod combineMethod;

    /**
     * Constructor of class Define
     *
     * The name has the type "NCName".
     *
     * @param name 
     */
    public Define(String name) {
        this.name = name;
        this.patterns = new LinkedList<Pattern>();
    }

    /**
     * Getter for the name of this definition
     * @return String   name (NCName) of this definition
     */
    public String getName() {
        return name;
    }

    /**
     * Setter for the name (NCName) of this definition
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Getter for the CombineMethod of this definition
     * @return CombineMethod
     */
    public CombineMethod getCombineMethod() {
        return combineMethod;
    }

    /**
     * Setter for the CombineMethod of this definition
     * @param combineMethod 
     */
    public void setCombineMethod(CombineMethod combineMethod) {
        this.combineMethod = combineMethod;
    }

    /**
     * Getter for the contained patterns in this definition
     * @return LinkedList<Pattern>
     */
    public LinkedList<Pattern> getPatterns() {
        return new LinkedList<Pattern>(patterns);
    }

    /**
     * Method for adding a pattern to this definition
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
