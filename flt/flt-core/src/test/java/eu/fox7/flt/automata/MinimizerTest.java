/*
 * Created on Jun 24, 2007
 * Modified on $Date: 2009-11-12 21:44:56 $
 */
package eu.fox7.flt.automata;

import eu.fox7.flt.automata.converters.GlushkovMinimizer;
import eu.fox7.flt.automata.converters.Minimizer;
import eu.fox7.flt.automata.converters.NFAMinimizer;
import eu.fox7.flt.automata.factories.sparse.Determinizer;
import eu.fox7.flt.automata.factories.sparse.GlushkovFactory;
import eu.fox7.flt.automata.impl.sparse.SparseNFA;
import eu.fox7.flt.automata.io.SimpleReader;
import eu.fox7.flt.automata.io.SparseNFAReader;
import eu.fox7.flt.automata.measures.EquivalenceTest;
import eu.fox7.flt.regex.Glushkov;

import java.io.Reader;
import java.io.StringReader;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * @author eu.fox7
 * @version $Revision: 1.1 $
 * 
 */
public class MinimizerTest extends TestCase {

    public static Test suite() {
        return new TestSuite(MinimizerTest.class);
    }

    public void test_minimizer01() throws Exception {
        SparseNFA nfa1 = new SparseNFA();
        nfa1.setInitialState(Glushkov.INITIAL_STATE);
        nfa1.addTransition("a", Glushkov.INITIAL_STATE, "a_1");
        nfa1.addTransition("b", Glushkov.INITIAL_STATE, "b_1");
        nfa1.addTransition("a", "b_1", "a_2");
        nfa1.addTransition("c", "a_1", "c_1");
        nfa1.addTransition("c", "b_1", "c_2");
        nfa1.addTransition("d", "c_1", "d_1");
        nfa1.addTransition("d", "c_2", "d_1");
        nfa1.addTransition("e", "c_1", "e_1");
        nfa1.addTransition("e", "c_2", "e_1");
        nfa1.addTransition("e", "d_1", "e_2");
        nfa1.addTransition("e", "e_1", "e_1");
        nfa1.addTransition("e", "e_2", "e_2");
        nfa1.addFinalState("a_2");
        nfa1.addFinalState("e_1");
        nfa1.addFinalState("e_2");
        SparseNFA nfa2 = new SparseNFA(nfa1);
        Minimizer minimizer = new GlushkovMinimizer();
        minimizer.minimize(nfa2);
        assertTrue("equiv", EquivalenceTest.areEquivalent(nfa1, nfa2));
        assertEquals("states", 7, nfa2.getNumberOfStates());
        assertEquals("transitions", 9, nfa2.getNumberOfTransitions());
    }

    public void test_minimizer02() throws Exception {
        SparseNFA nfa1 = new SparseNFA();
        nfa1.setInitialState(Glushkov.INITIAL_STATE);
        nfa1.addTransition("a", Glushkov.INITIAL_STATE, "a_1");
        nfa1.addTransition("b", Glushkov.INITIAL_STATE, "b_1");
        nfa1.addTransition("c", "a_1", "c_1");
        nfa1.addTransition("c", "b_1", "c_2");
        nfa1.addTransition("d", "c_1", "d_1");
        nfa1.addTransition("d", "c_2", "d_2");
        nfa1.addTransition("e", "d_1", "e_1");
        nfa1.addTransition("e", "d_2", "e_1");
        nfa1.addTransition("c", "d_1", "c_1");
        nfa1.addTransition("c", "d_2", "c_2");
        nfa1.addFinalState("c_1");
        nfa1.addFinalState("c_2");
        nfa1.addFinalState("e_1");
        SparseNFA nfa2 = new SparseNFA(nfa1);
        Minimizer minimizer = new GlushkovMinimizer();
        minimizer.minimize(nfa2);
        assertTrue("equiv", EquivalenceTest.areEquivalent(nfa1, nfa2));
        assertEquals("states", 6, nfa2.getNumberOfStates());
        assertEquals("transitions", 7, nfa2.getNumberOfTransitions());
    }
    
    public void test_minimizer03() throws Exception {
        SparseNFA nfa1 = new SparseNFA();
        nfa1.setInitialState(Glushkov.INITIAL_STATE);
        nfa1.addTransition("a", Glushkov.INITIAL_STATE, "a_1");
        nfa1.addTransition("b", Glushkov.INITIAL_STATE, "b_1");
        nfa1.addTransition("c", "a_1", "c_1");
        nfa1.addTransition("c", "b_1", "c_2");
        nfa1.addTransition("d", "c_1", "d_1");
        nfa1.addTransition("d", "c_2", "d_2");
        nfa1.addTransition("e", "d_1", "e_1");
        nfa1.addTransition("e", "d_2", "e_1");
        nfa1.addTransition("c", "d_1", "c_1");
        nfa1.addTransition("c", "d_2", "c_2");
        nfa1.addFinalState("c_1");
        nfa1.addFinalState("c_2");
        nfa1.addFinalState("e_1");
        SparseNFA nfa2 = new SparseNFA(nfa1);
        Minimizer minimizer = new NFAMinimizer();
        minimizer.minimize(nfa2);
        assertTrue("equiv", EquivalenceTest.areEquivalent(nfa1, nfa2));
        assertEquals("states", 5, nfa2.getNumberOfStates());
        assertEquals("transitions", 6, nfa2.getNumberOfTransitions());
    }

