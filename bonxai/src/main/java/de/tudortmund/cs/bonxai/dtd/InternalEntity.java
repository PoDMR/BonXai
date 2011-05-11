package de.tudortmund.cs.bonxai.dtd;

/**
 * Class for an internal entity defined in a DTD
 * @author Lars Schmidt
 */
public class InternalEntity extends Entity {

    private String value;

    /**
     * Constructor of class InternalEntity
     * @param name - String
     * @param value - String
     */
    public InternalEntity(String name, String value) {
        super(name);
        this.value = value;
    }

    /**
     * Getter for the value attribute of the current internal entity
     * @return value - String
     */
    public String getValue() {
        return value;
    }

    /**
     * Setter for the value attribute of the current internal entity
     * @param value - String
     */
    public void setValue(String value) {
        this.value = value;
    }
    


}
