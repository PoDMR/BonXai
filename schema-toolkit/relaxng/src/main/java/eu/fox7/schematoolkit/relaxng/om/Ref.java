package eu.fox7.schematoolkit.relaxng.om;

import java.util.List;

/**
 * Class representing the <ref name="NCName"/> element of RelaxNG
 * @author Lars Schmidt
 */
public class Ref extends AbstractRef {
    /**
     * Constructor of class Ref
     * @param definedPattern        Defined pattern as target of the parentRef
     * @param grammar               Grammar object of the parentRef
     */
    public Ref(String refName, Grammar grammar) {
    	super(refName, grammar);
    }

    /**
     * Getter for the pattern of the defined and referenced pattern
     * @return <LinkedList<Define>>
     */
    public List<Define> getDefineList() {
        return this.grammar.getDefinedPattern(refName);
    }

    /**
     * ToString method of the current class
     * @return String   return the refName
     */
    public String toString() {
        return "Ref (" + this.getRefName() + ")";
    }

    /**
     * Getter for the unique ID of the Ref object
     * @return String       The unique ID of the Ref object
     */
    @Override
    public String getUniqueRefID() {
        return this.getRefName() + "_" + Integer.toHexString(System.identityHashCode(this.grammar));
    }
}
