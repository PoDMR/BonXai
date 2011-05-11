package de.tudortmund.cs.bonxai.relaxng;

import de.tudortmund.cs.bonxai.common.SymbolTableRef;
import java.util.LinkedList;

/**
 * Class representing the <parentRef name="NCName"/> element of RelaxNG
 * @author Lars Schmidt
 */
public class ParentRef extends Pattern {

    /**
     * The defined pattern as target of the parentRef
     */
    private SymbolTableRef<LinkedList<Define>> definedPattern;
    /**
     * Name of the define
     */
    private String RefName;
    /**
     * Grammar object of the parentRef
     */
    private Grammar grammar;
    /**
     * Parent grammar object of the parentRef
     */
    private Grammar parentGrammar;

    /**
     * Constructor of class ParentRef
     * @param definedPattern
     * @param grammar
     * @param parentGrammar
     */
    public ParentRef(SymbolTableRef<LinkedList<Define>> definedPattern, Grammar grammar, Grammar parentGrammar) {
        this.definedPattern = definedPattern;
        this.grammar = grammar;
        this.parentGrammar = parentGrammar;
    }

    /**
     * Getter for the pattern of the defined and referenced pattern
     * @return <LinkedList<Define>>
     */
    public LinkedList<Define> getDefineList() {
        if (this.definedPattern != null && this.definedPattern.getReference() != null) {
            return this.definedPattern.getReference();
        } else {
            return null;
        }
    }

    /**
     * Getter for the RefName (name of the define)
     * @return String   Name of the define
     */
    public String getRefName() {
        return RefName;
    }

    /**
     * Setter for the RefName (name of the define)
     * @param RefName   Name of the define
     */
    public void setRefName(String RefName) {
        this.RefName = RefName;
    }

    /**
     * Getter for the grammar object
     * @return Grammar   Grammar of the parentRef
     */
    public Grammar getGrammar() {
        return grammar;
    }

    /**
     * Getter for the parent grammar object
     * @return Grammar   Parent grammar of the parentRef
     */
    public Grammar getParentGrammar() {
        return parentGrammar;
    }

    /**
     * Getter for the unique ID of the current parentRef
     * @return String   The unique ID of the current parentRef
     */
    public String getUniqueRefID() {
        return this.getRefName() + "_" + Integer.toHexString(System.identityHashCode(this.parentGrammar));
    }
}
