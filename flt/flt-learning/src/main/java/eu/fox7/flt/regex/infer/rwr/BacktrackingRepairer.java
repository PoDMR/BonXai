/*
 * Created on Sep 11, 2008
 * Modified on $Date: 2009-10-27 09:30:13 $
 */
package eu.fox7.flt.regex.infer.rwr;

import eu.fox7.flt.automata.io.NFAWriteException;
import eu.fox7.flt.regex.infer.rwr.impl.Automaton;
import eu.fox7.flt.regex.infer.rwr.impl.GraphAutomatonConverter;
import eu.fox7.flt.regex.infer.rwr.impl.GraphAutomatonFactory;
import eu.fox7.flt.regex.infer.rwr.impl.Opportunity;
import eu.fox7.flt.regex.infer.rwr.impl.OpportunityComparator;
import eu.fox7.flt.regex.infer.rwr.impl.OpportunityFilter;
import eu.fox7.flt.regex.infer.rwr.measures.LanguageSizeMeasure;
import eu.fox7.flt.regex.infer.rwr.measures.OpportunityMeasure;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

import org.apache.commons.lang.StringUtils;

/**
 * @author eu.fox7
 * @version $Revision: 1.3 $
 * 
 */
public class BacktrackingRepairer extends FixedOrderRepairer {

    protected OpportunityMeasure measure;
    protected OpportunityComparator cmp = new OpportunityComparator();
    protected int maxTries = 5;
    protected GraphAutomatonFactory factory = new GraphAutomatonFactory();
    protected OpportunityFilter filter = new DefaultOpportunityFilter();
    protected boolean isVerbose = false;
    protected GraphAutomatonConverter converter = null;

    public BacktrackingRepairer(OpportunityMeasure measure, int maxTries) {
        super();
        this.measure = measure;
        this.maxTries = maxTries;
    }

    public int getMaxTries() {
        return maxTries;
    }

    public void setMaxTries(int maxTries) {
        this.maxTries = maxTries;
    }

    public OpportunityFilter getFilter() {
        return filter;
    }

    public void setFilter(OpportunityFilter filter) {
        this.filter = filter;
    }

    public boolean isVerbose() {
		return isVerbose;
	}

	public void setVerbose(boolean isVerbose) {
		this.isVerbose = isVerbose;
		if (isVerbose)
			converter = new GraphAutomatonConverter();
		else
			converter = null;
	}

	public Automaton rewrite(Automaton automaton) {
        automaton = rewriter.rewrite(automaton);
        if (automaton.isReduced())
            return automaton;
        if (isVerbose()) {
        	try {
				System.err.println(converter.convertToDot(automaton));
				System.err.println("\n\n");
			} catch (NFAWriteException e) {
				throw new RuntimeException(e);
			}
        }
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
            automaton = stack.peek().getAutomaton();
            AutomatonRewriter repairer = opp.getRepairer();
            AutomatonRewriter oppRewriter = opp.getRewriter();
            Automaton newAutomaton = repairer.rewrite(automaton, opp.getIndices());
            newAutomaton = oppRewriter.rewrite(newAutomaton, opp.getIndices());
            newAutomaton = rewriter.rewrite(newAutomaton);
            if (isVerbose()) {
            	try {
					System.err.println(opp.toString());
					System.err.println(converter.convertToDot(newAutomaton));
					System.err.println();
					System.err.println();
				} catch (NFAWriteException e) {
					throw new RuntimeException(e);
				}
            }
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

    protected void updateStack(Stack<BacktrackState> stack, Automaton automaton) {
        List<Opportunity> opportunities = new ArrayList<Opportunity>();
        for (int i = 0; i < repairFinders.length; i++) {
            List<int[]> indicesList = repairFinders[i].getAll(automaton);
            for (int[] indices : indicesList) {
                Automaton newAutomaton = repairers[i].rewrite(automaton, indices);
                Opportunity opportunity = measure.measure(automaton,
                                                          factory.expand(newAutomaton),
                                                          repairers[i],
                                                          rewriters[i],
                                                          indices);
                if (filter.isPassed(opportunity, newAutomaton))
                    opportunities.add(opportunity);
            }
        }
        if (opportunities.isEmpty())
            throw new RuntimeException("no repair opportunities found");
        Collections.sort(opportunities, cmp);
        int n = maxTries < opportunities.size() ? maxTries : opportunities.size();
        Opportunity[] opps = opportunities.subList(0, n).toArray(new Opportunity[0]);
        stack.push(new BacktrackState(automaton, opps));
    }

    protected static class BacktrackState {
 
        protected Automaton automaton;
        protected Opportunity[] opportunities;
        protected int index;

        public BacktrackState(Automaton automaton, Opportunity[] opportunities) {
            this.automaton = automaton;
            this.opportunities = opportunities;
            this.index = 0;
        }

        public boolean hasNext() {
            return index < opportunities.length;
        }

        public double getMeasure() {
            return opportunities[index].getMeasure();
        }

        public Opportunity getNext() {
            return opportunities[index++];
        }

        public Automaton getAutomaton() {
            return automaton;
        }

        @Override
        public String toString() {
            return opportunities.length + " opportunities: " +
                "[" + StringUtils.join(opportunities, ", ") + "]" +
                ", index " + index + " for " + automaton.toString();
        }

    }

    public static class DefaultOpportunityFilter implements OpportunityFilter {

        public boolean isPassed(Opportunity opportunity, Automaton automaton) {
            return true;
        }
        
    }

}
