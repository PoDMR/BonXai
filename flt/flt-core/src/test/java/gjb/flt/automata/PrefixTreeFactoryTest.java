/*
 * Created on Oct 10, 2006
 * Modified on $Date: 2009-11-12 21:44:56 $
 */
package gjb.flt.automata;

import gjb.flt.automata.NotDFAException;
import gjb.flt.automata.factories.sparse.PrefixTreeFactory;
import gjb.flt.automata.impl.sparse.SparseNFA;
import gjb.flt.automata.measures.EquivalenceTest;
import gjb.flt.regex.Glushkov;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * @author gjb
 * @version $Revision: 1.1 $
 * 
 */
public class PrefixTreeFactoryTest extends TestCase {

    public static void main(String[] args) {
        junit.textui.TestRunner.run(PrefixTreeFactoryTest.class);
    }

    public static Test suite() {
        return new TestSuite(PrefixTreeFactoryTest.class);
    }

    public void test_prefixTree() {
        String[][] examples = {
                {"a", "b", "c"}, {"a", "d", "c"}, {"b", "c"}, {"a", "b", "d"},
                {"a", "b"}, {"b", "b"}
        };
        SparseNFA targetNFA = new SparseNFA();
        targetNFA.setInitialState(Glushkov.INITIAL_STATE);
        targetNFA.addTransition("a", Glushkov.INITIAL_STATE, "a_1");
        targetNFA.addTransition("b", Glushkov.INITIAL_STATE, "b_2");
        targetNFA.addTransition("b", "a_1", "b_1");
        targetNFA.addTransition("d", "a_1", "d_1");
        targetNFA.addTransition("c", "b_1", "c_1");
        targetNFA.addTransition("d", "b_1", "d_2");
        targetNFA.addTransition("c", "d_1", "c_2");
        targetNFA.addTransition("c", "b_2", "c_3");
        targetNFA.addTransition("b", "b_2", "b_3");
        targetNFA.addFinalState("b_1");
        targetNFA.addFinalState("b_3");
        targetNFA.addFinalState("c_1");
        targetNFA.addFinalState("c_2");
        targetNFA.addFinalState("c_3");
        targetNFA.addFinalState("d_2");
        PrefixTreeFactory factory = new PrefixTreeFactory();
        for (int i = 0; i < examples.length; i++)
            factory.add(examples[i]);
        SparseNFA nfa = factory.getAutomaton();
        try {
            assertTrue("prefix tree automaton", EquivalenceTest.areEquivalent(new SparseNFA[] {nfa, targetNFA}));
        } catch (NotDFAException e) {
            e.printStackTrace();
            fail("both automata should be DFAs");
        }
    }

}
