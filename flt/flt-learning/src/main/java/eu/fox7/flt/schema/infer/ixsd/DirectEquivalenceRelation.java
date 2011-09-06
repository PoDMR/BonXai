/*
 * Created on Mar 14, 2007
 * Modified on $Date: 2009-11-09 13:12:26 $
 */
package eu.fox7.flt.schema.infer.ixsd;

import eu.fox7.flt.automata.measures.RelativeLanguageMeasure;
import eu.fox7.flt.treeautomata.impl.ContentAutomaton;

import java.util.Set;

/**
 * @author eu.fox7
 * @version $Revision: 1.1 $
 * 
 */
public class DirectEquivalenceRelation
        implements ContentEquivalenceRelation {

    protected RelativeLanguageMeasure<Double> measure;
    protected double epsilon;

    public DirectEquivalenceRelation(double epsilon, RelativeLanguageMeasure<Double> measure) {
        super();
        this.measure = measure;
        this.epsilon = epsilon;
    }

    public boolean areEquivalent(ContentAutomaton model1, ContentAutomaton model2) {
        return getMeasure().compute(model1, model2) < epsilon;
    }

    public RelativeLanguageMeasure<Double> getMeasure() {
        return measure;
    }

    public double getEpsilon() {
        return epsilon;
    }

    public Set<Set<ContentAutomaton>> getClasses(Iterable<ContentAutomaton> set) {
        return null;
    }

}
