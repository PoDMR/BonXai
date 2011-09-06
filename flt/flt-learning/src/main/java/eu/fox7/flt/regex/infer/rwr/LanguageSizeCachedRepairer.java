/*
 * Created on Oct 10, 2008
 * Modified on $Date: 2009-10-27 09:30:13 $
 */
package eu.fox7.flt.regex.infer.rwr;

import eu.fox7.flt.regex.infer.rwr.impl.Automaton;
import eu.fox7.flt.regex.infer.rwr.impl.Opportunity;
import eu.fox7.flt.regex.infer.rwr.impl.io.AutomatonWriter;
import eu.fox7.flt.regex.infer.rwr.impl.io.GraphAutomatonWriter;
import eu.fox7.flt.regex.infer.rwr.measures.LanguageSizeMeasure;
import eu.fox7.flt.regex.infer.rwr.measures.OpportunityMeasure;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

/**
 * @author eu.fox7
 * @version $Revision: 1.3 $
 * 
 */
public class LanguageSizeCachedRepairer extends BacktrackingRepairer {

    protected Set<String> cache = new HashSet<String>();
    protected Map<Opportunity,String> signatures = new HashMap<Opportunity,String>();
    protected AutomatonWriter writer = new GraphAutomatonWriter();
    
    public LanguageSizeCachedRepairer(OpportunityMeasure measure, int maxTries) {
        super(measure, maxTries);
    }

    @Override
    public Automaton rewrite(Automaton automaton) {
        automaton = rewriter.rewrite(automaton);
        if (automaton.isReduced())
            return automaton;
        Stack<BacktrackState> stack = new Stack<BacktrackState>();
        double bestMeasure = Double.MAX_VALUE;
        Automaton bestAutomaton = null;
        updateStack(stack, automaton);
        while (!stack.isEmpty()) {
            if (!stack.peek().hasNext() || stack.peek().getMeasure() > bestMeasure) {
                stack.pop();
                continue;
            }
            Opportunity opp = stack.peek().getNext();
            if (cache.contains(signatures.get(opp)))
                continue;
            automaton = stack.peek().getAutomaton();
            AutomatonRewriter repairer = opp.getRepairer();
            AutomatonRewriter oppRewriter = opp.getRewriter();
            Automaton newAutomaton = repairer.rewrite(automaton, opp.getIndices());
            newAutomaton = oppRewriter.rewrite(newAutomaton, opp.getIndices());
            newAutomaton = rewriter.rewrite(newAutomaton);
            if (newAutomaton.isReduced()) {
                double size = ((LanguageSizeMeasure) measure).compute(factory.expand(newAutomaton));
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

    @Override
    protected void updateStack(Stack<BacktrackState> stack, Automaton automaton) {
        List<Opportunity> opportunities = new ArrayList<Opportunity>();
        for (int i = 0; i < repairFinders.length; i++) {
            List<int[]> indicesList = repairFinders[i].getAll(automaton);
            for (int[] indices : indicesList) {
                Automaton newAutomaton = repairers[i].rewrite(automaton, indices);
                Automaton expandedAutomaton = factory.expand(newAutomaton);
                String signature = convert(expandedAutomaton);
                if (cache.contains(signature)) {
                    opportunities.add(null);
                } else {
                    Opportunity opportunity = measure.measure(automaton,
                                                              expandedAutomaton,
                                                              repairers[i],
                                                              rewriters[i],
                                                              indices);
                    signatures.put(opportunity, signature);
                    opportunities.add(opportunity);
                }
            }
        }
        if (opportunities.isEmpty())
            throw new RuntimeException("no repair opportunities found");
        Collections.sort(opportunities, cmp);
        int n = maxTries < opportunities.size() ? maxTries : opportunities.size();
        Opportunity[] opps = select(opportunities, n);
        stack.push(new BacktrackState(automaton, opps));
    }

    protected Opportunity[] select(List<Opportunity> opportunities, int n) {
        List<Opportunity> nonCached = new ArrayList<Opportunity>();
        for (int i = 0; i < n; i++) {
            Opportunity opportunity = opportunities.get(i);
            if (opportunity != null)
                nonCached.add(opportunity);
        }
        return nonCached.toArray(new Opportunity[0]);
    }

    protected String convert(Automaton automaton) {
        try {
            Writer strWriter = new StringWriter();
            writer.write(strWriter, automaton);
            return strWriter.toString();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("unexpected exception", e);
        }
    }

}