    public void test_minimizer04() throws Exception {
        SparseNFA nfa1 = new SparseNFA();
        nfa1.setInitialState(Glushkov.INITIAL_STATE);
        nfa1.addTransition("a", Glushkov.INITIAL_STATE, "a_1");
        nfa1.addTransition("a", Glushkov.INITIAL_STATE, "a_2");
        nfa1.addTransition("b", "a_1", "b_1");
        nfa1.addTransition("a", "b_1", "a_3");
        nfa1.addTransition("a", "a_3", "a_3");
        nfa1.addTransition("a", "a_2", "a_4");
        nfa1.addTransition("a", "a_4", "a_4");
        nfa1.addFinalState("a_3");
        nfa1.addFinalState("a_4");
        SparseNFA nfa2 = new SparseNFA(nfa1);
        Minimizer minimizer = new GlushkovMinimizer();
        minimizer.minimize(nfa2);
        assertTrue("equiv", EquivalenceTest.areEquivalent(Determinizer.dfa(nfa1), Determinizer.dfa(nfa2)));
        assertEquals("states", 5, nfa2.getNumberOfStates());
        assertEquals("transitions", 6, nfa2.getNumberOfTransitions());
    }

    public void test_minimizer05() throws Exception {
        final String targetRegexStr = "(. (? (+ (| (i) (c) (d)))) (j) (a) (e) (f) (c) (e) (b) (g) (b) (a) (d) (h))";
        GlushkovFactory glushkov = new GlushkovFactory();
        SparseNFA targetNFA = glushkov.create(targetRegexStr);
        String nfaStr =
            "init-state: q_I\n" +
            "q_I, c -> c_14\n" +
            "c_4, j -> j_0\n" +
            "c_4, i -> i_29\n" +
            "d_27, c -> c_4\n" +
            "i_19, d -> d_27\n" +
            "i_19, i -> i_29\n" +
            "d_7, j -> j_30\n" +
            "e_22, f -> f_23\n" +
            "c_14, j -> j_0\n" +
            "d_27, j -> j_30\n" +
            "i_29, c -> c_14\n" +
            "c_14, d -> d_27\n" +
            "i_29, d -> d_27\n" +
            "b_15, g -> g_6\n" +
            "i_29, j -> j_0\n" +
            "b_25, a -> a_31\n" +
            "j_0, a -> a_1\n" +
            "i_19, c -> c_14\n" +
            "c_4, c -> c_14\n" +
            "d_27, d -> d_27\n" +
            "i_29, i -> i_19\n" +
            "g_6, b -> b_25\n" +
            "c_4, d -> d_7\n" +
            "q_I, d -> d_7\n" +
            "a_1, e -> e_22\n" +
            "c_24, e -> e_2\n" +
            "i_19, j -> j_30\n" +
            "q_I, i -> i_29\n" +
            "a_31, d -> d_37\n" +
            "j_30, a -> a_1\n" +
            "c_14, i -> i_29\n" +
            "c_14, c -> c_14\n" +
            "d_7, c -> c_14\n" +
            "e_2, b -> b_15\n" +
            "q_I, j -> j_30\n" +
            "d_7, d -> d_7\n" +
            "f_23, c -> c_24\n" +
            "d_37, h -> h_18\n" +
            "d_7, i -> i_29\n" +
            "d_27, i -> i_19\n" +
            "final-state: h_18\n";
        Reader strReader = new StringReader(nfaStr);
        SparseNFAReader nfaReader = new SimpleReader(strReader);
        SparseNFA nfa1 = nfaReader.read();
        SparseNFA nfa2 = new SparseNFA(nfa1);
        Minimizer minimizer = new GlushkovMinimizer();
        minimizer.minimize(nfa2);
        assertTrue("equiv", EquivalenceTest.areEquivalent(Determinizer.dfa(nfa1), Determinizer.dfa(nfa2)));
        assertTrue("equiv", EquivalenceTest.areEquivalent(targetNFA, Determinizer.dfa(nfa1)));
        assertTrue("equiv", EquivalenceTest.areEquivalent(targetNFA, Determinizer.dfa(nfa2)));
        assertEquals("states", 16, nfa2.getNumberOfStates());
        assertEquals("transitions", 27, nfa2.getNumberOfTransitions());
        assertEquals("states", 16, targetNFA.getNumberOfStates());
        assertEquals("transitions", 27, targetNFA.getNumberOfTransitions());
    }
    
}
