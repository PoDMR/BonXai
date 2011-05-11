/*
 * Created on Sep 3, 2008
 * Modified on $Date: 2009-10-26 15:29:20 $
 */
package gjb.flt.regex.infer.rwr.impl;


import java.util.List;

import org.apache.commons.collections15.BidiMap;
import org.apache.commons.collections15.bidimap.DualHashBidiMap;

/**
 * @author gjb
 * @version $Revision: 1.1 $
 * 
 */
public class GraphAutomaton implements Automaton {

    public static final String SOURCE_LABEL = "^";
    public static final String SINK_LABEL = "$";
    protected int[][] transitions;
    protected BidiMap<String,Integer> labels;

    public GraphAutomaton(int alphabetSize) {
        transitions = new int[alphabetSize + 1][alphabetSize + 1];
        labels = new DualHashBidiMap<String,Integer>();
    }

    public GraphAutomaton(List<String> labels) {
        this(labels.size());
        for (String label : labels) {
            try {
                getNewIndex(label);
            } catch (AutomatonConstructionException e) {
                e.printStackTrace();
                throw new RuntimeException("unexpected exception", e);
            }
        }
    }

    public void addInitial(String label) throws AutomatonConstructionException {
        addTransition(SOURCE_LABEL, label);
    }

    public void addTransition(String fromLabel, String toLabel)
            throws AutomatonConstructionException {
        int i = getNewIndex(fromLabel);
        int j = getNewIndex(toLabel);
        transitions[i][j] = 1;
    }

    public void addFinal(String label) throws AutomatonConstructionException {
        addTransition(label, SINK_LABEL);
    }

    public void addEpsilon() {
        try {
            addTransition(SOURCE_LABEL, SINK_LABEL);
        } catch (AutomatonConstructionException e) {
            e.printStackTrace();
            throw new RuntimeException("unexpected exception", e);
        }
    }

    public boolean isInitial(String label) throws InvalidLabelException {
        return hasTransition(SOURCE_LABEL, label);
    }

    public boolean hasTransition(String fromLabel, String toLabel)
            throws InvalidLabelException {
        int i = getIndex(fromLabel);
        int j = getIndex(toLabel);
        return transitions[i][j] != 0;
    }

    public boolean isFinal(String label) throws InvalidLabelException {
        return hasTransition(label, SINK_LABEL);
    }

    public boolean acceptsEpsilon() {
        try {
            return hasTransition(SOURCE_LABEL, SINK_LABEL);
        } catch (InvalidLabelException e) {
            e.printStackTrace();
            throw new RuntimeException("unexpected exception", e);
        }
    }

    public int get(int i, int j) {
        return transitions[i][j];
    }

    public boolean isInitial(int i) {
        return transitions[transitions.length-1][i] != 0;
    }

    public boolean isFinal(int i) {
        return transitions[i][transitions.length-1] != 0;
    }

    public void set(int i, int j, int value) {
        transitions[i][j] = value;
    }

    public String getLabel(int i) {
        return labels.getKey(i);
    }

    public void setLabel(int i, String label) {
        labels.put(label, i);
    }

    public boolean hasLabel(String label) {
        return label.equals(SOURCE_LABEL) || label.equals(SINK_LABEL) ||
            labels.containsKey(label);
    }

    public int getNumberOfStates() {
        return transitions.length;
    }

    protected int getNewIndex(String label) throws AutomatonConstructionException {
        if (label.equals(SOURCE_LABEL))
            return transitions.length - 1;
        else if (label.equals(SINK_LABEL))
            return transitions.length - 1;
        else {
            if (!labels.containsKey(label)) {
                if (labels.size() == transitions.length - 1)
                    throw new AutomatonConstructionException("too many labels for automaton size");
                labels.put(label, labels.size());
            }
            return labels.get(label);
        }
    }

    protected int getIndex(String label) throws InvalidLabelException {
        if (label.equals(SOURCE_LABEL))
            return transitions.length - 1;
        else if (label.equals(SINK_LABEL))
            return transitions.length - 1;
        else if (labels.containsKey(label))
            return labels.get(label);
        else
            throw new InvalidLabelException("label '" + label + "' is not defined");
    }

    public boolean isReduced() {
        if (this.getNumberOfStates() == 1)
            return true;
        else if (this.getNumberOfStates() == 2 && this.get(1, 1) == 0)
            return true;
        else
            return false;
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        for (String label : labels.keySet()) {
            str.append(label).append(":").append(labels.get(label)).append(" ");
        }
        str.append("\n");
        for (int i = 0; i < getNumberOfStates(); i++) {
            for (int j = 0; j < getNumberOfStates(); j++)
                str.append(transitions[i][j]).append(" ");
            str.append("\n");
        }
        return str.toString();
    }

}
