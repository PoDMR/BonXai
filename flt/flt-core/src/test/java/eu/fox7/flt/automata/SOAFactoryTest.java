/*
 * Created on Feb 8, 2006
 * Modified on $Date: 2009-11-12 21:44:56 $
 */
package eu.fox7.flt.automata;

import eu.fox7.flt.automata.factories.sparse.AnnotatedSOAFactory;
import eu.fox7.flt.automata.factories.sparse.GlushkovFactory;
import eu.fox7.flt.automata.impl.sparse.AnnotatedSparseNFA;
import eu.fox7.flt.automata.impl.sparse.SparseNFA;
import eu.fox7.flt.automata.measures.EquivalenceTest;

import java.io.StringReader;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class SOAFactoryTest extends TestCase {

    public static void main(String[] args) {
        junit.textui.TestRunner.run(SOAFactoryTest.class);
    }

    public static Test suite() {
        return new TestSuite(SOAFactoryTest.class);
    }

    public void test_soaFactory() throws Exception {
        String examples =
            "a b c\n" +
            "b b c\n" +
            "a b b b b b c\n";
        String regexStr = "(. (? (a)) (+ (b)) (c))";
        AnnotatedSOAFactory factory = new AnnotatedSOAFactory();
        factory.add(new StringReader(examples));
        AnnotatedSparseNFA<Integer,Integer> nfa1 = factory.getAutomaton();
        GlushkovFactory glushkov = new GlushkovFactory();
        SparseNFA nfa2 = glushkov.create(regexStr);
        assertTrue("SOA factory", EquivalenceTest.areEquivalent(new SparseNFA[] {nfa1, nfa2}));
        assertEquals("q_I -> a",
                     new Integer(2),
                     nfa1.getAnnotation("a", "q_I", "a"));
        assertEquals("q_I -> b",
                     new Integer(1),
                     nfa1.getAnnotation("b", "q_I", "b"));
        assertEquals("a -> b",
                     new Integer(2),
                     nfa1.getAnnotation("b", "a", "b"));
        assertEquals("b -> b",
                     new Integer(5),
                     nfa1.getAnnotation("b", "b", "b"));
        assertEquals("b -> c",
                     new Integer(3),
                     nfa1.getAnnotation("c", "b", "c"));
    }

}
