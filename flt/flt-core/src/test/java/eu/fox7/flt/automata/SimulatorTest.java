/*
 * Created on May 11, 2007
 * Modified on $Date: 2009-11-12 21:44:56 $
 */
package eu.fox7.flt.automata;

import eu.fox7.flt.automata.factories.sparse.Determinizer;
import eu.fox7.flt.automata.factories.sparse.EquivalenceCondition;
import eu.fox7.flt.automata.factories.sparse.GlushkovFactory;
import eu.fox7.flt.automata.factories.sparse.InclusionCondition;
import eu.fox7.flt.automata.factories.sparse.ThompsonBuilder;
import eu.fox7.flt.automata.factories.sparse.ThompsonFactory;
import eu.fox7.flt.automata.impl.sparse.SparseNFA;
import eu.fox7.flt.automata.measures.Simulator;
import eu.fox7.flt.regex.Glushkov;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * @author eu.fox7
 * @version $Revision: 1.1 $
 * 
 */
public class SimulatorTest extends TestCase {

    public static Test suite() {
        return new TestSuite(SimulatorTest.class);
    }

    public void test_equivalentNFAs1() throws Exception {
        String regexStr = "(+ (| (a) (. (b) (c)) (. (a) (d))))";
        ThompsonFactory regex = new ThompsonFactory();
        GlushkovFactory glushkov = new GlushkovFactory();
        SparseNFA nfa1 = Determinizer.dfa(regex.create(regexStr));
        SparseNFA nfa2 = Determinizer.dfa(glushkov.create(regexStr));
        Simulator simulator = new Simulator(new EquivalenceCondition());
        assertTrue("equivalent", simulator.simulate(nfa1, nfa2));
    }

    public void test_equivalentNFAs2() throws Exception {
        SparseNFA nfa1 = new SparseNFA();
        nfa1.addTransition("a", "q_I", "a");
        nfa1.addTransition("c", "q_I", "c");
        nfa1.addTransition("a", "a", "a");
        nfa1.addTransition("b", "a", "b");
        nfa1.addTransition("b", "b", "b");
        nfa1.addTransition("c", "b", "c");
        nfa1.setInitialState("q_I");
        nfa1.addFinalState("c");
        
        SparseNFA nfa2 = new SparseNFA();
        nfa2.addTransition("a", "q_I", "#a");
        nfa2.addTransition("c", "q_I", "#c");
        nfa2.addTransition("a", "#a", "aa");
        nfa2.addTransition("b", "#a", "ab");
        nfa2.addTransition("a", "aa", "aa");
        nfa2.addTransition("b", "aa", "ab");
        nfa2.addTransition("b", "ab", "bb");
        nfa2.addTransition("b", "bb", "bb");
        nfa2.addTransition("c", "ab", "bc");
        nfa2.addTransition("c", "bb", "bc");
        nfa2.setInitialState("q_I");
        nfa2.addFinalState("#c");
        nfa2.addFinalState("bc");

        Simulator simulator = new Simulator(new EquivalenceCondition());
        assertTrue("equivalent", simulator.simulate(nfa1, nfa2));
    }

    public void test_equivalentNFAs3() throws Exception {
        String regexStr1 = "(+ (. (a) (b) (c)))";
        String regexStr2 = "(+ (. (a) (b) (c) (a) (b) (c)))";
        ThompsonFactory regex = new ThompsonFactory();
        SparseNFA nfa1 = Determinizer.dfa(regex.create(regexStr1));
        SparseNFA nfa2 = Determinizer.dfa(regex.create(regexStr2));
        Simulator simulator = new Simulator(new EquivalenceCondition());
        assertTrue("not equivalent", !simulator.simulate(nfa1, nfa2));
    }
    
    public void test_equivalentNFAs4() throws Exception {
        String regexStr = "(+ (| (a) (. (b) (c)) (. (a) (d))))";
        ThompsonFactory regex = new ThompsonFactory();
        SparseNFA nfa1 = Determinizer.dfa(regex.create(regexStr));
        Simulator simulator = new Simulator(new EquivalenceCondition());
        assertTrue("equivalent", simulator.simulate(nfa1, nfa1));
    }

