/*
 * Created on Nov 13, 2006
 * Modified on $Date: 2009-11-12 21:44:56 $
 */
package gjb.flt.automata;

import gjb.flt.automata.impl.sparse.SparseNFA;
import gjb.flt.automata.io.SparseNFAReader;
import gjb.flt.automata.io.NFAWriter;
import gjb.flt.automata.io.SimpleReader;
import gjb.flt.automata.io.SimpleWriter;
import gjb.flt.automata.measures.EquivalenceTest;

import java.io.StringReader;
import java.io.StringWriter;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * @author gjb
 * @version $Revision: 1.1 $
 * 
 */
public class SimpleReadWriteTest extends TestCase {

    public static Test suite() {
        return new TestSuite(SimpleReadWriteTest.class);
    }

    public void test_readWrite1() throws Exception {
        SparseNFA nfa = new SparseNFA();
        nfa.setInitialState("q_I");
        nfa.addTransition("a", "q_I", "a");
        nfa.addTransition("b", "q_I", "b");
        nfa.addTransition("b", "a", "b");
        nfa.addTransition("c", "b", "c");
        nfa.addTransition("a", "c", "a");
        nfa.addFinalState("c");
        StringWriter writer = new StringWriter();
        NFAWriter nfaWriter = new SimpleWriter(writer);
        nfaWriter.write(nfa);
        StringReader reader = new StringReader(writer.toString());
        SparseNFAReader nfaReader = new SimpleReader(reader);
        SparseNFA newNFA = nfaReader.read();
        assertTrue("equiv", EquivalenceTest.areEquivalent(new SparseNFA[] {nfa, newNFA}));
    }

    public void test_readWrite2() throws Exception {
        SparseNFA nfa = new SparseNFA();
        nfa.setInitialState("q_I");
        nfa.addTransition("a", "q_I", "a");
        nfa.addTransition("b", "q_I", "b");
        nfa.addTransition("b", "a", "b");
        nfa.addTransition("c", "b", "c");
        nfa.addTransition("a", "c", "a");
        nfa.addFinalState("c");
        String representation =
            "init-state: alpha\n" +
            "alpha, a -> beta\n" +
            "alpha, b -> gamma\n" +
            "beta, b -> gamma\n" +
            "gamma, c -> delta\n" +
            "delta, a -> beta\n" +
            "final-state: delta\n";
        StringReader reader = new StringReader(representation);
        SparseNFAReader nfaReader = new SimpleReader(reader);
        SparseNFA newNFA = nfaReader.read();
        assertTrue("equiv", EquivalenceTest.areEquivalent(new SparseNFA[] {nfa, newNFA}));
    }

}
