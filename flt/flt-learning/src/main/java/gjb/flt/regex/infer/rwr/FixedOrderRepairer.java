/*
 * Created on Sep 10, 2008
 * Modified on $Date: 2009-10-26 15:29:20 $
 */
package gjb.flt.regex.infer.rwr;

import gjb.flt.regex.infer.rwr.impl.Automaton;
import gjb.flt.regex.infer.rwr.repairers.ConcatOptionalRepairFinder;
import gjb.flt.regex.infer.rwr.repairers.ConcatOptionalRepairer;
import gjb.flt.regex.infer.rwr.repairers.DisjunctionRepairFinder;
import gjb.flt.regex.infer.rwr.repairers.DisjunctionRepairer;
import gjb.flt.regex.infer.rwr.repairers.OptionalConcatOptionalRepairFinder;
import gjb.flt.regex.infer.rwr.repairers.OptionalConcatOptionalRepairer;
import gjb.flt.regex.infer.rwr.repairers.OptionalConcatRepairFinder;
import gjb.flt.regex.infer.rwr.repairers.OptionalConcatRepairer;
import gjb.flt.regex.infer.rwr.repairers.RepairOpportunityFinder;
import gjb.flt.regex.infer.rwr.rewriters.ConcatOptionalRewriter;
import gjb.flt.regex.infer.rwr.rewriters.DisjunctionRewriter;
import gjb.flt.regex.infer.rwr.rewriters.OptionalConcatOptionalRewriter;
import gjb.flt.regex.infer.rwr.rewriters.OptionalConcatRewriter;


/**
 * @author gjb
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
