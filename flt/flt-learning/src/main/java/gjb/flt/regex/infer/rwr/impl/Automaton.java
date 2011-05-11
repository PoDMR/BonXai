/*
 * Created on Sep 3, 2008
 * Modified on $Date: 2009-10-26 15:29:20 $
 */
package gjb.flt.regex.infer.rwr.impl;


/**
 * @author gjb
 * @version $Revision: 1.1 $
 * 
 */
public interface Automaton {

    public void addInitial(String label)
            throws AutomatonConstructionException;

    public void addTransition(String fromLabel, String toLabel)
            throws AutomatonConstructionException;

    public void addFinal(String label) throws AutomatonConstructionException;

    public void addEpsilon();

    public boolean isInitial(String label) throws InvalidLabelException;

    public boolean hasTransition(String fromLabel, String toLabel)
            throws InvalidLabelException;

    public boolean isFinal(String label) throws InvalidLabelException;

    public boolean acceptsEpsilon();

    public int get(int i, int j);

    public boolean isInitial(int i);

    public boolean isFinal(int i);
    
    public void set(int i, int j, int value);

    public String getLabel(int i);

    public void setLabel(int i, String label);

    public boolean hasLabel(String label);

    public int getNumberOfStates();

    public boolean isReduced();

}
