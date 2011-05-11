/*
 * Created on Sep 11, 2008
 * Modified on $Date: 2009-10-27 09:30:13 $
 */
package gjb.flt.regex.infer.rwr.impl;

import gjb.flt.regex.infer.rwr.AutomatonRewriter;

/**
 * @author gjb
 * @version $Revision: 1.1 $
 * 
 */
public class Opportunity {

    protected int[] indices;
    protected double measure;
    protected AutomatonRewriter repairer;
    protected AutomatonRewriter rewriter;

    public Opportunity(int[] indices, double measure,
                       AutomatonRewriter repairer,
                       AutomatonRewriter rewriter) {
        this.indices = indices;
        this.measure = measure;
        this.repairer = repairer;
        this.rewriter = rewriter;
    }

    public int[] getIndices() {
        return indices;
    }

    public double getMeasure() {
        return measure;
    }

    public AutomatonRewriter getRepairer() {
        return repairer;
    }

    public AutomatonRewriter getRewriter() {
        return rewriter;
    }

    @Override
    public String toString() {
        return getRepairer().getClass().toString() +
            " at " + indicesToString() +
            " for measure " + getMeasure();
    }

    protected String indicesToString() {
        StringBuilder str = new StringBuilder();
        str.append("[");
        for (int i = 0; i < indices.length; i++) {
            if (i > 0)
                str.append(", ");
            str.append(indices[i]);
        }
        str.append("]");
        return str.toString();
    }

}
