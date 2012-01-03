package eu.fox7.schematoolkit.relaxng.om;

import java.util.LinkedList;

/**
 * Class representing the <element/> element of RelaxNG
 * @author Lars Schmidt
 */
public class Element extends Pattern {

    /**
     * Name of the element: QName, this is an alternative to the nameClass content element
     */
    private String name;
    /**
     * NameClass of the element
     */
    private NameClass nameClass;
    /**
     * In the Simple XML Syntax of RELAX NG there is only one pattern
     * allowed in an element element. This can also be the notAllowed or empty
     * pattern.
     * In the Full XML Syntax there has to be at least one pattern defined in an
     * element element, but there is no upper bound.
     */
    private LinkedList<Pattern> patterns;

    /**
     * Constructor of class Element
     */
    public Element() {
        this.patterns = new LinkedList<Pattern>();
    }

    /**
     * Getter for the nameclass attribute of this element
     * @return NameClass
     */
    public NameClass getNameClass() {
        return this.nameClass;
    }

    /**
     * Setter for the nameclass attribute of this element
     * @param nameClass 
     */
    public void setNameClass(NameClass nameClass) {
        this.nameClass = nameClass;
    }

    /**
     * Getter for the contained patterns in this element
     * @return LinkedList<Pattern>
     */
    public LinkedList<Pattern> getPatterns() {
        return new LinkedList<Pattern>(patterns);
    }

    /**
     * Method for adding a pattern to this element
     * @param pattern
     */
    public void addPattern(Pattern pattern) {
        this.patterns.add(pattern);
    }

    /**
     * Getter for the name attribute of this element
     * @return NameClass    NameClass as Name for the element
     */
    public String getNameAttribute() {
        return this.name;
    }

    /**
     * Setter for the name attribute of this element
     * @param name      String for the name
     */
    public void setNameAttribute(String name) {
        this.name = name;
    }
    
    /**
     * Setter for the patterns of this element
     * @param patterns  Patterns contained in this element
     */
    public void setPatterns(LinkedList<Pattern> patterns) {
        this.patterns = patterns;
    }
}
