package eu.fox7.schematoolkit.relaxng.om;

import eu.fox7.schematoolkit.common.AnonymousNamespace;

/**
 * Class representing the Name element of RelaxNG
 * @author Lars Schmidt
 */
public class Name extends NameClass {

    private String content;             // NCName

    /**
     * Constructor of class Name
     * The content of a name is of type "QName".
     * @param content
     */
    public Name(String content) {
        super();
        this.content = content;
    }

    /**
     * Constructor of class Name with content and namespace
     * @param content
     * @param attributeNamespace 
     */
    public Name(String content, String attributeNamespace) {
        super();
        this.content = content;
        this.attributeNamespace = new AnonymousNamespace(attributeNamespace);
    }

    /**
     * Getter for the content of this name
     * @return String   A string containing the string content
     */
    public String getContent() {
        return content;
    }
    /**
     * Setter for the content of this name
     * @param content
     */
    public void setContent(String content) {
        this.content = content;
    }
}