    public void test_equivalentNFAs5() throws Exception {
        SparseNFA nfa1 = ThompsonBuilder.emptyNFA();
        SparseNFA nfa2 = ThompsonBuilder.emptyNFA();
        Simulator simulator = new Simulator(new EquivalenceCondition());
        assertTrue("equivalent", simulator.simulate(nfa1, nfa2));
    }

    public void test_equivalentNFAs6() throws Exception {
        SparseNFA nfa1 = new SparseNFA();
        nfa1.setInitialState("q_I");
        nfa1.addFinalState("q_I");
        SparseNFA nfa2 = new SparseNFA();
        nfa2.setInitialState("q_I");
        nfa2.addFinalState("q_I");
        Simulator simulator = new Simulator(new EquivalenceCondition());
        assertTrue("equivalent", simulator.simulate(nfa1, nfa2));
    }

    public void test_equivalentNFAs7() throws Exception {
        SparseNFA nfa = new SparseNFA();
        nfa.setInitialState("q_I");
        nfa.addTransition("a", "q_I", "a1");
        nfa.addTransition("b", "a1", "b1");
        nfa.addTransition("c", "b1", "c1");
        nfa.addTransition("a", "c1", "a2");
        nfa.addTransition("b", "a2", "b2");
        
        nfa.addTransition("c", "b2", "c2");
        nfa.addTransition("a", "c2", "a2");
        nfa.addFinalState("q_I");
        nfa.addFinalState("c1");
        nfa.addFinalState("c2");
        Simulator simulator = new Simulator(new EquivalenceCondition());
        assertTrue("equivalent", simulator.simulate(nfa, nfa.getState("q_I"),
                                                    nfa, nfa.getState("c1")));
    }

    public void test_notEquivalentNFAs() throws Exception {
        String regexStr1 = "(+ (| (a) (. (b) (? (c))) (. (a) (d))))";
        String regexStr2 = "(+ (| (a) (. (b) (c)) (. (a) (d))))";
        ThompsonFactory regex = new ThompsonFactory();
        GlushkovFactory glushkov = new GlushkovFactory();
        SparseNFA nfa1 = Determinizer.dfa(regex.create(regexStr1));
        SparseNFA nfa2 = Determinizer.dfa(glushkov.create(regexStr2));
        Simulator simulator = new Simulator(new EquivalenceCondition());
        assertTrue("not equivalent", !simulator.simulate(nfa1, nfa2));
    }

    public void test_inclusionNFAs1() throws Exception {
        String regexStr1 = "(+ (| (a) (. (b) (c)) (. (a) (d))))";
        String regexStr2 = "(+ (| (a) (. (b) (? (c))) (. (a) (d))))";
        ThompsonFactory regex = new ThompsonFactory();
        GlushkovFactory glushkov = new GlushkovFactory();
        SparseNFA nfa1 = Determinizer.dfa(regex.create(regexStr1));
        SparseNFA nfa2 = Determinizer.dfa(glushkov.create(regexStr2));
        Simulator simulator = new Simulator(new InclusionCondition());
        assertTrue("inclusion", simulator.simulate(nfa1, nfa2));
        assertTrue("not inclusion", !simulator.simulate(nfa2, nfa1));
    }

    public void test_inclusionNFAs2() throws Exception {
        SparseNFA nfa1 = new SparseNFA();
        nfa1.addTransition("a", "q_I", "a");
        nfa1.addTransition("c", "q_I", "c");
        nfa1.addTransition("a", "a", "a");
        nfa1.addTransition("b", "a", "b");
        nfa1.addTransition("b", "b", "b");
        nfa1.addTransition("c", "b", "c");
        nfa1.setInitialState("q_I");
        nfa1.addFinalState("c");
        
        SparseNFA nfa2 = new SparseNFA();
        nfa2.addTransition("a", "q_I", "#a");
        nfa2.addTransition("c", "q_I", "#c");
        nfa2.addTransition("a", "#a", "aa");
        nfa2.addTransition("b", "#a", "ab");
        nfa2.addTransition("a", "aa", "aa");
        nfa2.addTransition("b", "aa", "ab");
        nfa2.addTransition("b", "ab", "bb");
        nfa2.addTransition("b", "bb", "bb");
        nfa2.addTransition("c", "ab", "bc");
        nfa2.addTransition("c", "bb", "bc");
        nfa2.setInitialState("q_I");
        nfa2.addFinalState("#c");
        nfa2.addFinalState("bc");

        Simulator simulator = new Simulator(new InclusionCondition());
        assertTrue("inclusion", simulator.simulate(nfa2, nfa1));
        assertTrue("inclusion", simulator.simulate(nfa1, nfa2));
    }

