package eu.fox7.bonxai.dtd;

/**
 * Class for an external entity defined in a DTD
 *
 * Important:
 * This is not necessary and therefor not supported in the current
 * implementation. All external entities are fetched by the SAXParser itself
 * and their content will be placed within the generated dtd structure.
 *
 * @author Lars Schmidt
 */
public class ExternalEntity extends Entity {

    private String publicID, systemID;

    /**
     * Constructor of class ExternalEntity
     * @param name - String
     * @param publicID - String
     * @param systemID - String
     */
    public ExternalEntity(String name, String publicID, String systemID) {
        super(name);
        this.publicID = publicID;

        // Check "%" --> parsed entity

        // SystemID is currently the absolute path to the file.
        // This might be a problem.
        this.systemID = systemID;
    }

    /**
     * Getter for the publicID of the Notation
     * @return
     */
    public String getPublicID() {
        return this.publicID;
    }

    /**
     * Getter for the systemID of the Notation
     * @return
     */
    public String getSystemID() {
        return this.systemID;
    }


}
