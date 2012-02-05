package eu.fox7.schematoolkit.dtd.om;

/**
 * Abstract base class for DTD entities specified by
 * the "<!ENTITY ... !>" tag.
 *
 * There are two types:
 * internalEntities and
 * externalEntities
 *
 * @author Lars Schmidt
 */
public abstract class Entity {

    /**
     * Attribute holding the name of an entity
     * @param name
     */
    private String name;

    /**
     * Constructor of class Entity
     * @param name - String
     */
    public Entity(String name) {
        this.name = name;
    }

    /**
     * Getter for the name attribute of the current entity
     * @return name - String
     */
    public String getName() {
        return name;
    }
}
