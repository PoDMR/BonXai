package eu.fox7.schematoolkit.relaxng.om;

import java.util.List;

/**
 * Class representing the <parentRef name="NCName"/> element of RelaxNG
 * @author Lars Schmidt
 */
public class ParentRef extends Pattern {
    /**
     * Name of the define
     */
    private String refName;
    /**
     * Grammar object of the parentRef
     */
    private Grammar grammar;

    /**
     * Constructor of class ParentRef
     * @param definedPattern
     * @param grammar
     */
    public ParentRef(String refName, Grammar grammar) {
        this.refName = refName;
        this.grammar = grammar;
    }

    /**
     * Getter for the pattern of the defined and referenced pattern
     * @return <LinkedList<Define>>
     */
    public List<Define> getDefineList() {
        return this.grammar.getParentGrammar().getDefinedPattern(refName);
    }

    /**
     * Getter for the RefName (name of the define)
     * @return String   Name of the define
     */
    public String getRefName() {
        return refName;
    }

    /**
     * Getter for the grammar object
     * @return Grammar   Grammar of the parentRef
     */
    public Grammar getGrammar() {
        return grammar;
    }

    /**
     * Getter for the unique ID of the current parentRef
     * @return String   The unique ID of the current parentRef
     */
    public String getUniqueRefID() {
        return this.getRefName() + "_" + Integer.toHexString(System.identityHashCode(this.grammar.getParentGrammar()));
    }
}
