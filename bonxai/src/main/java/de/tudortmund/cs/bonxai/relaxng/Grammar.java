package de.tudortmund.cs.bonxai.relaxng;

import de.tudortmund.cs.bonxai.common.DefaultNamespace;
import de.tudortmund.cs.bonxai.common.NamespaceList;
import de.tudortmund.cs.bonxai.common.SymbolTable;
import de.tudortmund.cs.bonxai.common.SymbolTableRef;
import java.util.LinkedHashSet;
import java.util.LinkedList;

/**
 * Class representing a RELAX NG grammar pattern.
 * @author Lars Schmidt
 */
public class Grammar extends Pattern {

    /**
     * Optional feature of the Full Syntax: combination of start patterns
     * Holds the value of the enumeration CombineMethod for this start pattern.
     *
     * It is important, that all start elements share the
     * same combine method.
     */
    private CombineMethod startCombineMethod;
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
     * of "combine" (same name, same combine method) in this grammar.
     *
     * When the structure is completely built, all referenced Patterns have to 
     * be found in the definedPatternNames list of this grammar, too! If one
     * name is not specified there, it is an error!
     **/
    private SymbolTable<LinkedList<Define>> defineLookUpTable;
    // List of all directly defined patterns in this grammar
    private LinkedHashSet<String> definedPatternNames;
    /**
     * List of include contents of this grammar
     */
    private LinkedList<IncludeContent> includeContents;
    private Grammar parentGrammar;

    /**
     * Constructor of class Grammar
     */
    public Grammar() {
        super(null, null, new NamespaceList(new DefaultNamespace(RelaxNGSchema.RELAXNG_NAMESPACE)));
        this.defineLookUpTable = new SymbolTable<LinkedList<Define>>();
        this.definedPatternNames = new LinkedHashSet<String>();
        this.includeContents = new LinkedList<IncludeContent>();
        this.startPatterns = new LinkedList<Pattern>();
    }

    /**
     * Constructor of class Grammar with default Namespace
     * @param defaultNamespace 
     */
    public Grammar(String defaultNamespace) {
        super(null, null, new NamespaceList(new DefaultNamespace(defaultNamespace)));
        this.defineLookUpTable = new SymbolTable<LinkedList<Define>>();
        this.definedPatternNames = new LinkedHashSet<String>();
        this.includeContents = new LinkedList<IncludeContent>();
        this.startPatterns = new LinkedList<Pattern>();
    }

    /**
     * Constructor of class Grammar with parameters of interface pattern
     * @param attributeDatatypeLibrary
     * @param attributeNamespace 
     * @param defaultNamespace
     */
    public Grammar(String attributeDatatypeLibrary, String attributeNamespace, String defaultNamespace) {
        super(attributeDatatypeLibrary, attributeNamespace, new NamespaceList(new DefaultNamespace(defaultNamespace)));
        this.defineLookUpTable = new SymbolTable<LinkedList<Define>>();
        this.definedPatternNames = new LinkedHashSet<String>();
        this.includeContents = new LinkedList<IncludeContent>();
        this.startPatterns = new LinkedList<Pattern>();
    }

    /**
     * Getter for the start pattern of this grammar element
     * @return Pattern - This pattern contains all references to patterns, which
     * can be used as start elements.
     */
    public LinkedList<Pattern> getStartPatterns() {
        return startPatterns;
    }

    /**
     * Getter for the start pattern of this grammar element
     * @param startPattern 
     */
    public void addStartPattern(Pattern startPattern) {
        this.startPatterns.add(startPattern);
    }

    /**
     * Getter of the XML attribute xmlns default namespace.
     * @return String   A string containing the value of the xmlns default namespace.
     */
    @Override
    public String getDefaultNamespace() {
        if (this.namespaceList.getDefaultNamespace() != null) {
            return this.namespaceList.getDefaultNamespace().getUri();
        } else {
            return null;
        }
    }

    /**
     * Setter of the XML attribute xmlns default namespace.
     * @param xmlns
     */
    @Override
    public void setDefaultNamespace(String xmlns) {
        if (this.namespaceList.getDefaultNamespace() != null) {
            this.namespaceList.getDefaultNamespace().setUri(xmlns);
        }
    }

    /**
     * Getter of the defineLookUpTable SymbolTable within this grammar.
     * @return SymbolTable<Define>  
     */
    public SymbolTable<LinkedList<Define>> getDefineLookUpTable() {
        return defineLookUpTable;
    }

