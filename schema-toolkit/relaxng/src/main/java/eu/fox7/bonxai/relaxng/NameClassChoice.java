package eu.fox7.bonxai.relaxng;

import java.util.LinkedHashSet;

/**
 * Class representing the choice element for nameClass contents of RelaxNG
 * @author Lars Schmidt
 */
public class NameClassChoice extends NameClass {

    /**
     * In the Simple XML Syntax of RELAX NG there are only two elements allowed
     * in each choice.
     * The Full XML Syntax makes no restrictions on the quantity of names
     * defined in a choice.
     */
    private LinkedHashSet<NameClass> choiceNames;

    /**
     * Constructor of class NameClassChoice
     */
    public NameClassChoice() {
        super();
        this.choiceNames = new LinkedHashSet<NameClass>();
    }

    /**
     * Getter for the contained choiceNames in this choice
     * @return LinkedList<Pattern>
     */
    public LinkedHashSet<NameClass> getChoiceNames() {
        return choiceNames;
    }

    /**
     * Method for adding an choiceName to this choice
     * @param choiceName
     */
    public void addChoiceName(NameClass choiceName) {
        this.choiceNames.add(choiceName);
    }
}
