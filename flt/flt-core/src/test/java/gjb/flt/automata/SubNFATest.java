/*
 * Created on Feb 1, 2006
 * Modified on $Date: 2009-11-12 21:44:56 $
 */
package gjb.flt.automata;

import gjb.flt.automata.factories.sparse.SubNFAFactory;
import gjb.flt.automata.impl.sparse.SparseNFA;
import gjb.flt.automata.measures.EquivalenceTest;
import junit.framework.TestCase;

public class SubNFATest extends TestCase {

    protected SparseNFA nfa;

    public static void main(String[] args) {
        junit.textui.TestRunner.run(SubNFATest.class);
    }

    protected void setUp() throws Exception {
        super.setUp();
        nfa = new SparseNFA();
        nfa.addTransition("a", "q0", "21");
        nfa.addTransition("b", "q0", "q2");
        nfa.addTransition("d", "q1", "q3");
        nfa.addTransition("b", "q2", "q2");
        nfa.addTransition("c", "q2", "q3");
        nfa.addTransition("d", "q3", "q1");
        nfa.addTransition("e", "q3", "q4");
        nfa.setInitialState("q0");
        nfa.addFinalState("q4");
    }

    public void test_sub1() throws Exception {
        SparseNFA targetNFA = new SparseNFA();
        targetNFA.addTransition("d", "q1", "q3");
        targetNFA.addTransition("d", "q3", "q1");
        targetNFA.addTransition("e", "q3", "q4");
        targetNFA.setInitialState("q1");
        targetNFA.addFinalState("q4");
        assertTrue("q1", EquivalenceTest.areEquivalent(new SparseNFA[] {SubNFAFactory.create(nfa, "q1"),
                targetNFA}));
    }

    public void test_sub2() throws Exception {
        SparseNFA targetNFA = new SparseNFA();
        targetNFA.addTransition("d", "q1", "q3");
        targetNFA.addTransition("d", "q3", "q1");
        targetNFA.addTransition("e", "q3", "q4");
        targetNFA.setInitialState("q3");
        targetNFA.addFinalState("q4");
        assertTrue("q3", EquivalenceTest.areEquivalent(new SparseNFA[] {SubNFAFactory.create(nfa, "q3"), targetNFA}));
    }

}