    public void test_inclusionNFAs3() throws Exception {
        String regexStr1 = "(+ (. (a) (b) (c) (a) (b) (c)))";
        String regexStr2 = "(+ (. (a) (b) (c)))";
        ThompsonFactory regex = new ThompsonFactory();
        SparseNFA nfa1 = Determinizer.dfa(regex.create(regexStr1));
        SparseNFA nfa2 = Determinizer.dfa(regex.create(regexStr2));
        Simulator simulator = new Simulator(new InclusionCondition());
        assertTrue("inclusion", simulator.simulate(nfa1, nfa2));
        assertTrue("not inclusion", !simulator.simulate(nfa2, nfa1));
    }
    
    public void test_inclusionNFAs4() throws Exception {
        String regexStr = "(+ (| (a) (. (b) (c)) (. (a) (d))))";
        ThompsonFactory regex = new ThompsonFactory();
        SparseNFA nfa1 = Determinizer.dfa(regex.create(regexStr));
        Simulator simulator = new Simulator(new InclusionCondition());
        assertTrue("inclusion", simulator.simulate(nfa1, nfa1));
    }

    public void test_inclusionNFAs5() throws Exception {
        SparseNFA nfa1 = ThompsonBuilder.emptyNFA();
        SparseNFA nfa2 = ThompsonBuilder.emptyNFA();
        Simulator simulator = new Simulator(new InclusionCondition());
        assertTrue("inclusion", simulator.simulate(nfa1, nfa2));
        assertTrue("inclusion", simulator.simulate(nfa2, nfa1));
    }

    public void test_inclusionNFAs6() throws Exception {
        SparseNFA nfa1 = ThompsonBuilder.emptyNFA();
        SparseNFA nfa2 = new SparseNFA();
        nfa2.setInitialState("q_I");
        nfa2.addFinalState("q_I");
        Simulator simulator = new Simulator(new InclusionCondition());
        assertTrue("inclusion", simulator.simulate(nfa1, nfa2));
        assertTrue("no inclusion", !simulator.simulate(nfa2, nfa1));
    }
    
    public void test_inclusionNFAs7() throws Exception {
        SparseNFA nfa1 = ThompsonBuilder.emptyNFA();
        String regexStr = "(+ (| (a) (. (b) (c)) (. (a) (d))))";
        ThompsonFactory regex = new ThompsonFactory();
        SparseNFA nfa2 = Determinizer.dfa(regex.create(regexStr));
        Simulator simulator = new Simulator(new InclusionCondition());
        assertTrue("inclusion", simulator.simulate(nfa1, nfa2));
        assertTrue("no inclusion", !simulator.simulate(nfa2, nfa1));
    }
    
    public void test_inclusionNFAs8() throws Exception {
        String regexStr1 = "(. (a) (b))";
        String regexStr2 = "(+ (| (. (a) (b) (? (c))) (d)))";
        ThompsonFactory regex = new ThompsonFactory();
        SparseNFA nfa1 = Determinizer.dfa(regex.create(regexStr1));
        SparseNFA nfa2 = Determinizer.dfa(regex.create(regexStr2));
        Simulator simulator = new Simulator(new InclusionCondition());
        assertTrue("inclusion", simulator.simulate(nfa1, nfa2));
        assertTrue("not inclusion", !simulator.simulate(nfa2, nfa1));
    }

