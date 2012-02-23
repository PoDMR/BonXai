/*
 * Created on Sep 8, 2008
 * Modified on $Date: 2009-10-26 15:29:20 $
 */
package eu.fox7.flt.regex.infer.rwr;

import eu.fox7.flt.regex.Glushkov;
import eu.fox7.flt.regex.Regex;
import eu.fox7.flt.regex.infer.rwr.impl.Automaton;
import eu.fox7.flt.regex.infer.rwr.rewriters.ConcatFinder;
import eu.fox7.flt.regex.infer.rwr.rewriters.ConcatOptionalFinder;
import eu.fox7.flt.regex.infer.rwr.rewriters.ConcatOptionalRewriter;
import eu.fox7.flt.regex.infer.rwr.rewriters.ConcatRewriter;
import eu.fox7.flt.regex.infer.rwr.rewriters.DisjunctionFinder;
import eu.fox7.flt.regex.infer.rwr.rewriters.DisjunctionRewriter;
import eu.fox7.flt.regex.infer.rwr.rewriters.OpportunityFinder;
import eu.fox7.flt.regex.infer.rwr.rewriters.OptionalConcatFinder;
import eu.fox7.flt.regex.infer.rwr.rewriters.OptionalConcatOptionalFinder;
import eu.fox7.flt.regex.infer.rwr.rewriters.OptionalConcatOptionalRewriter;
import eu.fox7.flt.regex.infer.rwr.rewriters.OptionalConcatRewriter;
import eu.fox7.flt.regex.infer.rwr.rewriters.OptionalFinder;
import eu.fox7.flt.regex.infer.rwr.rewriters.OptionalRewriter;
import eu.fox7.flt.regex.infer.rwr.rewriters.RepetitionFinder;
import eu.fox7.flt.regex.infer.rwr.rewriters.RepetitionRewriter;
import eu.fox7.util.tree.SExpressionParseException;
import eu.fox7.util.tree.Tree;

/**
 * @author eu.fox7
 * @version $Revision: 1.2 $
 * 
 */
public class Rewriter implements RewriteEngine {

    protected OpportunityFinder repetitionFinder = new RepetitionFinder();
    protected OpportunityFinder[] finders = new OpportunityFinder[] {
            new DisjunctionFinder(),
            repetitionFinder,
            new ConcatFinder(),
            new ConcatOptionalFinder(),
            new OptionalConcatFinder(),
            new OptionalConcatOptionalFinder()
    };
    protected OpportunityFinder optionalFinder = new OptionalFinder();
    protected AutomatonRewriter repetitionRewriter = new RepetitionRewriter();
    protected AutomatonRewriter[] rewriters = new AutomatonRewriter[] {
            new DisjunctionRewriter(),
            repetitionRewriter,
            new ConcatRewriter(),
            new ConcatOptionalRewriter(),
            new OptionalConcatRewriter(),
            new OptionalConcatOptionalRewriter()
    };
    protected AutomatonRewriter optionalRewriter = new OptionalRewriter();

    public String rewriteToRegex(Automaton automaton)
            throws NoOpportunityFoundException {
        if (automaton.getNumberOfStates() == 1)
            if (automaton.acceptsEpsilon())
                return Regex.LEFT_BRACKET + Regex.EPSILON_SYMBOL + Regex.RIGHT_BRACKET;
            else
                return Regex.LEFT_BRACKET + Regex.EMPTY_SYMBOL + Regex.RIGHT_BRACKET;
        Automaton newAutomaton = rewrite(automaton);
        if (newAutomaton.isReduced())
            return unmark(newAutomaton.getLabel(0));
        else
            throw new NoOpportunityFoundException("could not be reduced",
                                                  newAutomaton);
    }

    public Automaton rewrite(Automaton automaton) {
        while (automaton.getNumberOfStates() > 2) {
            int[] indices = null;
            for (int i = 0; i < finders.length; i++) {
                indices = finders[i].getFirst(automaton);
                if (indices != null) {
                    automaton = rewriters[i].rewrite(automaton, indices);
                    break;
                }
            }
            if (indices == null)
                return automaton;
        }
        int[] indices = repetitionFinder.getFirst(automaton);
        if (indices != null)
            automaton = repetitionRewriter.rewrite(automaton, indices);
        indices = optionalFinder.getFirst(automaton);
        if (indices != null)
            automaton = optionalRewriter.rewrite(automaton, indices);
        return automaton;
    }

    @SuppressWarnings("rawtypes")
    protected String unmark(String regexStr) {
        try {
            Regex regex = new Regex();
            Glushkov glushkov = new Glushkov();
            Tree tree = glushkov.unmark(regex.getTree(regexStr));
            return tree.toSExpression();
        } catch (SExpressionParseException e) {
            e.printStackTrace();
            throw new RuntimeException("unexpected exception", e);
        }
    }

}
