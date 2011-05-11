package de.tudortmund.cs.bonxai.relaxng;

/**
 * Class representing the Choice element of RelaxNG.
 * @author Lars Schmidt
 */
public class ExternalRef extends Pattern {

    private String href;                        // anyURI

    /**
     * Object holding the parsed external RELAX NG XSDSchema, if not null.
     */
    private RelaxNGSchema rngSchema;

    /**
     * Constructor of class Choice
     * @param href
     */
    public ExternalRef(String href) {
        this.href = href;
    }

    /**
     * Getter of the XML attribute href.
     * @return String   A string containing the value of the datatypeLibrary
     */
    public String getHref() {
        return this.href;
    }

    /**
     * Setter of the href attribute of this include element.
     * @param href 
     */
    public void setHref(String href) {
        this.href = href;
    }

    /**
     * Getter for the parsed Relax NG XSDSchema, which builds the target of this externalRef.
     * @return RelaxNGSchema        the parsed Relax NG XSDSchema
     */
    public RelaxNGSchema getRngSchema() {
        return rngSchema;
    }

    /**
     * Setter for the parsed Relax NG XSDSchema, which builds the target of this externalRef.
     * @param rngSchema     the parsed Relax NG XSDSchema
     */
    public void setRngSchema(RelaxNGSchema rngSchema) {
        this.rngSchema = rngSchema;
    }
}
