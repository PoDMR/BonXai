/*
 * Created on Dec 22, 2005
 * Modified on $Date: 2009-11-12 21:44:56 $
 */
package eu.fox7.flt.automata;

import eu.fox7.flt.automata.impl.sparse.AnnotatedSparseNFA;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class AnnotatedNFATest extends TestCase {

    public static void main(String[] args) {
        junit.textui.TestRunner.run(AnnotatedNFATest.class);
    }

    public static Test suite() {
        return new TestSuite(AnnotatedNFATest.class);
    }

    public void test_annotation() throws Exception {
        AnnotatedSparseNFA<String,Integer> nfa = new AnnotatedSparseNFA<String,Integer>();
        nfa.addTransition("a", "q0", "q1");
        nfa.addTransition("b", "q1", "q1");
        nfa.addTransition("c", "q1", "q2");
        nfa.addTransition("c", "q2", "q1");
        nfa.setInitialState("q0");
        nfa.setFinalState("q2");
        nfa.annotate("q0", "initial state");
        nfa.annotate("q2", "final state");
        nfa.annotate("a", "q0", "q1", 10);
        nfa.annotate("b", "q1", "q1", 3);
        nfa.annotate("c", "q2", "q1", 7);
        assertTrue("state q0", nfa.hasAnnotation("q0"));
        assertTrue("state q1", !nfa.hasAnnotation("q1"));
        assertTrue("state q2", nfa.hasAnnotation("q2"));
        assertTrue("transition a", nfa.hasAnnotation("a", "q0", "q1"));
        assertTrue("transition b", nfa.hasAnnotation("b", "q1", "q1"));
        assertTrue("transition c1", !nfa.hasAnnotation("c", "q1", "q2"));
        assertTrue("transition c2", nfa.hasAnnotation("c", "q2", "q1"));
        assertEquals("q0 ann", "initial state", nfa.getAnnotation("q0"));
        assertEquals("q2 ann", "final state", nfa.getAnnotation("q2"));
        assertEquals("a ann", new Integer(10), nfa.getAnnotation("a", "q0", "q1"));
        assertEquals("b ann", new Integer(3), nfa.getAnnotation("b", "q1", "q1"));
        assertEquals("c2 ann", new Integer(7), nfa.getAnnotation("c", "q2", "q1"));
    }

}
