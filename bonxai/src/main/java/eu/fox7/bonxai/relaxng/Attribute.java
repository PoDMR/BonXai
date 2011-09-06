package eu.fox7.bonxai.relaxng;

/**
 * Class representing the <attribute/> element of RelaxNG
 * @author Lars Schmidt
 */
public class Attribute extends Pattern {

    private String name; // QName, this is an alternative to the nameClass content element
    private NameClass nameClass;
    // In the Full XML Syntax of Relax NG the pattern is optional in an attribute
    private Pattern pattern;

    /**
     * Getter for the pattern of this attribute.
     * In the Full XML Syntax of RELAX NG this is an optional content of an
     * attribute.
     * @return Pattern
     */
    public Pattern getPattern() {
        return pattern;
    }

    /**
     * Setter for the pattern of this attribute.
     * In the Full XML Syntax of RELAX NG this is an optional content of an
     * attribute.
     * @param pattern
     */
    public void setPattern(Pattern pattern) {
        this.pattern = pattern;
    }

    /**
     * Getter of the nameclass attribute of this attribute
     * @return NameClass
     */
    public NameClass getNameClass() {
        return this.nameClass;
    }

    /**
     * Setter for the nameclass attribute of this attribute
     * @param nameClass
     */
    public void setNameClass(NameClass nameClass) {
        this.nameClass = nameClass;
    }

    /**
     * Getter for the name attribute of this attribute
     * @return NameClass
     */
    public String getNameAttribute() {
        return this.name;
    }

    /**
     * Setter for the name attribute of this attribute
     * @param name
     */
    public void setNameAttribute(String name) {
        this.name = name;
    }

}
