package eu.fox7.bonxai.relaxng;

import eu.fox7.bonxai.common.SymbolTableRef;

import java.util.LinkedList;

/**
 * Class representing the <ref name="NCName"/> element of RelaxNG
 * @author Lars Schmidt
 */
public class Ref extends Pattern {

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
     * Constructor of class Ref
     * @param definedPattern        Defined pattern as target of the parentRef
     * @param grammar               Grammar object of the parentRef
     */
    public Ref(SymbolTableRef<LinkedList<Define>> definedPattern, Grammar grammar) {
        this.definedPattern = definedPattern;
        this.grammar = grammar;
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
     * ToString method of the current class
     * @return String   return the refName
     */
    public String toString() {
        return "Ref (" + this.getRefName() + ")";
    }

    /**
     * Getter for the defined patterns
     * @return SymbolTableRef<LinkedList<Define>>
     */
    public SymbolTableRef<LinkedList<Define>> getSymbolTableReference() {
        return this.definedPattern;
    }

    /**
     * Getter for the grammar object
     * @return Grammar     The grammar object of the Ref
     */
    public Grammar getGrammar() {
        return grammar;
    }

    /**
     * Getter for the unique ID of the Ref object
     * @return String       The unique ID of the Ref object
     */
    public String getUniqueRefID() {
        return this.getRefName() + "_" + Integer.toHexString(System.identityHashCode(this.grammar)).toString();
    }
}
