/*
 * Created on Oct 24, 2006
 * Modified on $Date: 2009-11-05 14:45:15 $
 */
package eu.fox7.flt.schema.infer.ixsd;

import eu.fox7.flt.FLTRuntimeException;
import eu.fox7.flt.automata.NotDFAException;
import eu.fox7.flt.automata.factories.sparse.StateCondition;
import eu.fox7.flt.automata.impl.sparse.State;
import eu.fox7.flt.automata.impl.sparse.StateNFA;
import eu.fox7.flt.automata.impl.sparse.Symbol;
import eu.fox7.flt.automata.measures.EquivalenceTest;
import eu.fox7.flt.automata.measures.Simulator;
import eu.fox7.flt.treeautomata.factories.SupportContextAutomatonFactory;
import eu.fox7.flt.treeautomata.impl.ContentAutomaton;
import eu.fox7.flt.treeautomata.impl.ContextAutomaton;
import eu.fox7.util.AbstractEquivalenceRelation;
import eu.fox7.util.EquivalenceRelation;

import java.util.HashSet;
import java.util.Set;

/**
 * @author eu.fox7
 * @version $Revision: 1.1 $
 * 
 */
public class ContextEquivalenceRelation extends AbstractEquivalenceRelation<State> {

    protected ContextAutomaton nfa;
    protected EquivalenceRelation<ContentAutomaton> modelEquivRel;

    public ContextEquivalenceRelation(ContextAutomaton nfa,
                                      EquivalenceRelation<ContentAutomaton> modelEquivRel) {
        this.nfa = nfa;
        this.modelEquivRel = modelEquivRel;
    }

    public ContextEquivalenceRelation(ContextAutomaton nfa) {
        this(nfa, new DefaultContentEquivalenceRelation());
    }
    
    /* (non-Javadoc)
     * @see eu.fox7.util.AbstractEquivalenceRelation#areEquivalent(java.lang.Object, java.lang.Object)
     */
    @Override
    public boolean areEquivalent(State state1, State state2) {
        try {
            Simulator simulator = new Simulator(new EquivalenceCondition());
            return simulator.simulate(nfa, state1, nfa, state2);
        } catch (NotDFAException e) {
            throw new RuntimeException(e);
        }
    }

    protected static String computeSymbolValue(ContextAutomaton nfa,
                                               State state) {
        return SupportContextAutomatonFactory.stripValue(nfa.getStateValue(state),
                                                         SupportContextAutomatonFactory.DEFAULT_SEPARATION_CHAR);
    }

    protected static class DefaultContentEquivalenceRelation
            extends AbstractEquivalenceRelation<ContentAutomaton> {

        @Override
        public boolean areEquivalent(ContentAutomaton model1, ContentAutomaton model2) {
            try {
	            return EquivalenceTest.areEquivalent(model1, model2);
            } catch (NotDFAException e) {
	            e.printStackTrace();
	            throw new FLTRuntimeException("content models must be unambiguous for this equivalence relation", e);
            }
        }
        
    }

    public class EquivalenceCondition implements StateCondition {

        protected ContextAutomaton nfa1, nfa2;
        protected Set<Symbol> alphabet;

        public void setNFAs(StateNFA nfa1, StateNFA nfa2) {
            this.nfa1 = (ContextAutomaton) nfa1;
            this.nfa2 = (ContextAutomaton) nfa2;
            this.alphabet = new HashSet<Symbol>();
            this.alphabet.addAll(nfa1.getSymbols());
            this.alphabet.addAll(nfa2.getSymbols());
        }

        public boolean satisfy(State fromState1, State fromState2) {
            if (fromState1 == fromState2)
                return true;
            String fromSymbolValue1 = computeSymbolValue(nfa1, fromState1);
            String fromSymbolValue2 = computeSymbolValue(nfa2, fromState2);
            if (!fromSymbolValue1.equals(fromSymbolValue2))
                return false;
            if ((nfa1.isFinalState(fromState1) && !nfa2.isFinalState(fromState2)) ||
                    (!nfa1.isFinalState(fromState1) && nfa2.isFinalState(fromState2)))
                return false;
            ContentAutomaton model1 = nfa1.getAnnotation(fromState1);
            ContentAutomaton model2 = nfa2.getAnnotation(fromState2);
            if (!modelEquivRel.areEquivalent(model2, model1))
                return false;
            return true;
        }

    }

}