    public void test_inclusionNFAs9() throws Exception {
        SparseNFA nfa1 = new SparseNFA();
        nfa1.setInitialState("q_I");
        nfa1.addTransition("a", "q_I", "a1");
        nfa1.addTransition("b", "a1", "b1");
        nfa1.addTransition("c", "b1", "c1");
        nfa1.addTransition("a", "c1", "a2");
        nfa1.addTransition("b", "a2", "b2");
        nfa1.addTransition("c", "b2", "c2");
        nfa1.addTransition("a", "c2", "a1");
        nfa1.addFinalState("q_I");
        nfa1.addFinalState("a1");
        nfa1.addFinalState("b1");
        nfa1.addFinalState("c1");
        nfa1.addFinalState("a2");
        nfa1.addFinalState("b2");
        nfa1.addFinalState("c2");
        
        SparseNFA nfa2 = new SparseNFA();
        nfa2.setInitialState("q_I");
        nfa2.addTransition("a", "q_I", "a");
        nfa2.addTransition("b", "a", "b");
        nfa2.addTransition("c", "b", "c");
        nfa2.addTransition("a", "c", "a");
        nfa2.addFinalState("q_I");
        nfa2.addFinalState("a");
        nfa2.addFinalState("b");
        nfa2.addFinalState("c");

        Simulator simulator = new Simulator(new InclusionCondition());
        assertTrue("inclusion", simulator.simulate(nfa1, nfa2));
    }

    public void test_noInclusionNFAs() throws Exception {
        SparseNFA nfa1 = new SparseNFA();
        nfa1.setInitialState("q_I");
        nfa1.addFinalState("q_I");
        String regexStr = "(+ (| (a) (. (b) (c)) (. (a) (d))))";
        ThompsonFactory regex = new ThompsonFactory();
        SparseNFA nfa2 = Determinizer.dfa(regex.create(regexStr));
        Simulator simulator = new Simulator(new InclusionCondition());
        assertTrue("no inclusion", !simulator.simulate(nfa1, nfa2));
        assertTrue("no inclusion", !simulator.simulate(nfa2, nfa1));
    }

    public void test_selfEquivalence1() throws Exception {
        SparseNFA nfa = new SparseNFA();
        nfa.setInitialState(Glushkov.INITIAL_STATE);
        nfa.addTransition("a", Glushkov.INITIAL_STATE, "a_1");
        nfa.addTransition("b", Glushkov.INITIAL_STATE, "b_1");
        nfa.addTransition("a", "b_1", "a_2");
        nfa.addTransition("c", "a_1", "c_1");
        nfa.addTransition("c", "b_1", "c_2");
        nfa.addTransition("d", "c_1", "d_1");
        nfa.addTransition("d", "c_2", "d_1");
        nfa.addTransition("e", "c_1", "e_1");
        nfa.addTransition("e", "c_2", "e_1");
        nfa.addTransition("e", "d_1", "e_2");
        nfa.addTransition("e", "e_1", "e_1");
        nfa.addTransition("e", "e_2", "e_2");
        nfa.addFinalState("a_2");
        nfa.addFinalState("e_1");
        nfa.addFinalState("e_2");
        Simulator simulator = new Simulator(new EquivalenceCondition());
        assertTrue("c_1 == c_2", simulator.simulate(nfa, nfa.getState("c_1"),
                                                    nfa, nfa.getState("c_2")));
        assertTrue("c_1 == c_2", simulator.simulate(nfa, nfa.getState("e_1"),
                                                    nfa, nfa.getState("e_2")));
        assertTrue("c_1 == c_2", !simulator.simulate(nfa, nfa.getState("a_1"),
                                                     nfa, nfa.getState("b_1")));
        assertTrue("c_1 == c_2", !simulator.simulate(nfa, nfa.getState("a_1"),
                                                     nfa, nfa.getState("a_2")));
        assertTrue("c_1 == c_2", !simulator.simulate(nfa, nfa.getState("d_1"),
                                                     nfa, nfa.getState("c_1")));
    }

    public void test_selfEquivalence2() throws Exception {
        String regexStr = "(+ (| (a) (. (b) (c)) (. (a) (d))))";
        ThompsonFactory regex = new ThompsonFactory();
        SparseNFA nfa = Determinizer.dfa(regex.create(regexStr));
        Simulator simulator = new Simulator(new InclusionCondition());
        assertTrue("no inclusion", simulator.simulate(nfa, nfa));
    }

}
