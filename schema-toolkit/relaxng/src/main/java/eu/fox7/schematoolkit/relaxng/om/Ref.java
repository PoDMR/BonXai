package eu.fox7.schematoolkit.relaxng.om;

import java.util.List;

/**
 * Class representing the <ref name="NCName"/> element of RelaxNG
 * @author Lars Schmidt
 */
public class Ref extends Pattern {
    /**
     * Name of the define
     */
    private String refName;
    /**
     * Grammar object of the parentRef
     */
    private Grammar grammar;

    /**
     * Constructor of class Ref
     * @param definedPattern        Defined pattern as target of the parentRef
     * @param grammar               Grammar object of the parentRef
     */
    public Ref(String refName, Grammar grammar) {
        this.refName = refName;
        this.grammar = grammar;
    }

    /**
     * Getter for the pattern of the defined and referenced pattern
     * @return <LinkedList<Define>>
     */
    public List<Define> getDefineList() {
        return this.grammar.getDefinedPattern(refName);
    }

    /**
     * Getter for the RefName (name of the define)
     * @return String   Name of the define
     */
    public String getRefName() {
        return refName;
    }

    /**
     * ToString method of the current class
     * @return String   return the refName
     */
    public String toString() {
        return "Ref (" + this.getRefName() + ")";
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
