package de.tudortmund.cs.bonxai.dtd;

import java.util.LinkedHashSet;

/**
 * Class for a DTD attribute (ATTLIST entry)
 * @author Lars Schmidt
 */
public class Attribute {

    private String name;
    private AttributeType type;
    private AttributeDefaultPresence attributeDefaultPresence;
    private String value;
    private LinkedHashSet<String> enumerationOrNotationTokens;

    /**
     * Holds an enumeration of the possible values for defining the attribute default presence
     **/
    public enum AttributeDefaultPresence {

        // REQUIRED: the attribute MUST be specified for all elements of the type in the attribute-list declaration
        REQUIRED,
        // IMPLIED: no default value is provided
        IMPLIED,
        // FIXED:  states that the attribute MUST always have the default value
        FIXED;
    }

    /**
     * Constructor of class Attribute with defaultPresenceMode.
     * This class is a respresentation of the DTD attribute specified by
     * the "<!ATTLIST ... !>" tag.
     *
     * @param name
     * @param value
     */
    public Attribute(String name, String value) {
        this.name = name;
        this.value = value;
        // In this case the value stands for the default value of this attribute
        this.attributeDefaultPresence = null;
        this.enumerationOrNotationTokens = new LinkedHashSet<String>();
    }

    /**
     * Constructor of class Attribute with defaultPresenceMode.
     * This class is a respresentation of the DTD attribute specified by
     * the "<!ATTLIST ... !>" tag.
     *
     * @param name
     * @param defaultPresenceMode
     * @param value
     */
    public Attribute(String name, String defaultPresenceMode, String value) {
        this.name = name;

        if (defaultPresenceMode != null) {
            if (defaultPresenceMode.equals("#REQUIRED")) {
                this.attributeDefaultPresence = AttributeDefaultPresence.REQUIRED;
                this.value = null;
            } else if (defaultPresenceMode.equals("#IMPLIED")) {
                this.attributeDefaultPresence = AttributeDefaultPresence.IMPLIED;
                this.value = null;
            } else if (defaultPresenceMode.equals("#FIXED")) {
                this.attributeDefaultPresence = AttributeDefaultPresence.FIXED;
                this.value = value;
            }
        } else {
            this.value = value;
        }
        this.enumerationOrNotationTokens = new LinkedHashSet<String>();
    }

    /**
     * Setter for the type of the current attribute-object.
     * @param attributeType - AttributeType
     */
    public void setType(AttributeType attributeType) {
        this.type = attributeType;
    }

    /**
     * Getter for the enumerationOrNotationTokens (LinkedHashSet<String>)
     * This is used in case of type
     * AttributeType.ENUMERATION or
     * AttributeType.NOTATION
     * 
     * @return enumerationOrNotationTokens - LinkedHashSet<String>
     */
    public LinkedHashSet<String> getEnumerationOrNotationTokens() {
        return enumerationOrNotationTokens;
    }

    /**
     * Getter for the attributeDefaultPresence. This defines, which default value
     * handling is used by the current attribute.
     * @return attributeDefaultPresence - AttributeDefaultPresence
     */
    public AttributeDefaultPresence getAttributeDefaultPresence() {
        return this.attributeDefaultPresence;
    }

    /**
     * Getter for the name of the attribute.
     * @return name - String
     */
    public String getName() {
        return name;
    }

    /**
     * Getter for the type of the attribute.
     * @return type - AttributeType
     */
    public AttributeType getType() {
        return type;
    }

    /**
     * Getter for the value of the attribute, if there is one.
     * It is not used in all cases of attributeType.
     *
     * @return value - String
     */
    public String getValue() {
        return value;
    }

    /**
     * Setter for the Enumeration or Notation Stringtokens representing the only
     * allowed values of this attribute in case of Type ENUMERATION or NOTATION.
     *
     * @param enumerationOrNotationTokens
     */
    public void setEnumerationOrNotationTokens(LinkedHashSet<String> enumerationOrNotationTokens) {
        this.enumerationOrNotationTokens = enumerationOrNotationTokens;
    }
}
