package eu.fox7.bonxai.relaxng;


import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Map;

/**
 * Class representing an include content of Relax NG used in a "grammar"-element
 * @author Lars Schmidt
 */
public class IncludeContent {

    private String href;

    /* Normally the startPatterns is a single pattern, but in the Full XML Syntax
     * it is possible to combine multiple startPatterns with the same name with
     * the method "choice" or "interleaving". For this case the startPatterns
     * must be able to hold a list of this startPatterns.
     *
     * The same behaviour is possible for define-elements.
     */
    private LinkedList<Pattern> startPatterns;
    /**
     * Used for look-up or register a referenced pattern or a paternlist in case
     * of "combine" (same name, same combine method) in this include.
     *
     * When the structure is completely built, all referenced Patterns have to
     * be found in the definedPatternNames list of this include, too! If one
     * name is not specified there, it is an error!
     **/
    private Map<String, LinkedList<Define>> defineLookUpTable;
    /**
     * Object holding the parsed external RELAX NG XSDSchema, if not null.
     */
    private RelaxNGSchema externalRNGSchema;

    /**
     * Constructor of class IncludeContent
     * @param href
     */
    public IncludeContent(String href) {
        this.defineLookUpTable = new HashMap<String,LinkedList<Define>>();
        this.startPatterns = new LinkedList<Pattern>();
        this.href = href;
    }

    /**
     * Getter for the start pattern of this include element
     * @return Pattern - This pattern contains all references to patterns, which
     * can be used as start elements.
     */
    public LinkedList<Pattern> getStartPatterns() {
        return startPatterns;
    }

    /**
     * Getter for the start pattern of this include element
     * @param startPattern
     */
    public void addStartPattern(Pattern startPattern) {
        this.startPatterns.add(startPattern);
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
     * Method for adding a define-pattern to the list of definedPatterns
     * This is used for declaration of a define-tag in this include.
     * The look-up table for all define-elements is updated, too.
     * @param definePattern
     */
    public void addDefinePattern(Define definePattern) {
        /**
         * Manage the look-up table:
         *
         * Check if there is already a Reference in the look-up table for the
         * name of the given definePattern
         **/
        if (!this.defineLookUpTable.containsKey(definePattern.getName())) {
            // Case: No Entry in the look-up table
            LinkedList<Define> newDefineList = new LinkedList<Define>();
            if (definePattern.getPatterns() != null && !definePattern.getPatterns().isEmpty()) {
                newDefineList.add(definePattern);
            }
            this.defineLookUpTable.put(definePattern.getName(), newDefineList);
        } else {
            // Case: There is already an entry in the look-up table
            LinkedList<Define> oldDefineList = this.defineLookUpTable.get(definePattern.getName());
            // Check if the same java object is already in this list
            if (!oldDefineList.contains(definePattern)) {
            	// Case the given definePattern is not in this list
            	// ==> add this definePattern to the list
            	if (definePattern.getPatterns() != null && !definePattern.getPatterns().isEmpty()) {
            		oldDefineList.add(definePattern);
            	}
            }
        }
    }

    /**
     * Getter for the list of definedPatterns
     * @return definedPatterns      LinkedHashSet<Define>
     */
    public LinkedHashSet<String> getDefinedPatternNames() {
        return new LinkedHashSet<String>(this.defineLookUpTable.keySet());
    }

    /**
     * Wrapper method for getting a list of definePatterns from the look-up table
     * @param defineName
     * @return LinkedList<Define>
     */
    public LinkedList<Define> getDefinedPatternsFromLookUpTable(String defineName) {
        return this.defineLookUpTable.get(defineName);
    }

    /**
     * Getter for the parsed Relax NG XSDSchema, which builds the base of this include.
     * @return RelaxNGSchema        the parsed Relax NG XSDSchema
     */
    public RelaxNGSchema getRngSchema() {
        return externalRNGSchema;
    }

    /**
     * Setter for the parsed Relax NG XSDSchema, which builds the base of this include.
     * @param rngSchema 
     */
    public void setRngSchema(RelaxNGSchema rngSchema) {
        this.externalRNGSchema = rngSchema;
    }

    /**
     * Setter for the start patterns
     * @param startPatterns  start patterns
     */
    public void setStartPatterns(LinkedList<Pattern> startPatterns) {
        this.startPatterns = startPatterns;
    }
}
