package eu.fox7.schematoolkit.relaxng.om;

import java.util.LinkedHashSet;

/**
 * Class representing the anyName element of RelaxNG
 * @author Lars Schmidt
 */
public class AnyName extends NameClass {

    // The exceptions are optional in an anyName of Relax NG
    private LinkedHashSet<NameClass> exceptNames;

    /**
     * Constructor of class AnyName
     */
    public AnyName() {
        super();
        this.exceptNames = new LinkedHashSet<NameClass>();
    }

    /**
     * Getter for the contained exceptNames in this AnyName
     * @return LinkedList<Pattern>
     */
    public LinkedHashSet<NameClass> getExceptNames() {
        return exceptNames;
    }

    /**
     * Method for adding an exceptName to this AnyName
     * @param exceptName
     */
    public void addExceptName(NameClass exceptName) {
        this.exceptNames.add(exceptName);
    }

}
