package eu.fox7.schematoolkit.dtd.om;

/**
 * Class for a DTD notation
 * @author Lars Schmidt
 */
public abstract class Notation {

    private String name, identifier;

    /**
     * Constructor of class Notation
     * @param name
     * @param identifier 
     */
    public Notation(String name, String identifier) {
        this.name = name;
        this.identifier = identifier;
    }

    /**
     * Getter for the name of the Notation
     * @return
     */
    public String getName() {
        return this.name;
    }

    /**
     * Getter for the identifier of the Notation
     * @return
     */
    public String getIdentifier() {
        return this.identifier;
    }
}
