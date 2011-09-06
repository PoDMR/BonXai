/*
 * Created on Feb 17, 2006
 * Modified on $Date: 2009-11-12 21:44:56 $
 */
package eu.fox7.flt.automata;

import eu.fox7.flt.automata.NotDFAException;
import eu.fox7.flt.automata.factories.sparse.AnnotatedSOAFactory;
import eu.fox7.flt.automata.factories.sparse.KLAFactory;
import eu.fox7.flt.automata.factories.sparse.StateMerger;
import eu.fox7.flt.automata.factories.sparse.ThompsonBuilder;
import eu.fox7.flt.automata.impl.sparse.AnnotatedSparseNFA;
import eu.fox7.flt.automata.impl.sparse.AnnotatedStateNFA;
import eu.fox7.flt.automata.impl.sparse.SparseNFA;
import eu.fox7.flt.automata.impl.sparse.State;
import eu.fox7.flt.automata.impl.sparse.StateNFA;
import eu.fox7.flt.automata.measures.EquivalenceTest;
import eu.fox7.flt.automata.misc.PrefixStringRelation;
import eu.fox7.flt.regex.Glushkov;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class PrefixStringRelationTest extends TestCase {

    protected static final String PADDING = KLAFactory.DEFAULT_PADDING_CHAR;
    protected SparseNFA nfa, psa;

    public static void main(String[] args) {
        junit.textui.TestRunner.run(PrefixStringRelationTest.class);
    }

    public static Test suite() {
        return new TestSuite(PrefixStringRelationTest.class);
    }

    protected void setUp() throws Exception {
        nfa = new SparseNFA();
        nfa.addTransition("a", "q0", "q1");
        nfa.addTransition("b", "q0", "q1");
        nfa.addTransition("a", "q0", "q2");
        nfa.addTransition("c", "q1", "q3");
        nfa.addTransition("d", "q2", "q2");
        nfa.addTransition("d", "q2", "q3");
        nfa.setInitialState("q0");
        nfa.addFinalState("q3");
        
        psa = new SparseNFA();
        psa.addTransition("a", Glushkov.INITIAL_STATE, "a1");
        psa.addTransition("c", Glushkov.INITIAL_STATE, "c3");
        psa.addTransition("a", "a1", "a2");
        psa.addTransition("a", "a2", "a3");
        psa.addTransition("b", "a3", "b1");
        psa.addTransition("c", "b1", "c1");
        psa.addTransition("b", "a1", "b2");
        psa.addTransition("b", "b2", "b3");
        psa.addTransition("c", "b3", "c2");
        psa.setInitialState(Glushkov.INITIAL_STATE);
        psa.addFinalState("c1");
        psa.addFinalState("c2");
        psa.addFinalState("c3");
    }

    public void test_computePrefixLanguage1() {
        Map<String,String[][]> target = new HashMap<String,String[][]>();
        target.put("q0", new String[][] {{PADDING}});
        target.put("q1", new String[][] {{"a"}, {"b"}});
        target.put("q2", new String[][] {{"a"}, {"d"}});
        target.put("q3", new String[][] {{"c"}, {"d"}});
        PrefixStringRelation relation = new PrefixStringRelation(nfa, 1);
        Set<String[]> language;
        Set<String[]> targetLanguage;
        for (String stateValue : target.keySet()) {
            language = relation.getPrefixStrings(nfa.getState(stateValue));
            targetLanguage = computeTarget(target.get(stateValue));
            assertEquals(stateValue + " prefix 1",
                         PrefixStringRelation.convert(targetLanguage), PrefixStringRelation.convert(language));
        }
    }

    public void test_computePrefixLanguage2() {
        Map<String,String[][]> target = new HashMap<String,String[][]>();
        target.put("q0", new String[][] {{PADDING, PADDING}});
        target.put("q1", new String[][] {{PADDING, "a"},
                                         {PADDING, "b"}});
        target.put("q2", new String[][] {{"a", "d"},
                                         {"d", "d"},
                                         {PADDING, "a"}});
        target.put("q3", new String[][] {{"a", "c"}, {"b", "c"},
                                         {"d", "d"}, {"a", "d"}});
        PrefixStringRelation relation = new PrefixStringRelation(nfa, 2);
        Set<String[]> language;
        Set<String[]> targetLanguage;
        for (String stateValue : target.keySet()) {
            language = relation.getPrefixStrings(nfa.getState(stateValue));
            targetLanguage = computeTarget(target.get(stateValue));
            assertEquals(stateValue + " prefix 2",
                         PrefixStringRelation.convert(targetLanguage), PrefixStringRelation.convert(language));
        }
    }

    public void test_computePrefixLanguage3() {
        Map<String,String[][]> target = new HashMap<String,String[][]>();
        target.put("q0", new String[][] {{PADDING, PADDING, PADDING}});
        target.put("q1", new String[][] {{PADDING, PADDING, "a"},
                                         {PADDING, PADDING, "b"}});
        target.put("q2", new String[][] {{PADDING, "a", "d"},
                                         {"a", "d", "d"},
                                         {"d", "d", "d"},
                                         {PADDING, PADDING, "a"}});
        target.put("q3", new String[][] {{PADDING, "a", "c"},
                                         {PADDING, "b", "c"},
                                         {"a", "d", "d"}, {"d", "d", "d"},
                                         {PADDING, "a", "d"}});
        PrefixStringRelation relation = new PrefixStringRelation(nfa, 3);
        Set<String[]> language;
        Set<String[]> targetLanguage;
        for (String stateValue : target.keySet()) {
            language = relation.getPrefixStrings(nfa.getState(stateValue));
            targetLanguage = computeTarget(target.get(stateValue));
            assertEquals(stateValue + " prefix 2",
                         PrefixStringRelation.convert(targetLanguage), PrefixStringRelation.convert(language));
        }
    }

    public void test_equivalenceRelation1() {
        String[][] equiv = {
                {"a1", "a2", "a3"},
                {"b1", "b2", "b3"},
                {"c1", "c2", "c3"}
        };
        PrefixStringRelation relation = new PrefixStringRelation(psa, 1);
        for (int i = 0; i < equiv.length; i++)
            for (int j = 0; j < equiv[i].length; j++)
                for (int k = 0; k < equiv[i].length; k++)
                    assertTrue(equiv[i][j] + " === " + equiv[i][k],
                               relation.areEquivalent(psa.getState(equiv[i][j]),
                                                      psa.getState(equiv[i][k])));
        for (int i = 0; i < equiv.length; i++)
            for (int j = 0; j < equiv.length; j++) {
                if (i == j) continue;
                for (int k = 0; k < equiv[i].length; k++)
                    for (int l = 0; l < equiv[j].length; l++)
                        assertTrue(equiv[i][k] + " =!= " + equiv[j][l],
                                   !relation.areEquivalent(psa.getState(equiv[i][k]),
                                                           psa.getState(equiv[j][l])));
            }
        for (int i = 0; i < equiv.length; i++)
            for (int j = 0; j < equiv[i].length; j++)
                assertTrue(Glushkov.INITIAL_STATE + " =!= " + equiv[i][j],
                           !relation.areEquivalent(psa.getInitialState(),
                                                   psa.getState(equiv[i][j])));
        Set<Set<State>> classes = relation.getClasses(psa.getStates());
        assertEquals("nr of classes", 4, classes.size());
    }

    public void test_equivalenceRelation2() {
        PrefixStringRelation relation = new PrefixStringRelation(psa, 2);
        Set<Set<State>> classes = relation.getClasses(psa.getStates());
        assertEquals("nr of classes", 7, classes.size());
        String[][] classStrings = {
                {Glushkov.INITIAL_STATE}, {"a1"}, {"b3"}, {"c3"},
                {"a2", "a3"}, {"b1", "b2"}, {"c1", "c2"}
        };
        for (int i = 0; i < classStrings.length; i++) {
            Set<State> stateSet = computeClass(psa, classStrings[i]);
            assertTrue("class " + stateSet.toString(),
                       classes.contains(stateSet));
        }
        String[][] noClassStrings = {
                {Glushkov.INITIAL_STATE, "a1"}, {"b3", "c3"},
                {"a2"}, {"a3"}, {"b1"}, {"b2"}, {"c1"}, {"c2"}
        };
        for (int i = 0; i < noClassStrings.length; i++) {
            Set<State> stateSet = computeClass(psa, noClassStrings[i]);
            assertTrue("not class " + stateSet.toString(),
                       !classes.contains(stateSet));
        }
    }

    public void test_equivalenceRelation3() {
        PrefixStringRelation relation = new PrefixStringRelation(psa, 3);
        Set<Set<State>> classes = relation.getClasses(psa.getStates());
        assertEquals("nr of classes", 10, classes.size());
        String[][] classStrings = {
                {Glushkov.INITIAL_STATE}, {"a1"}, {"b3"}, {"c3"},
                {"a2"}, {"a3"}, {"b1"}, {"b2"}, {"c1"}, {"c2"}
        };
        for (int i = 0; i < classStrings.length; i++) {
            Set<State> stateSet = computeClass(psa, classStrings[i]);
            assertTrue("class " + stateSet.toString(),
                       classes.contains(stateSet));
        }
        String[][] noClassStrings = {
                {Glushkov.INITIAL_STATE, "a1"}, {"b3", "c3"},
                {"a2", "a3"}, {"b1", "b2"}, {"c1", "c2"}
        };
        for (int i = 0; i < noClassStrings.length; i++) {
            Set<State> stateSet = computeClass(psa, noClassStrings[i]);
            assertTrue("not class " + stateSet.toString(),
                       !classes.contains(stateSet));
        }
    }

    public void test_equivalenceRelation8() {
        PrefixStringRelation relation = new PrefixStringRelation(psa, 8);
        Set<Set<State>> classes = relation.getClasses(psa.getStates());
        assertEquals("nr of classes", 10, classes.size());
    }

    public void test_equivalenceRelation0() {
        PrefixStringRelation relation = new PrefixStringRelation(psa, 0);
        Set<Set<State>> classes = relation.getClasses(psa.getStates());
        assertEquals("nr of classes", 1, classes.size());
    }

    public void test_mergeToPrefix0() {
        AnnotatedSparseNFA<Integer,Integer> nfa = new AnnotatedSparseNFA<Integer,Integer>(psa);
        PrefixStringRelation relation = new PrefixStringRelation(nfa, 0);
        StateMerger<Integer,Integer> merger = new StateMerger<Integer,Integer>();
        AnnotatedStateNFA<Integer,Integer> mergedNFA = merger.merge(nfa, relation);
        SparseNFA sigmaStar = ThompsonBuilder.sigmaStarNFA(new String[] {"a", "b", "c"});
        try {
            assertTrue("sigma* equivalence",
                       EquivalenceTest.areEquivalent(new StateNFA[] {sigmaStar, mergedNFA}));
        } catch (NotDFAException e) {
            e.printStackTrace();
            fail("should be deterministic");
        }
    }

    public void test_mergeToPrefix1() {
        AnnotatedSparseNFA<Integer, Integer> nfa = new AnnotatedSparseNFA<Integer,Integer>(psa);
        PrefixStringRelation relation = new PrefixStringRelation(nfa, 1);
        StateMerger<Integer,Integer> merger = new StateMerger<Integer,Integer>();
        AnnotatedStateNFA<Integer,Integer> mergedNFA = merger.merge(nfa, relation);
        String[][] sample = {
                {"a", "a", "a", "b", "c"},
                {"a", "b", "b", "c"},
                {"c"}
        };
        AnnotatedSOAFactory factory = new AnnotatedSOAFactory();
        for (int i = 0; i < sample.length; i++) {
            factory.add(sample[i]);            
        }
        SparseNFA soa = factory.getAutomaton();
        try {
            assertTrue("SOA equivalence",
                       EquivalenceTest.areEquivalent(new StateNFA[] {soa, mergedNFA}));
        } catch (NotDFAException e) {
            e.printStackTrace();
            fail("should be deterministic");
        }
    }

    public void test_mergeToPrefix2() {
        AnnotatedSparseNFA<Integer, Integer> nfa = new AnnotatedSparseNFA<Integer,Integer>(psa);
        PrefixStringRelation relation = new PrefixStringRelation(nfa, 2);
        StateMerger<Integer,Integer> merger = new StateMerger<Integer,Integer>();
        AnnotatedStateNFA<Integer,Integer> mergedNFA = merger.merge(nfa, relation);
        SparseNFA targetNFA = new SparseNFA();
        targetNFA.addTransition("a", Glushkov.INITIAL_STATE, "a1");
        targetNFA.addTransition("c", Glushkov.INITIAL_STATE, "c2");
        targetNFA.addTransition("a", "a1", "a2");
        targetNFA.addTransition("b", "a1", "b1");
        targetNFA.addTransition("a", "a2", "a2");
        targetNFA.addTransition("b", "a2", "b1");
        targetNFA.addTransition("b", "b1", "b2");
        targetNFA.addTransition("c", "b1", "c1");
        targetNFA.addTransition("c", "b2", "c1");
        targetNFA.setInitialState(Glushkov.INITIAL_STATE);
        targetNFA.addFinalState("c1");
        targetNFA.addFinalState("c2");

        try {
            assertTrue("prefix 3 equivalence",
                       EquivalenceTest.areEquivalent(new StateNFA[] {targetNFA, mergedNFA}));
        } catch (NotDFAException e) {
            e.printStackTrace();
            fail("should be deterministic");
        }
    }

    public void test_mergeToPrefix3() {
        AnnotatedSparseNFA<Integer, Integer> nfa = new AnnotatedSparseNFA<Integer,Integer>(psa);
        PrefixStringRelation relation = new PrefixStringRelation(nfa, 3);
        StateMerger<Integer,Integer> merger = new StateMerger<Integer,Integer>();
        AnnotatedStateNFA<Integer,Integer> mergedNFA = merger.merge(nfa, relation);
        try {
            assertTrue("prefix 3 equivalence",
                       EquivalenceTest.areEquivalent(new StateNFA[] {psa, mergedNFA}));
        } catch (NotDFAException e) {
            e.printStackTrace();
            fail("should be deterministic");
        }
    }

    public void test_mergeToPrefix8() {
        AnnotatedSparseNFA<Integer, Integer> nfa = new AnnotatedSparseNFA<Integer,Integer>(psa);
        PrefixStringRelation relation = new PrefixStringRelation(nfa, 8);
        StateMerger<Integer,Integer> merger = new StateMerger<Integer,Integer>();
        AnnotatedStateNFA<Integer,Integer> mergedNFA = merger.merge(nfa, relation);
        try {
            assertTrue("prefix 3 equivalence",
                       EquivalenceTest.areEquivalent(new StateNFA[] {psa, mergedNFA}));
        } catch (NotDFAException e) {
            e.printStackTrace();
            fail("should be deterministic");
        }
    }

    public void test_mergeVsKLAFactory1() {
        AnnotatedSparseNFA<Integer, Integer> nfa = new AnnotatedSparseNFA<Integer,Integer>(psa);
        PrefixStringRelation relation = new PrefixStringRelation(nfa, 1);
        StateMerger<Integer,Integer> merger = new StateMerger<Integer,Integer>();
        AnnotatedStateNFA<Integer,Integer> mergedNFA = merger.merge(nfa, relation);
        KLAFactory factory = new KLAFactory(1);
        String examples =
            "a a a b c\n" +
            "a b b c\n" +
            "c\n";
        try {
            factory.add(new StringReader(examples));
        } catch (IOException e) {
            e.printStackTrace();
            fail("What do you mean, IOException??!??");
        }
        SparseNFA targetNFA = factory.getAutomaton();

        try {
            assertTrue("2-LA vs. merged",
                       EquivalenceTest.areEquivalent(new StateNFA[] {targetNFA, mergedNFA}));
        } catch (NotDFAException e) {
            e.printStackTrace();
            fail("This shouldn't happen");
        }
    }

    public void test_mergeVsKLAFactory2() {
        AnnotatedSparseNFA<Integer, Integer> nfa = new AnnotatedSparseNFA<Integer,Integer>(psa);
        PrefixStringRelation relation = new PrefixStringRelation(nfa, 2);
        StateMerger<Integer,Integer> merger = new StateMerger<Integer,Integer>();
        AnnotatedStateNFA<Integer,Integer> mergedNFA = merger.merge(nfa, relation);
        KLAFactory factory = new KLAFactory(2);
        String examples =
            "a a a b c\n" +
            "a b b c\n" +
            "c\n";
        try {
            factory.add(new StringReader(examples));
        } catch (IOException e) {
            e.printStackTrace();
            fail("What do you mean, IOException??!??");
        }
        SparseNFA targetNFA = factory.getAutomaton();

        try {
            assertTrue("2-LA vs. merged",
                       EquivalenceTest.areEquivalent(new StateNFA[] {targetNFA, mergedNFA}));
        } catch (NotDFAException e) {
            e.printStackTrace();
            fail("This shouldn't happen");
        }
    }

    public void test_mergeVsKLAFactory3() {
        AnnotatedSparseNFA<Integer, Integer> nfa = new AnnotatedSparseNFA<Integer,Integer>(psa);
        PrefixStringRelation relation = new PrefixStringRelation(nfa, 3);
        StateMerger<Integer,Integer> merger = new StateMerger<Integer,Integer>();
        AnnotatedStateNFA<Integer,Integer> mergedNFA = merger.merge(nfa, relation);
        KLAFactory factory = new KLAFactory(3);
        String examples =
            "a a a b c\n" +
            "a b b c\n" +
            "c\n";
        try {
            factory.add(new StringReader(examples));
        } catch (IOException e) {
            e.printStackTrace();
            fail("What do you mean, IOException??!??");
        }
        SparseNFA targetNFA = factory.getAutomaton();

        try {
            assertTrue("2-LA vs. merged",
                       EquivalenceTest.areEquivalent(new StateNFA[] {targetNFA, mergedNFA}));
        } catch (NotDFAException e) {
            e.printStackTrace();
            fail("This shouldn't happen");
        }
    }

    public void test_mergeVsKLAFactory8() {
        AnnotatedSparseNFA<Integer, Integer> nfa = new AnnotatedSparseNFA<Integer,Integer>(psa);
        PrefixStringRelation relation = new PrefixStringRelation(nfa, 8);
        StateMerger<Integer,Integer> merger = new StateMerger<Integer,Integer>();
        AnnotatedStateNFA<Integer,Integer> mergedNFA = merger.merge(nfa, relation);
        KLAFactory factory = new KLAFactory(8);
        String examples =
            "a a a b c\n" +
            "a b b c\n" +
            "c\n";
        try {
            factory.add(new StringReader(examples));
        } catch (IOException e) {
            e.printStackTrace();
            fail("What do you mean, IOException??!??");
        }
        SparseNFA targetNFA = factory.getAutomaton();

        try {
            assertTrue("2-LA vs. merged",
                       EquivalenceTest.areEquivalent(new StateNFA[] {targetNFA, mergedNFA}));
        } catch (NotDFAException e) {
            e.printStackTrace();
            fail("This shouldn't happen");
        }
    }

    protected static Set<State> computeClass(SparseNFA nfa, String[] elements) {
        Set<State> stateSet = new HashSet<State>();
        for (int i = 0; i < elements.length; i++) {
            stateSet.add(nfa.getState(elements[i]));
        }
        return stateSet;
    }

    protected static Set<String[]> computeTarget(String[][] strings) {
        Set<String[]> target = new HashSet<String[]>();
        for (int i = 0; i < strings.length; i++) {
            target.add(strings[i]);
        }
        return target;
    }

    protected static void printLanguages(Set<String[]> result) {
        for (String[] string : result) {
            System.out.println("'" + StringUtils.join(string, " ") + "'");
        }
    }

}
