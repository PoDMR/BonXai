/*
 * Created on Sep 20, 2008
 * Modified on $Date: 2009-10-27 09:30:13 $
 */
package gjb.flt.regex.infer.rwr;

import gjb.flt.regex.infer.rwr.impl.Automaton;
import gjb.flt.regex.infer.rwr.impl.GraphAutomatonFactory;
import gjb.flt.regex.infer.rwr.impl.Opportunity;
import gjb.flt.regex.infer.rwr.measures.LanguageSizeMeasure;
import gjb.flt.regex.infer.rwr.measures.OpportunityMeasure;

import java.util.Stack;

/**
 * @author gjb
 * @version $Revision: 1.3 $
 * 
 */
public class TransitionCountRepairer extends BacktrackingRepairer {

    protected static LanguageSizeMeasure sizeMeasure = new LanguageSizeMeasure();
    protected static GraphAutomatonFactory factory = new GraphAutomatonFactory();

    public TransitionCountRepairer(OpportunityMeasure measure, int maxTries) {
        super(measure, maxTries);
    }

    public Automaton rewrite(Automaton automaton) {
        automaton = rewriter.rewrite(automaton);
        if (automaton.isReduced())
            return automaton;
        Stack<BacktrackState> stack = new Stack<BacktrackState>();
        double bestMeasure = Double.MAX_VALUE;
        Automaton bestAutomaton = null;
        updateStack(stack, automaton);
        while (!stack.isEmpty()) {
            if (!stack.peek().hasNext()) {
                stack.pop();
                continue;
            }
            Opportunity opp = stack.peek().getNext();
            automaton = stack.peek().getAutomaton();
            AutomatonRewriter repairer = opp.getRepairer();
            AutomatonRewriter oppRewriter = opp.getRewriter();
            Automaton newAutomaton = repairer.rewrite(automaton, opp.getIndices());
            newAutomaton = oppRewriter.rewrite(newAutomaton, opp.getIndices());
            newAutomaton = rewriter.rewrite(newAutomaton);
            if (newAutomaton.isReduced()) {
                double size = sizeMeasure.compute(factory.expand(newAutomaton));
                if (size < bestMeasure) {
                    bestMeasure = size;
                    bestAutomaton = newAutomaton;
                }
            } else {
                updateStack(stack, newAutomaton);
            }
        }
        return bestAutomaton;
    }

}
