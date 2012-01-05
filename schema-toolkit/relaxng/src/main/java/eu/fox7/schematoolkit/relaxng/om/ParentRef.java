package eu.fox7.schematoolkit.relaxng.om;

import java.util.List;

/**
 * Class representing the <parentRef name="NCName"/> element of RelaxNG
 * @author Lars Schmidt
 */
public class ParentRef extends AbstractRef {
    /**
     * Constructor of class ParentRef
     * @param definedPattern
     * @param grammar
     */
    public ParentRef(String refName, Grammar grammar) {
        super(refName, grammar);
    }

    /**
     * Getter for the pattern of the defined and referenced pattern
     * @return <LinkedList<Define>>
     */
    @Override
    public List<Define> getDefineList() {
        return this.grammar.getParentGrammar().getDefinedPattern(refName);
    }

    /**
     * Getter for the unique ID of the current parentRef
     * @return String   The unique ID of the current parentRef
     */
    @Override
    public String getUniqueRefID() {
        return this.getRefName() + "_" + Integer.toHexString(System.identityHashCode(this.grammar.getParentGrammar()));
    }

}
