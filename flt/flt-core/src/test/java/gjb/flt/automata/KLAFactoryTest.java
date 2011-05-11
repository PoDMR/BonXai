/*
 * Created on Feb 13, 2006
 * Modified on $Date: 2009-11-12 21:44:56 $
 */
package gjb.flt.automata;

import gjb.flt.automata.factories.sparse.AnnotatedPSAFactory;
import gjb.flt.automata.factories.sparse.AnnotatedSOAFactory;
import gjb.flt.automata.factories.sparse.KLAFactory;
import gjb.flt.automata.impl.sparse.AnnotatedSparseNFA;
import gjb.flt.automata.impl.sparse.SparseNFA;
import gjb.flt.automata.matchers.NFAMatcher;
import gjb.flt.automata.measures.EquivalenceTest;
import gjb.flt.regex.Glushkov;

import java.io.StringReader;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class KLAFactoryTest extends TestCase {

    public static void main(String[] args) {
        junit.textui.TestRunner.run(KLAFactoryTest.class);
    }

    public static Test suite() {
        return new TestSuite(KLAFactoryTest.class);
    }

    public void test_1LA_SOA() throws Exception {
        String example =
            "a a a b c\n" +
            "a b b c\n" +
            "c\n";
        KLAFactory factory = new KLAFactory(1);
        factory.add(new StringReader(example));
        AnnotatedSparseNFA<Integer,Integer> nfa = factory.getAutomaton();
        AnnotatedSOAFactory targetFactory = new AnnotatedSOAFactory();
        targetFactory.add(new StringReader(example));
        AnnotatedSparseNFA<Integer,Integer> targetNFA = targetFactory.getAutomaton();
        assertTrue("1LA === SOA", EquivalenceTest.areEquivalent(new SparseNFA[] {targetNFA, nfa}));
    }

    public void test_1LA_2LA() throws Exception {
        String example =
            "a a a b c\n" +
            "a b b c\n" +
            "c\n";
        KLAFactory factory = new KLAFactory(1);
        factory.add(new StringReader(example));
        AnnotatedSparseNFA<Integer,Integer> nfa = factory.getAutomaton();
        KLAFactory targetFactory = new KLAFactory(2);
        targetFactory.add(new StringReader(example));
        AnnotatedSparseNFA<Integer,Integer> targetNFA = targetFactory.getAutomaton();
        assertTrue("1LA =!= 2LA", !EquivalenceTest.areEquivalent(new SparseNFA[] {targetNFA, nfa}));
    }

    public void test_2LA() throws Exception {
        String example =
            "a a a b c\n" +
            "a b b c\n" +
            "c\n";
        KLAFactory factory = new KLAFactory(2);
        factory.add(new StringReader(example));
        AnnotatedSparseNFA<Integer,Integer> nfa = factory.getAutomaton();
        assertEquals("nr of states", 7, nfa.getNumberOfStates());
        assertEquals("initial state",
                     Glushkov.INITIAL_STATE,
                     nfa.getStateValue(nfa.getInitialState()));
        assertEquals("nr of final states", 2, nfa.getFinalStates().size());
        assertTrue("b-c final", nfa.isFinalState("b-c"));
        assertTrue("#-c final", nfa.isFinalState("#-c"));
        assertTrue("q_I -> #-a",
                   nfa.hasTransition("a", Glushkov.INITIAL_STATE, "#-a"));
        assertTrue("q_I -> #-c",
                   nfa.hasTransition("c", Glushkov.INITIAL_STATE, "#-c"));
        assertTrue("#-a -> a-a",
                   nfa.hasTransition("a", "#-a", "a-a"));
        assertTrue("a-a -> a-a",
                   nfa.hasTransition("a", "a-a", "a-a"));
        assertTrue("a-a -> a-b",
                   nfa.hasTransition("b", "a-a", "a-b"));
        assertTrue("a-b -> b-b",
                   nfa.hasTransition("b", "a-b", "b-b"));
        assertTrue("a-b -> b-c",
                   nfa.hasTransition("c", "a-b", "b-c"));
        assertTrue("b-b -> b-c",
                   nfa.hasTransition("c", "b-b", "b-c"));
        String[][] positive = {
                {"a", "b", "c"},
                {"a", "a", "a", "a", "a", "b", "c"},
                {"a", "a", "b", "c"}
        };
        String[][] negative = {
                {"a", "a", "b", "b", "b", "c"},
                {"c", "c"}
        };
        NFAMatcher matcher = new NFAMatcher(nfa);
        for (int i = 0; i < positive.length; i++)
            assertTrue("running pos. example " + (i + 1),
                       matcher.matches(positive[i]));
        for (int i = 0; i < negative.length; i++)
            assertTrue("running neg. example " + (i + 1),
                       !matcher.matches(negative[i]));
    }

    public void test_2LA_3LA() throws Exception {
        String example =
            "a a a b c\n" +
            "a b b c\n" +
            "c\n";
        KLAFactory factory = new KLAFactory(2);
        factory.add(new StringReader(example));
        AnnotatedSparseNFA<Integer,Integer> nfa = factory.getAutomaton();
        KLAFactory targetFactory = new KLAFactory(3);
        targetFactory.add(new StringReader(example));
        AnnotatedSparseNFA<Integer,Integer> targetNFA = targetFactory.getAutomaton();
        assertTrue("3LA =!= 2LA", !EquivalenceTest.areEquivalent(new SparseNFA[] {targetNFA, nfa}));
    }

    public void test_3LA_PSA() throws Exception {
        String example =
            "a a a b c\n" +
            "a b b c\n" +
            "c\n";
        KLAFactory factory = new KLAFactory(3);
        factory.add(new StringReader(example));
        AnnotatedSparseNFA<Integer,Integer> nfa = factory.getAutomaton();
        AnnotatedPSAFactory targetFactory = new AnnotatedPSAFactory();
        targetFactory.add(new StringReader(example));
        AnnotatedSparseNFA<Integer,Integer> targetNFA = targetFactory.getAutomaton();
        assertTrue("3LA === PSA", EquivalenceTest.areEquivalent(new SparseNFA[] {targetNFA, nfa}));
    }

    public void test_3LA() throws Exception {
        String example =
            "a a a b c\n" +
            "a b b c\n" +
            "c\n";
        KLAFactory factory = new KLAFactory(3);
        factory.add(new StringReader(example));
        AnnotatedSparseNFA<Integer,Integer> nfa = factory.getAutomaton();
        assertEquals("nr of states", 10, nfa.getNumberOfStates());
        assertEquals("initial state",
                     Glushkov.INITIAL_STATE,
                     nfa.getStateValue(nfa.getInitialState()));
        assertEquals("nr of final states", 3, nfa.getFinalStates().size());
        assertTrue("a-b-c final", nfa.isFinalState("a-b-c"));
        assertTrue("b-b-c final", nfa.isFinalState("b-b-c"));
        assertTrue("#-#-c final", nfa.isFinalState("#-#-c"));
        assertTrue("q_I -> #-#-a",
                   nfa.hasTransition("a", Glushkov.INITIAL_STATE, "#-#-a"));
        assertTrue("q_I -> #-#-c",
                   nfa.hasTransition("c", Glushkov.INITIAL_STATE, "#-#-c"));
        assertTrue("#-#-a -> #-a-a",
                   nfa.hasTransition("a", "#-#-a", "#-a-a"));
        assertTrue("#-#-a -> #-a-b",
                   nfa.hasTransition("b", "#-#-a", "#-a-b"));
        assertTrue("#-a-a -> a-a-a",
                   nfa.hasTransition("a", "#-a-a", "a-a-a"));
        assertTrue("a-a-a -> a-a-b",
                   nfa.hasTransition("b", "a-a-a", "a-a-b"));
        String[][] positive = {
                {"a", "a", "a", "b", "c"},
                {"a", "b", "b", "c"},
                {"c"}
        };
        String[][] negative = {
                {"a", "b", "c"},
                {"a", "a", "a", "a", "a", "b", "c"},
                {"a", "a", "b", "c"},
                {"a", "a", "b", "b", "b", "c"},
                {"c", "c"}
        };
		NFAMatcher matcher = new NFAMatcher(nfa);
        for (int i = 0; i < positive.length; i++)
            assertTrue("running pos. example " + (i + 1),
                       matcher.matches(positive[i]));
        for (int i = 0; i < negative.length; i++)
            assertTrue("running neg. example " + (i + 1),
                       !matcher.matches(negative[i]));
    }

    public void test_3LA_4LA() throws Exception {
        String example =
            "a a a b c\n" +
            "a b b c\n" +
            "c\n";
        KLAFactory factory = new KLAFactory(3);
        factory.add(new StringReader(example));
        AnnotatedSparseNFA<Integer,Integer> nfa = factory.getAutomaton();
        KLAFactory targetFactory = new KLAFactory(4);
        targetFactory.add(new StringReader(example));
        AnnotatedSparseNFA<Integer,Integer> targetNFA = targetFactory.getAutomaton();
        assertTrue("3LA === 4LA", EquivalenceTest.areEquivalent(new SparseNFA[] {targetNFA, nfa}));
    }

}
