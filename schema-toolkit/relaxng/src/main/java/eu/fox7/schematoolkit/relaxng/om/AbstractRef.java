package eu.fox7.schematoolkit.relaxng.om;

import java.util.List;

public abstract class AbstractRef extends Pattern {
    /**
     * Name of the define
     */
    protected String refName;
    /**
     * Grammar object of the parentRef
     */
    protected Grammar grammar;

    /**
     * Constructor of class ParentRef
     * @param definedPattern
     * @param grammar
     */
    public AbstractRef(String refName, Grammar grammar) {
        this.refName = refName;
        this.grammar = grammar;
    }
    
    /**
     * Getter for the pattern of the defined and referenced pattern
     * @return <LinkedList<Define>>
     */
    public abstract List<Define> getDefineList();
    
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
    public abstract String getUniqueRefID();
}
