package eu.fox7.bonxai.relaxng;

/**
 * Class representing the Value element of RelaxNG
 * @author Lars Schmidt
 */
public class Value extends Pattern {

    private String content;                  // string
    private String type;                     // NCName

    /**
     * Constructor of class Data
     * @param content
     */
    public Value(String content) {
        this.content = content;
    }

    /**
     * Getter for the name of this Value
     * @return String   name of this Value
     */
    public String getType() {
        return type;
    }

    /**
     * Setter for the name of this Value
     * @param type 
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Getter for the content of this Value
     * @return String   content of this Value
     */
    public String getContent() {
        return content;
    }

    /**
     * Setter for the content of this Value
     * @param content
     */
    public void setContent(String content) {
        this.content = content;
    }
}
