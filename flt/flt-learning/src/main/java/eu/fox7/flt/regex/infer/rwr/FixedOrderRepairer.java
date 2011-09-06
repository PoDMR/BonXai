/*
 * Created on Sep 10, 2008
 * Modified on $Date: 2009-10-26 15:29:20 $
 */
package eu.fox7.flt.regex.infer.rwr;

import eu.fox7.flt.regex.infer.rwr.impl.Automaton;
import eu.fox7.flt.regex.infer.rwr.repairers.ConcatOptionalRepairFinder;
import eu.fox7.flt.regex.infer.rwr.repairers.ConcatOptionalRepairer;
import eu.fox7.flt.regex.infer.rwr.repairers.DisjunctionRepairFinder;
import eu.fox7.flt.regex.infer.rwr.repairers.DisjunctionRepairer;
import eu.fox7.flt.regex.infer.rwr.repairers.OptionalConcatOptionalRepairFinder;
import eu.fox7.flt.regex.infer.rwr.repairers.OptionalConcatOptionalRepairer;
import eu.fox7.flt.regex.infer.rwr.repairers.OptionalConcatRepairFinder;
import eu.fox7.flt.regex.infer.rwr.repairers.OptionalConcatRepairer;
import eu.fox7.flt.regex.infer.rwr.repairers.RepairOpportunityFinder;
import eu.fox7.flt.regex.infer.rwr.rewriters.ConcatOptionalRewriter;
import eu.fox7.flt.regex.infer.rwr.rewriters.DisjunctionRewriter;
import eu.fox7.flt.regex.infer.rwr.rewriters.OptionalConcatOptionalRewriter;
import eu.fox7.flt.regex.infer.rwr.rewriters.OptionalConcatRewriter;


/**
 * @author eu.fox7
 * @version $Revision: 1.2 $
 * 
 */
public class FixedOrderRepairer implements RewriteEngine {

    protected RewriteEngine rewriter = new Rewriter();
    protected RepairOpportunityFinder[] repairFinders = {
            new ConcatOptionalRepairFinder(),
            new OptionalConcatRepairFinder(),
            new DisjunctionRepairFinder(),
            new OptionalConcatOptionalRepairFinder()
    };
    protected AutomatonRewriter[] repairers = {
            new ConcatOptionalRepairer(),
            new OptionalConcatRepairer(),
            new DisjunctionRepairer(),
            new OptionalConcatOptionalRepairer()
    };
    protected AutomatonRewriter[] rewriters = {
            new ConcatOptionalRewriter(),
            new OptionalConcatRewriter(),
            new DisjunctionRewriter(),
            new OptionalConcatOptionalRewriter()
    };

    public Automaton rewrite(Automaton automaton) {
        for (;;) {
            automaton = rewriter.rewrite(automaton);
            if (automaton.isReduced())
                break;
            int[] indices = null;
            for (int i = 0; i < repairFinders.length; i++) {
                indices = repairFinders[i].getFirst(automaton);
                if (indices != null) {
                    automaton = repairers[i].rewrite(automaton, indices);
                    automaton = rewriters[i].rewrite(automaton, indices);
                    break;
                }
            }
            if (indices == null)
                break;
        }
        return automaton;
    }

    public String rewriteToRegex(Automaton automaton)
            throws NoOpportunityFoundException {
        Automaton newAutomaton = rewrite(automaton);
        if (newAutomaton.isReduced())
            return rewriter.rewriteToRegex(newAutomaton);
        else
            throw new NoOpportunityFoundException("no repairs found",
                                                  newAutomaton);
    }

}