    /**
     * Method for adding a define-pattern to the list of definedPatterns
     * This is used for declaration of a define-tag in this grammar.
     * The look-up table for all define-elements and the set of all define names
     * are updated, too.
     * @param definePattern
     */
    public void addDefinePattern(Define definePattern) {

        /**
         * Manage the look-up table:
         *
         **/
        this.registerDefinePatternInLookUpTable(definePattern);

        /**
         * Manage the list of all defined pattern names of this grammar element
         *
         **/
        if (!this.definedPatternNames.contains(definePattern.getName())) {
            this.definedPatternNames.add(definePattern.getName());
        }
    }

    /**
     * Method for registering a given definePattern in the look-up table
     *
     * @param definePattern
     * @return
     */
    public SymbolTableRef<LinkedList<Define>> registerDefinePatternInLookUpTable(Define definePattern) {
        SymbolTableRef<LinkedList<Define>> returnSymbolTableRef;

        /**
         * Manage the look-up table:
         *
         * Check if there is already a Reference in the look-up table for the
         * name of the given definePattern
         **/
        if (!this.getDefineLookUpTable().hasReference(definePattern.getName())) {
            // Case: No Entry in the look-up table
            LinkedList<Define> newDefineList = new LinkedList<Define>();

            /**
             * Only add Defines with pattern Content to the list of defines with the given name
             * If a Define has no pattern, it was created by a <ref name="defineName"/>.
             * This has to be registered in the SymbolTable,
             * but there must not be an empty entry in the referenced LinkedList.
             **/
            if (definePattern.getPatterns() != null && !definePattern.getPatterns().isEmpty()) {
                newDefineList.add(definePattern);
            }

            returnSymbolTableRef = this.getDefineLookUpTable().updateOrCreateReference(definePattern.getName(), newDefineList);

        } else {
            // Case: There is already an entry in the look-up table
            LinkedList<Define> oldDefineList = this.getDefineLookUpTable().getReference(definePattern.getName()).getReference();
            // Check if the same java object is already in this list
            if (!oldDefineList.contains(definePattern)) {
                // Case the given definePattern is not in this list
                // ==> add this definePattern to the list
                if (definePattern.getPatterns() != null && !definePattern.getPatterns().isEmpty()) {
                    oldDefineList.add(definePattern);
                }
                // update of the look-up-table
                returnSymbolTableRef = this.getDefineLookUpTable().updateOrCreateReference(definePattern.getName(), oldDefineList);
            } else {
                returnSymbolTableRef = this.getDefineLookUpTable().getReference(definePattern.getName());
            }
        }
        return returnSymbolTableRef;
    }

    /**
     * Getter for the list of definedPatterns
     * @return definedPatterns      LinkedHashSet<Define>
     */
    public LinkedHashSet<String> getDefinedPatternNames() {
        return new LinkedHashSet<String>(this.definedPatternNames);
    }

    /**
     * Getter for the CombineMethod of the list of startpatterns
     * @return CombineMethod
     */
    public CombineMethod getStartCombineMethod() {
        return startCombineMethod;
    }

    /**
     * Setter for the CombineMethod of the list of startpatterns
     * @param startCombineMethod
     */
    public void setStartCombineMethod(CombineMethod startCombineMethod) {
        this.startCombineMethod = startCombineMethod;
    }

    /**
     * Wrapper method for getting a list of definePatterns from the look-up table
     * @param defineName
     * @return LinkedList<Define>
     */
    public LinkedList<Define> getDefinedPatternsFromLookUpTable(String defineName) {
        return this.getDefineLookUpTable().getReference(defineName).getReference();
    }

    /**
     * Getter for the parent grammar object
     * @return Grammar      The parent grammar object
     */
    public Grammar getParentGrammar() {
        return this.parentGrammar;
    }

    /**
     * Setter for the parent grammar object
     * @param grammar      The parent grammar object
     */
    public void setParentGrammar(Grammar grammar) {
        this.parentGrammar = grammar;
    }

    /**
     * Getter for the include content
     * @return LinkedList<IncludeContent>      The include content
     */
    public LinkedList<IncludeContent> getIncludeContents() {
        return includeContents;
    }

    /**
     * Add an include content to the grammar
     * @param includeContents      The include content
     */
    public void addIncludeContent(IncludeContent includeContents) {
        this.includeContents.add(includeContents);
    }

    /**
     * Setter for the list of include contents
     * @param includeContents      The include content list
     */
    public void setIncludeContents(LinkedList<IncludeContent> includeContents) {
        this.includeContents = includeContents;
    }

    /**
     * Setter for the start patterns of the grammar
     * @param startPatterns      The list of start patterns
     */
    public void setStartPatterns(LinkedList<Pattern> startPatterns) {
        this.startPatterns = startPatterns;
    }

    /**
     * Method for registering a define pattern name in the grammar
     * @param name      String = define pattern name
     */
    public void registerDefinePatternName(String name) {
        if (!this.definedPatternNames.contains(name)) {
            this.definedPatternNames.add(name);
        }
    }
}
