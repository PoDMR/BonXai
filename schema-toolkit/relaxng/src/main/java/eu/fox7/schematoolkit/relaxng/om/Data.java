package eu.fox7.schematoolkit.relaxng.om;

import java.util.LinkedList;

/**
 * Class representing the data element of RelaxNG
 * @author Lars Schmidt
 */
public class Data extends Pattern {

    private String type;                    // NCName
    private LinkedList<Param> parameters;
    /**
     * In the Simple XML Syntax of RELAX NG there is only one optional pattern
     * allowed in data element. This is called "exceptPattern".
     * The Full XML Syntax makes no restrictions on the quantity of patterns
     * defined as exceptPattern in a data element.
     */
    private LinkedList<Pattern> exceptPatterns;

    /**
     * Constructor of class Data
     * @param type
     */
    public Data(String type) {
        this.type = type;
        this.parameters = new LinkedList<Param>();
        this.exceptPatterns = new LinkedList<Pattern>();
    }

    /**
     * Getter for the name of this data element
     * @return String   name of this data element
     */
    public String getType() {
        return type;
    }

    /**
     * Getter for the contained patterns in this data element
     * @return LinkedList<Param>   
     */
    public LinkedList<Param> getParams() {
        return new LinkedList<Param>(this.parameters);
    }

    /**
     * Method for adding a pattern to this data element
     * @param param 
     */
    public void addParam(Param param) {
        this.parameters.add(param);
    }

    /**
     * Getter for the contained ExceptPatterns in this data element
     * @return LinkedList<Pattern>
     */
    public LinkedList<Pattern> getExceptPatterns() {
        return new LinkedList<Pattern>(exceptPatterns);
    }

    /**
     * Method for adding a pattern to this data element
     * @param pattern
     */
    public void addExceptPattern(Pattern pattern) {
        this.exceptPatterns.add(pattern);
    }

    /**
     * Setter for the exceptPattern list
     * @param exceptPatterns
     */
    public void setExceptPatterns(LinkedList<Pattern> exceptPatterns) {
        this.exceptPatterns = exceptPatterns;
    }


}
