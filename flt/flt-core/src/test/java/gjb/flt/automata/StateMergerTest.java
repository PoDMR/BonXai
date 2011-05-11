/*
 * Created on Jan 2, 2006
 * Modified on $Date: 2009-11-12 21:44:56 $
 */
package gjb.flt.automata;

import gjb.flt.automata.factories.sparse.AnnotationMerger;
import gjb.flt.automata.factories.sparse.GlushkovFactory;
import gjb.flt.automata.factories.sparse.StateMerger;
import gjb.flt.automata.factories.sparse.StateSetRenamer;
import gjb.flt.automata.impl.sparse.AnnotatedSparseNFA;
import gjb.flt.automata.impl.sparse.AnnotatedStateNFA;
import gjb.flt.automata.impl.sparse.SparseNFA;
import gjb.flt.automata.impl.sparse.State;
import gjb.flt.automata.impl.sparse.StateNFA;
import gjb.flt.automata.impl.sparse.Transition;
import gjb.flt.automata.measures.EquivalenceTest;
import gjb.flt.regex.Glushkov;

import java.util.Map;
import java.util.Set;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class StateMergerTest extends TestCase {

    public static void main(String[] args) {
        junit.textui.TestRunner.run(StateMergerTest.class);
    }

    public static Test suite() {
        return new TestSuite(StateMergerTest.class);
    }

    @SuppressWarnings("unchecked")
    public void test_stateMergeGlushkov() throws Exception {
        StateMerger merger = new StateMerger();
        GlushkovFactory glushkov = new GlushkovFactory();
        AnnotatedSparseNFA<Integer,Integer> nfa = new AnnotatedSparseNFA<Integer,Integer>(glushkov.create("(. (+ (. (a) (b))) (. (c) (+ (a)) (? (b))))"));
        merger.setRenamer(new GlushkovRenamer<Integer,Integer>());
        AnnotatedStateNFA<Integer,Integer> mergedNFA = merger.merge(nfa,
                                                                    new SOAEquivalenceRelation(nfa));
        SparseNFA targetNFA = new SparseNFA();
        targetNFA.setInitialState(Glushkov.INITIAL_STATE);
        targetNFA.addTransition("a", Glushkov.INITIAL_STATE, "a");
        targetNFA.addTransition("a", "a", "a");
        targetNFA.addTransition("b", "a", "b");
        targetNFA.addTransition("a", "b", "a");
        targetNFA.addTransition("c", "b", "c");
        targetNFA.addTransition("a", "c", "a");
        targetNFA.addFinalState("a");
        targetNFA.addFinalState("b");
        assertTrue("equiv",
                   EquivalenceTest.areEquivalent(new StateNFA[] {targetNFA, mergedNFA}));
    }

    @SuppressWarnings("unchecked")
    public void test_stateMerge1() throws Exception {
        AnnotatedSparseNFA<Integer,Integer> nfa = new AnnotatedSparseNFA<Integer,Integer>();
        nfa.setInitialState("q_I");
        nfa.addTransition("a", "q_I", "a");
        nfa.addTransition("b", "q_I", "b");
        nfa.addTransition("c", "q_I", "c");
        nfa.addTransition("d", "a", "d_1");
        nfa.addTransition("d", "b", "d_2");
        nfa.addTransition("d", "c", "d_2");
        nfa.addTransition("e", "d_1", "e");
        nfa.addTransition("b", "d_1", "b");
        nfa.addTransition("f", "d_1", "f");
        nfa.addTransition("d", "d_2", "d_2");
        nfa.addTransition("f", "d_2", "f");
        nfa.addTransition("g", "d_2", "g");
        nfa.addTransition("f", "e", "f");
        nfa.addTransition("h", "f", "h");
        nfa.addTransition("g", "g", "g");
        nfa.addTransition("h", "g", "h");
        nfa.addFinalState("h");
        nfa.annotate("a", "q_I", "a", 1);
        nfa.annotate("b", "q_I", "b", 2);
        nfa.annotate("c", "q_I", "c", 3);
        nfa.annotate("d", "a", "d_1", 4);
        nfa.annotate("d", "b", "d_2", 5);
        nfa.annotate("d", "c", "d_2", 6);
        nfa.annotate("e", "d_1", "e", 7);
        nfa.annotate("b", "d_1", "b", 8);
        nfa.annotate("f", "d_1", "f", 9);
        nfa.annotate("d", "d_2", "d_2", 10);
        nfa.annotate("f", "d_2", "f", 11);
        nfa.annotate("g", "d_2", "g", 12);
        nfa.annotate("f", "e", "f", 13);
        nfa.annotate("h", "f", "h", 14);
        nfa.annotate("g", "g", "g", 15);
        nfa.annotate("h", "g", "h", 16);
        nfa.annotate("a", 1);
        nfa.annotate("b", 2);
        nfa.annotate("c", 3);
        nfa.annotate("d_1", 4);
        nfa.annotate("d_2", 5);
        nfa.annotate("e", 6);
        nfa.annotate("f", 7);
        nfa.annotate("g", 8);
        nfa.annotate("h", 9);
        StateMerger merger = new StateMerger();
        merger.setRenamer(new MyStateSetRenamer());
        merger.setAnnotationMerger(new MyAnnotationMerger());
        merger.merge(nfa, nfa.getState("d_1"), nfa.getState("d_2"));
        assertTrue("merge state present", nfa.hasState("d_1#d_2"));
        assertEquals("incoming",
                     4,
                     nfa.getIncomingTransitions(nfa.getState("d_1#d_2")).size());
        assertEquals("outgoing",
                     5,
                     nfa.getOutgoingTransitions(nfa.getState("d_1#d_2")).size());
        String[] in = {"a", "b", "c", "d_1#d_2"};
        String[] out = {"b", "e", "f", "g"};
        for (int i = 0; i < in.length; i++)
            assertTrue("transition from " + in[i],
                       nfa.hasTransition("d", in[i], "d_1#d_2"));
        for (int i = 0; i < out.length; i++)
            assertTrue("transition to " + out[i],
                       nfa.hasTransition(out[i], "d_1#d_2", out[i]));
        assertEquals("e annotation",
                     new Integer(7), nfa.getAnnotation("e", "d_1#d_2", "e"));
        assertEquals("f annotation",
                     new Integer(20), nfa.getAnnotation("f", "d_1#d_2", "f"));
        assertEquals("d state annotation",
                     new Integer(20), nfa.getAnnotation("d_1#d_2"));
    }

    @SuppressWarnings("unchecked")
    public void test_stateMerge2() throws Exception {
        AnnotatedSparseNFA<Integer,Integer> nfa = new AnnotatedSparseNFA<Integer,Integer>();
        nfa.setInitialState("q_I");
        nfa.addTransition("a", "q_I", "a");
        nfa.addTransition("b", "q_I", "b");
        nfa.addTransition("c", "a", "c_1");
        nfa.addTransition("c", "b", "c_2");
        nfa.addTransition("c", "c_1", "c_1");
        nfa.addTransition("c", "c_1", "c_2");
        nfa.addTransition("c", "c_2", "c_1");
        nfa.addTransition("c", "c_2", "c_2");
        nfa.addTransition("d", "c_1", "d");
        nfa.addTransition("e", "c_1", "e");
        nfa.addTransition("d", "c_2", "d");
        nfa.addTransition("e", "c_2", "e");
        nfa.addTransition("c", "d", "c_1");
        nfa.addTransition("c", "d", "c_2");
        nfa.addTransition("c", "e", "c_1");
        nfa.addTransition("c", "e", "c_2");
        nfa.addFinalState("d");
        nfa.addFinalState("e");
        nfa.annotate("a", "q_I", "a", 1);
        nfa.annotate("b", "q_I", "b", 2);
        nfa.annotate("c", "a", "c_1", 3);
        nfa.annotate("c", "b", "c_2", 4);
        nfa.annotate("c", "c_1", "c_1", 5);
        nfa.annotate("c", "c_1", "c_2", 6);
        nfa.annotate("c", "c_2", "c_1", 7);
        nfa.annotate("c", "c_2", "c_2", 8);
        nfa.annotate("d", "c_1", "d", 9);
        nfa.annotate("e", "c_1", "e", 10);
        nfa.annotate("d", "c_2", "d", 11);
        nfa.annotate("e", "c_2", "e", 12);
        nfa.annotate("c", "d", "c_1", 13);
        nfa.annotate("c", "d", "c_2", 14);
        nfa.annotate("c", "e", "c_1", 15);
        nfa.annotate("c", "e", "c_2", 16);
        nfa.annotate("c_1", 5);
        nfa.annotate("c_2", 10);
        StateMerger merger = new StateMerger();
        merger.setRenamer(new MyStateSetRenamer());
        merger.setAnnotationMerger(new MyAnnotationMerger());
        merger.merge(nfa, nfa.getState("c_1"), nfa.getState("c_2"));
        assertTrue("merge state present", nfa.hasState("c_1#c_2"));
        assertEquals("incoming",
                     5,
                     nfa.getIncomingTransitions(nfa.getState("c_1#c_2")).size());
        assertEquals("outgoing",
                     3,
                     nfa.getOutgoingTransitions(nfa.getState("c_1#c_2")).size());
        String[] in = {"a", "b", "c_1#c_2"};
        String[] out = {"d", "e"};
        for (int i = 0; i < in.length; i++)
            assertTrue("transition from " + in[i],
                       nfa.hasTransition("c", in[i], "c_1#c_2"));
        for (int i = 0; i < out.length; i++)
            assertTrue("transition to " + out[i],
                       nfa.hasTransition(out[i], "c_1#c_2", out[i]));
        assertEquals("d annotation",
                     new Integer(20), nfa.getAnnotation("d", "c_1#c_2", "d"));
        assertEquals("e annotation",
                     new Integer(22), nfa.getAnnotation("e", "c_1#c_2", "e"));
        assertEquals("c annotation",
                     new Integer(26), nfa.getAnnotation("c", "c_1#c_2", "c_1#c_2"));
        assertEquals("d annotation out",
                     new Integer(27), nfa.getAnnotation("c", "d", "c_1#c_2"));
        assertEquals("e annotation out",
                     new Integer(31), nfa.getAnnotation("c", "e", "c_1#c_2"));
        assertEquals("c state annotation",
                     new Integer(50), nfa.getAnnotation("c_1#c_2"));
    }

    protected class MyStateSetRenamer implements StateSetRenamer<Integer,Integer> {

        public String rename(AnnotatedStateNFA<Integer, Integer> nfa,
                             Set<State> stateSet) {
            return null;
        }

        public String rename(AnnotatedStateNFA<Integer,Integer> nfa,
                             State state1, State state2) {
            return nfa.getStateValue(state1) + "#" + nfa.getStateValue(state2);
        }
        
    }

    protected class MyAnnotationMerger implements AnnotationMerger<Integer,Integer> {

        public void merge(AnnotatedStateNFA<Integer,Integer> oldNFA,
                          AnnotatedStateNFA<Integer,Integer> newNFA,
                          Map<State,Set<State>> stateMap) {}

        public Integer mergeStateAnnotations(Integer stateAnn1,
                                             Integer stateAnn2) {
            return stateAnn1*stateAnn2;
        }

        public Integer mergeTransitionAnnotations(Integer transAnn1,
                                                  Integer transAnn2) {
            return transAnn1 + transAnn2;
        }

        public Integer mergeStateAnnotation(State state1, Integer stateAnn1,
                                            State state2, Integer stateAnn2) {
            return null;
        }

        public Integer mergeTransitionAnnotations(Transition trans1,
                                                  Integer transAnn1,
                                                  Transition trans2,
                                                  Integer transAnn2) {
            return null;
        }
        
    }

}
