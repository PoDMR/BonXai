package de.tudortmund.cs.bonxai.relaxng;

/**
 * Class representing the param element of RelaxNG
 * @author Lars Schmidt
 */
public class Param {

    private String name;    // NCName
    private String content; // string

    /**
     * Constructor of class Param
     * @param name 
     */
    public Param(String name) {
        this.name = name;
    }

    /**
     * Getter for the name of this parameter
     * @return String   name of this parameter
     */
    public String getName() {
        return name;
    }

    /**
     * Setter for the name of this parameter
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Getter for the content of this parameter
     * @return String   content of this parameter
     */
    public String getContent() {
        return content;
    }

    /**
     * Setter for the content of this parameter
     * @param content
     */
    public void setContent(String content) {
        this.content = content;
    }
}
