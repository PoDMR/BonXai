package eu.fox7.schematoolkit.relaxng.om;

import java.util.LinkedHashSet;

/**
 * Class representing the nsName element of RelaxNG
 * @author Lars Schmidt
 */
public class NsName extends NameClass {

    // The exceptions are optional in an nsName of Relax NG
    private LinkedHashSet<NameClass> exceptNames;

    /**
     * Constructor of class NsName
     */
    public NsName() {
        super();
        this.exceptNames = new LinkedHashSet<NameClass>();
    }

    /**
     * Getter for the contained exceptNames in this NsName
     * @return LinkedList<Pattern>
     */
    public LinkedHashSet<NameClass> getExceptNames() {
        return exceptNames;
    }

    /**
     * Method for adding an exceptName to this NsName
     * @param exceptName
     */
    public void addExceptName(NameClass exceptName) {
        this.exceptNames.add(exceptName);
    }
}
