/*
 * Created on Sep 6, 2008
 * Modified on $Date: 2009-10-26 15:29:20 $
 */
package eu.fox7.flt.regex.infer.rwr;

import eu.fox7.flt.regex.infer.rwr.AutomatonRewriter;
import eu.fox7.flt.regex.infer.rwr.impl.Automaton;
import eu.fox7.flt.regex.infer.rwr.impl.GraphAutomatonFactory;
import eu.fox7.flt.regex.infer.rwr.rewriters.ConcatFinder;
import eu.fox7.flt.regex.infer.rwr.rewriters.ConcatOptionalFinder;
import eu.fox7.flt.regex.infer.rwr.rewriters.ConcatOptionalRewriter;
import eu.fox7.flt.regex.infer.rwr.rewriters.ConcatRewriter;
import eu.fox7.flt.regex.infer.rwr.rewriters.DisjunctionFinder;
import eu.fox7.flt.regex.infer.rwr.rewriters.DisjunctionRewriter;
import eu.fox7.flt.regex.infer.rwr.rewriters.OpportunityFinder;
import eu.fox7.flt.regex.infer.rwr.rewriters.OptionalConcatFinder;
import eu.fox7.flt.regex.infer.rwr.rewriters.OptionalConcatOptionalFinder;
import eu.fox7.flt.regex.infer.rwr.rewriters.OptionalConcatOptionalRewriter;
import eu.fox7.flt.regex.infer.rwr.rewriters.OptionalConcatRewriter;
import eu.fox7.flt.regex.infer.rwr.rewriters.OptionalFinder;
import eu.fox7.flt.regex.infer.rwr.rewriters.OptionalRewriter;
import eu.fox7.flt.regex.infer.rwr.rewriters.RepetitionFinder;
import eu.fox7.flt.regex.infer.rwr.rewriters.RepetitionRewriter;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * @author eu.fox7
 * @version $Revision: 1.1 $
 * 
 */
public class RewriteStepTest extends TestCase {

    public static Test suite() {
        return new TestSuite(RewriteStepTest.class);
    }

    public void testDisjunction1() {
        final String[][] sample = {
                {"a"},
                {"b"}
        };
        final int[][] transitions = {{0, 1}, {1, 0}};
        GraphAutomatonFactory factory = new GraphAutomatonFactory();
        Automaton automaton = factory.create(sample);
        OpportunityFinder finder = new DisjunctionFinder();
        int[] result = finder.getFirst(automaton);
        AutomatonRewriter rewriter = new DisjunctionRewriter();
        Automaton newAutomaton = rewriter.rewrite(automaton, result);
        assertTrue("automaton", newAutomaton != null);
        assertEquals("size", transitions.length, newAutomaton.getNumberOfStates());
        assertEquals("label", "(| (a) (b))", newAutomaton.getLabel(0));
        for (int i = 0; i < transitions.length; i++)
            for (int j = 0; j < transitions[i].length; j++)
                assertEquals(i + "-" + j,
                             transitions[i][j],
                             newAutomaton.get(i, j));
        assertEquals("epsilon",
                     automaton.acceptsEpsilon(),
                     newAutomaton.acceptsEpsilon());
    }

    public void testDisjunction2() {
        final String[][] sample = {
                {"a"},
                {"b"},
                {}
        };
        final int[][] transitions = {{0, 1}, {1, 1}};
        GraphAutomatonFactory factory = new GraphAutomatonFactory();
        Automaton automaton = factory.create(sample);
        OpportunityFinder finder = new DisjunctionFinder();
        int[] result = finder.getFirst(automaton);
        AutomatonRewriter rewriter = new DisjunctionRewriter();
        Automaton newAutomaton = rewriter.rewrite(automaton, result);
        assertTrue("automaton", newAutomaton != null);
        assertEquals("size", transitions.length, newAutomaton.getNumberOfStates());
        assertEquals("label", "(| (a) (b))", newAutomaton.getLabel(0));
        for (int i = 0; i < transitions.length; i++)
            for (int j = 0; j < transitions[i].length; j++)
                assertEquals(i + "-" + j,
                             transitions[i][j],
                             newAutomaton.get(i, j));
        assertEquals("epsilon",
                     automaton.acceptsEpsilon(),
                     newAutomaton.acceptsEpsilon());
    }
    
    public void testDisjunction3() {
        final String[][] sample = {
                {"a", "c"},
                {"b", "c"}
        };
        final int[][] transitions = {{0, 1, 0}, {0, 0, 1}, {1, 0, 0}};
        GraphAutomatonFactory factory = new GraphAutomatonFactory();
        Automaton automaton = factory.create(sample);
        OpportunityFinder finder = new DisjunctionFinder();
        int[] result = finder.getFirst(automaton);
        AutomatonRewriter rewriter = new DisjunctionRewriter();
        Automaton newAutomaton = rewriter.rewrite(automaton, result);
        assertTrue("automaton", newAutomaton != null);
        assertEquals("size", transitions.length, newAutomaton.getNumberOfStates());
        assertEquals("label", "(| (a) (b))", newAutomaton.getLabel(0));
        for (int i = 0; i < transitions.length; i++)
            for (int j = 0; j < transitions[i].length; j++)
                assertEquals(i + "-" + j,
                             transitions[i][j],
                             newAutomaton.get(i, j));
        assertEquals("epsilon",
                     automaton.acceptsEpsilon(),
                     newAutomaton.acceptsEpsilon());
    }

    public void testDisjunction4() {
        final String[][] sample = {
                {"a", "c"},
                {"b", "c"},
                {"a"},
                {"b"}
        };
        final int[][] transitions = {{0, 1, 1}, {0, 0, 1}, {1, 0, 0}};
        GraphAutomatonFactory factory = new GraphAutomatonFactory();
        Automaton automaton = factory.create(sample);
        OpportunityFinder finder = new DisjunctionFinder();
        int[] result = finder.getFirst(automaton);
        AutomatonRewriter rewriter = new DisjunctionRewriter();
        Automaton newAutomaton = rewriter.rewrite(automaton, result);
        assertTrue("automaton", newAutomaton != null);
        assertEquals("size", transitions.length, newAutomaton.getNumberOfStates());
        assertEquals("label", "(| (a) (b))", newAutomaton.getLabel(0));
        for (int i = 0; i < transitions.length; i++)
            for (int j = 0; j < transitions[i].length; j++)
                assertEquals(i + "-" + j,
                             transitions[i][j],
                             newAutomaton.get(i, j));
        assertEquals("epsilon",
                     automaton.acceptsEpsilon(),
                     newAutomaton.acceptsEpsilon());
    }
    
    public void testDisjunction5() {
        final String[][] sample = {
                {"d", "a", "c"},
                {"d", "b", "c"}
        };
        final int[][] transitions = {{0, 1, 0, 0}, {0, 0, 0, 1}, {1, 0, 0, 0}, {0, 0, 1, 0}};
        GraphAutomatonFactory factory = new GraphAutomatonFactory();
        Automaton automaton = factory.create(sample);
        OpportunityFinder finder = new DisjunctionFinder();
        int[] result = finder.getFirst(automaton);
        AutomatonRewriter rewriter = new DisjunctionRewriter();
        Automaton newAutomaton = rewriter.rewrite(automaton, result);
        assertTrue("automaton", newAutomaton != null);
        assertEquals("size", transitions.length, newAutomaton.getNumberOfStates());
        assertEquals("label", "(| (a) (b))", newAutomaton.getLabel(0));
        for (int i = 0; i < transitions.length; i++)
            for (int j = 0; j < transitions[i].length; j++)
                assertEquals(i + "-" + j,
                             transitions[i][j],
                             newAutomaton.get(i, j));
        assertEquals("epsilon",
                     automaton.acceptsEpsilon(),
                     newAutomaton.acceptsEpsilon());
    }

    public void testDisjunction6() {
        final String[][] sample = {
                {"a"},
                {"b"},
                {"a", "a"},
                {"b", "b"},
                {"a", "b", "a"}
        };
        final int[][] transitions = {{1, 1}, {1, 0}};
        GraphAutomatonFactory factory = new GraphAutomatonFactory();
        Automaton automaton = factory.create(sample);
        OpportunityFinder finder = new DisjunctionFinder();
        int[] result = finder.getFirst(automaton);
        AutomatonRewriter rewriter = new DisjunctionRewriter();
        Automaton newAutomaton = rewriter.rewrite(automaton, result);
        assertTrue("automaton", newAutomaton != null);
        assertEquals("size", transitions.length, newAutomaton.getNumberOfStates());
        assertEquals("label", "(| (a) (b))", newAutomaton.getLabel(0));
        for (int i = 0; i < transitions.length; i++)
            for (int j = 0; j < transitions[i].length; j++)
                assertEquals(i + "-" + j,
                             transitions[i][j],
                             newAutomaton.get(i, j));
        assertEquals("epsilon",
                     automaton.acceptsEpsilon(),
                     newAutomaton.acceptsEpsilon());
    }

    public void testDisjunction7() {
        final String[][] sample = {
                {"a"},
                {"b"},
                {"a", "a"},
                {"b", "b"},
                {"a", "b", "a"},
                {}
        };
        final int[][] transitions = {{1, 1}, {1, 1}};
        GraphAutomatonFactory factory = new GraphAutomatonFactory();
        Automaton automaton = factory.create(sample);
        OpportunityFinder finder = new DisjunctionFinder();
        int[] result = finder.getFirst(automaton);
        AutomatonRewriter rewriter = new DisjunctionRewriter();
        Automaton newAutomaton = rewriter.rewrite(automaton, result);
        assertTrue("automaton", newAutomaton != null);
        assertEquals("size", transitions.length, newAutomaton.getNumberOfStates());
        assertEquals("label", "(| (a) (b))", newAutomaton.getLabel(0));
        for (int i = 0; i < transitions.length; i++)
            for (int j = 0; j < transitions[i].length; j++)
                assertEquals(i + "-" + j,
                             transitions[i][j],
                             newAutomaton.get(i, j));
        assertEquals("epsilon",
                     automaton.acceptsEpsilon(),
                     newAutomaton.acceptsEpsilon());
    }
    
    public void testDisjunction8() {
        final String[][] sample = {
                {"d", "a", "c"},
                {"d", "b", "c"},
                {"d", "a", "e"},
                {"d", "b", "e"}
        };
        int[][] transitions = {{0, 1, 0, 1, 0}, {0, 0, 0, 0, 1}, {1, 0, 0, 0, 0}, {0, 0, 0, 0, 1}, {0, 0, 1, 0, 0}};
        GraphAutomatonFactory factory = new GraphAutomatonFactory();
        Automaton automaton = factory.create(sample);
        OpportunityFinder finder = new DisjunctionFinder();
        int[] result = finder.getFirst(automaton);
        AutomatonRewriter rewriter = new DisjunctionRewriter();
        Automaton newAutomaton = rewriter.rewrite(automaton, result);
        assertTrue("automaton", newAutomaton != null);
        assertEquals("size", transitions.length, newAutomaton.getNumberOfStates());
        assertEquals("label", "(| (a) (b))", newAutomaton.getLabel(0));
        for (int i = 0; i < transitions.length; i++)
            for (int j = 0; j < transitions[i].length; j++)
                assertEquals(i + "-" + j,
                             transitions[i][j],
                             newAutomaton.get(i, j));
        assertEquals("epsilon",
                     automaton.acceptsEpsilon(),
                     newAutomaton.acceptsEpsilon());
        transitions = new int[][] {{0, 1, 0, 0}, {0, 0, 0, 1}, {1, 0, 0, 0}, {0, 0, 1, 0}};
        result = finder.getFirst(newAutomaton);
        newAutomaton = rewriter.rewrite(newAutomaton, result);
        assertTrue("automaton", newAutomaton != null);
        assertEquals("size", transitions.length, newAutomaton.getNumberOfStates());
        assertEquals("label", "(| (c) (e))", newAutomaton.getLabel(1));
        for (int i = 0; i < transitions.length; i++)
            for (int j = 0; j < transitions[i].length; j++)
                assertEquals(i + "-" + j,
                             transitions[i][j],
                             newAutomaton.get(i, j));
        assertEquals("epsilon",
                     automaton.acceptsEpsilon(),
                     newAutomaton.acceptsEpsilon());
    }

    public void testRepetition1() {
        final String[][] sample = {
                {"a"},
                {"a", "a"}
        };
        final int[][] transitions = {{0, 1}, {1, 0}};
        GraphAutomatonFactory factory = new GraphAutomatonFactory();
        Automaton automaton = factory.create(sample);
        OpportunityFinder finder = new RepetitionFinder();
        int[] result = finder.getFirst(automaton);
        AutomatonRewriter rewriter = new RepetitionRewriter();
        Automaton newAutomaton = rewriter.rewrite(automaton, result);
        assertTrue("automaton", newAutomaton != null);
        assertEquals("size", transitions.length, newAutomaton.getNumberOfStates());
        assertEquals("label", "(+ (a))", newAutomaton.getLabel(0));
        for (int i = 0; i < transitions.length; i++)
            for (int j = 0; j < transitions[i].length; j++)
                assertEquals(i + "-" + j,
                             transitions[i][j],
                             newAutomaton.get(i, j));
        assertEquals("epsilon",
                     automaton.acceptsEpsilon(),
                     newAutomaton.acceptsEpsilon());
    }

    public void testRepetition2() {
        final String[][] sample = {
                {"a"},
                {"b"},
                {"a", "a"},
                {"b", "b"},
                {"a", "b", "a"},
                {}
        };
        int[][] transitions = {{1, 1}, {1, 1}};
        GraphAutomatonFactory factory = new GraphAutomatonFactory();
        Automaton automaton = factory.create(sample);
        OpportunityFinder finder = new DisjunctionFinder();
        int[] result = finder.getFirst(automaton);
        AutomatonRewriter rewriter = new DisjunctionRewriter();
        Automaton newAutomaton = rewriter.rewrite(automaton, result);
        assertTrue("automaton", newAutomaton != null);
        assertEquals("size", transitions.length, newAutomaton.getNumberOfStates());
        assertEquals("label", "(| (a) (b))", newAutomaton.getLabel(0));
        for (int i = 0; i < transitions.length; i++)
            for (int j = 0; j < transitions[i].length; j++)
                assertEquals(i + "-" + j,
                             transitions[i][j],
                             newAutomaton.get(i, j));
        assertEquals("epsilon",
                     automaton.acceptsEpsilon(),
                     newAutomaton.acceptsEpsilon());
        transitions = new int[][] {{0, 1}, {1, 1}};
        finder = new RepetitionFinder();
        rewriter = new RepetitionRewriter();
        result = finder.getFirst(newAutomaton);
        newAutomaton = rewriter.rewrite(newAutomaton, result);
        assertTrue("automaton", newAutomaton != null);
        assertEquals("size", transitions.length, newAutomaton.getNumberOfStates());
        assertEquals("label", "(+ (| (a) (b)))", newAutomaton.getLabel(0));
        for (int i = 0; i < transitions.length; i++)
            for (int j = 0; j < transitions[i].length; j++)
                assertEquals(i + "-" + j,
                             transitions[i][j],
                             newAutomaton.get(i, j));
        assertEquals("epsilon",
                     automaton.acceptsEpsilon(),
                     newAutomaton.acceptsEpsilon());
    }
    
    public void testRepetition3() {
        final String[][] sample = {
                {"a"},
                {"b"},
                {"a", "a"},
                {"b", "b"},
                {"a", "b", "a"},
                {}
        };
        int[][] transitions = {{0, 1, 1}, {1, 1, 1}, {1, 1, 1}};
        GraphAutomatonFactory factory = new GraphAutomatonFactory();
        Automaton automaton = factory.create(sample);
        OpportunityFinder finder = new RepetitionFinder();
        AutomatonRewriter rewriter = new RepetitionRewriter();
        int[] result = finder.getFirst(automaton);
        Automaton newAutomaton = rewriter.rewrite(automaton, result);
        assertTrue("automaton", newAutomaton != null);
        assertEquals("size", transitions.length, newAutomaton.getNumberOfStates());
        assertEquals("label", "(+ (a))", newAutomaton.getLabel(0));
        for (int i = 0; i < transitions.length; i++)
            for (int j = 0; j < transitions[i].length; j++)
                assertEquals(i + "-" + j,
                             transitions[i][j],
                             newAutomaton.get(i, j));
        assertEquals("epsilon",
                     automaton.acceptsEpsilon(),
                     newAutomaton.acceptsEpsilon());
        transitions = new int[][] {{0, 1, 1}, {1, 0, 1}, {1, 1, 1}};
        result = finder.getFirst(newAutomaton);
        newAutomaton = rewriter.rewrite(newAutomaton, result);
        assertTrue("automaton", newAutomaton != null);
        assertEquals("size", transitions.length, newAutomaton.getNumberOfStates());
        assertEquals("label", "(+ (a))", newAutomaton.getLabel(0));
        assertEquals("label", "(+ (b))", newAutomaton.getLabel(1));
        for (int i = 0; i < transitions.length; i++)
            for (int j = 0; j < transitions[i].length; j++)
                assertEquals(i + "-" + j,
                             transitions[i][j],
                             newAutomaton.get(i, j));
        assertEquals("epsilon",
                     automaton.acceptsEpsilon(),
                     newAutomaton.acceptsEpsilon());
        transitions = new int[][] {{1, 1}, {1, 1}};
        finder = new DisjunctionFinder();
        result = finder.getFirst(newAutomaton);
        rewriter = new DisjunctionRewriter();
        newAutomaton = rewriter.rewrite(newAutomaton, result);
        assertTrue("automaton", newAutomaton != null);
        assertEquals("size", transitions.length, newAutomaton.getNumberOfStates());
        assertEquals("label", "(| (+ (a)) (+ (b)))", newAutomaton.getLabel(0));
        for (int i = 0; i < transitions.length; i++)
            for (int j = 0; j < transitions[i].length; j++)
                assertEquals(i + "-" + j,
                             transitions[i][j],
                             newAutomaton.get(i, j));
        assertEquals("epsilon",
                     automaton.acceptsEpsilon(),
                     newAutomaton.acceptsEpsilon());
    }
    
    public void testConcat1() {
        final String[][] sample = {
                {"a", "b"}
        };
        int[][] transitions = {{0, 1}, {1, 0}};
        GraphAutomatonFactory factory = new GraphAutomatonFactory();
        Automaton automaton = factory.create(sample);
        OpportunityFinder finder = new ConcatFinder();
        int[] result = finder.getFirst(automaton);
        AutomatonRewriter rewriter = new ConcatRewriter();
        Automaton newAutomaton = rewriter.rewrite(automaton, result);
        assertTrue("automaton", newAutomaton != null);
        assertEquals("size", transitions.length, newAutomaton.getNumberOfStates());
        assertEquals("label", "(. (a) (b))", newAutomaton.getLabel(0));
        for (int i = 0; i < transitions.length; i++)
            for (int j = 0; j < transitions[i].length; j++)
                assertEquals(i + "-" + j,
                             transitions[i][j],
                             newAutomaton.get(i, j));
        assertEquals("epsilon",
                     automaton.acceptsEpsilon(),
                     newAutomaton.acceptsEpsilon());
    }

    public void testConcat2() {
        final String[][] sample = {
                {"c", "a", "b"},
                {"a", "b"}
        };
        int[][] transitions = {{0, 0, 1}, {1, 0, 0}, {1, 1, 0}};
        GraphAutomatonFactory factory = new GraphAutomatonFactory();
        Automaton automaton = factory.create(sample);
        OpportunityFinder finder = new ConcatFinder();
        int[] result = finder.getFirst(automaton);
        AutomatonRewriter rewriter = new ConcatRewriter();
        Automaton newAutomaton = rewriter.rewrite(automaton, result);
        assertTrue("automaton", newAutomaton != null);
        assertEquals("size", transitions.length, newAutomaton.getNumberOfStates());
        assertEquals("label", "(. (a) (b))", newAutomaton.getLabel(0));
        for (int i = 0; i < transitions.length; i++)
            for (int j = 0; j < transitions[i].length; j++)
                assertEquals(i + "-" + j,
                             transitions[i][j],
                             newAutomaton.get(i, j));
        assertEquals("epsilon",
                     automaton.acceptsEpsilon(),
                     newAutomaton.acceptsEpsilon());
    }
    
    public void testConcat3() {
        final String[][] sample = {
                {"c", "a", "b"},
                {"a", "b", "a", "b"}
        };
        int[][] transitions = {{1, 0, 1}, {1, 0, 0}, {1, 1, 0}};
        GraphAutomatonFactory factory = new GraphAutomatonFactory();
        Automaton automaton = factory.create(sample);
        OpportunityFinder finder = new ConcatFinder();
        int[] result = finder.getFirst(automaton);
        AutomatonRewriter rewriter = new ConcatRewriter();
        Automaton newAutomaton = rewriter.rewrite(automaton, result);
        assertTrue("automaton", newAutomaton != null);
        assertEquals("size", transitions.length, newAutomaton.getNumberOfStates());
        assertEquals("label", "(. (a) (b))", newAutomaton.getLabel(0));
        for (int i = 0; i < transitions.length; i++)
            for (int j = 0; j < transitions[i].length; j++)
                assertEquals(i + "-" + j,
                             transitions[i][j],
                             newAutomaton.get(i, j));
        assertEquals("epsilon",
                     automaton.acceptsEpsilon(),
                     newAutomaton.acceptsEpsilon());
    }
    
    public void testConcat4() {
        final String[][] sample = {
                {"c", "a", "b"},
                {"a", "b", "a", "b"},
                {}
        };
        int[][] transitions = {{1, 0, 1}, {1, 0, 0}, {1, 1, 1}};
        GraphAutomatonFactory factory = new GraphAutomatonFactory();
        Automaton automaton = factory.create(sample);
        OpportunityFinder finder = new ConcatFinder();
        int[] result = finder.getFirst(automaton);
        AutomatonRewriter rewriter = new ConcatRewriter();
        Automaton newAutomaton = rewriter.rewrite(automaton, result);
        assertTrue("automaton", newAutomaton != null);
        assertEquals("size", transitions.length, newAutomaton.getNumberOfStates());
        assertEquals("label", "(. (a) (b))", newAutomaton.getLabel(0));
        for (int i = 0; i < transitions.length; i++)
            for (int j = 0; j < transitions[i].length; j++)
                assertEquals(i + "-" + j,
                             transitions[i][j],
                             newAutomaton.get(i, j));
        assertEquals("epsilon",
                     automaton.acceptsEpsilon(),
                     newAutomaton.acceptsEpsilon());
    }
    
    public void testConcat5() {
        final String[][] sample = {
                {"c", "a", "b", "d"},
                {"a", "b", "a", "b", "e"},
                {"d"},
                {"e"}
        };
        int[][] transitions = {{1, 0, 1, 1, 0}, {1, 0, 0, 0, 0}, {0, 0, 0, 0, 1}, {0, 0, 0, 0, 1}, {1, 1, 1, 1, 0}};
        GraphAutomatonFactory factory = new GraphAutomatonFactory();
        Automaton automaton = factory.create(sample);
        OpportunityFinder finder = new ConcatFinder();
        int[] result = finder.getFirst(automaton);
        AutomatonRewriter rewriter = new ConcatRewriter();
        Automaton newAutomaton = rewriter.rewrite(automaton, result);
        assertTrue("automaton", newAutomaton != null);
        assertEquals("size", transitions.length, newAutomaton.getNumberOfStates());
        assertEquals("label", "(. (a) (b))", newAutomaton.getLabel(0));
        for (int i = 0; i < transitions.length; i++)
            for (int j = 0; j < transitions[i].length; j++)
                assertEquals(i + "-" + j,
                             transitions[i][j],
                             newAutomaton.get(i, j));
        assertEquals("epsilon",
                     automaton.acceptsEpsilon(),
                     newAutomaton.acceptsEpsilon());
    }
    
    public void testConcat6() {
        final String[][] sample = {
                {"b", "a"}
        };
        int[][] transitions = {{0, 1}, {1, 0}};
        GraphAutomatonFactory factory = new GraphAutomatonFactory();
        Automaton automaton = factory.create(sample);
        OpportunityFinder finder = new ConcatFinder();
        int[] result = finder.getFirst(automaton);
        AutomatonRewriter rewriter = new ConcatRewriter();
        Automaton newAutomaton = rewriter.rewrite(automaton, result);
        assertTrue("automaton", newAutomaton != null);
        assertEquals("size", transitions.length, newAutomaton.getNumberOfStates());
        assertEquals("label", "(. (b) (a))", newAutomaton.getLabel(0));
        for (int i = 0; i < transitions.length; i++)
            for (int j = 0; j < transitions[i].length; j++)
                assertEquals(i + "-" + j,
                             transitions[i][j],
                             newAutomaton.get(i, j));
        assertEquals("epsilon",
                     automaton.acceptsEpsilon(),
                     newAutomaton.acceptsEpsilon());
    }

    public void testConcatOptional1() {
        final String[][] sample = {
                {"a", "b"},
                {"a"}
        };
        int[][] transitions = {{0, 1}, {1, 0}};
        GraphAutomatonFactory factory = new GraphAutomatonFactory();
        Automaton automaton = factory.create(sample);
        OpportunityFinder finder = new ConcatOptionalFinder();
        int[] result = finder.getFirst(automaton);
        AutomatonRewriter rewriter = new ConcatOptionalRewriter();
        Automaton newAutomaton = rewriter.rewrite(automaton, result);
        assertTrue("automaton", newAutomaton != null);
        assertEquals("size", transitions.length, newAutomaton.getNumberOfStates());
        assertEquals("label", "(. (a) (? (b)))", newAutomaton.getLabel(0));
        for (int i = 0; i < transitions.length; i++)
            for (int j = 0; j < transitions[i].length; j++)
                assertEquals(i + "-" + j,
                             transitions[i][j],
                             newAutomaton.get(i, j));
        assertEquals("epsilon",
                     automaton.acceptsEpsilon(),
                     newAutomaton.acceptsEpsilon());
    }
    
    public void testConcatOptional2() {
        final String[][] sample = {
                {"a", "b"},
                {"a"},
                {}
        };
        int[][] transitions = {{0, 1}, {1, 1}};
        GraphAutomatonFactory factory = new GraphAutomatonFactory();
        Automaton automaton = factory.create(sample);
        OpportunityFinder finder = new ConcatOptionalFinder();
        int[] result = finder.getFirst(automaton);
        AutomatonRewriter rewriter = new ConcatOptionalRewriter();
        Automaton newAutomaton = rewriter.rewrite(automaton, result);
        assertTrue("automaton", newAutomaton != null);
        assertEquals("size", transitions.length, newAutomaton.getNumberOfStates());
        assertEquals("label", "(. (a) (? (b)))", newAutomaton.getLabel(0));
        for (int i = 0; i < transitions.length; i++)
            for (int j = 0; j < transitions[i].length; j++)
                assertEquals(i + "-" + j,
                             transitions[i][j],
                             newAutomaton.get(i, j));
        assertEquals("epsilon",
                     automaton.acceptsEpsilon(),
                     newAutomaton.acceptsEpsilon());
    }
    
    public void testConcatOptional3() {
        final String[][] sample = {
                {"a", "b"},
                {"a"},
                {"a", "a", "b"},
                {"a", "b", "a"},
                {}
        };
        int[][] transitions = {{1, 1}, {1, 1}};
        GraphAutomatonFactory factory = new GraphAutomatonFactory();
        Automaton automaton = factory.create(sample);
        OpportunityFinder finder = new ConcatOptionalFinder();
        int[] result = finder.getFirst(automaton);
        AutomatonRewriter rewriter = new ConcatOptionalRewriter();
        Automaton newAutomaton = rewriter.rewrite(automaton, result);
        assertTrue("automaton", newAutomaton != null);
        assertEquals("size", transitions.length, newAutomaton.getNumberOfStates());
        assertEquals("label", "(. (a) (? (b)))", newAutomaton.getLabel(0));
        for (int i = 0; i < transitions.length; i++)
            for (int j = 0; j < transitions[i].length; j++)
                assertEquals(i + "-" + j,
                             transitions[i][j],
                             newAutomaton.get(i, j));
        assertEquals("epsilon",
                     automaton.acceptsEpsilon(),
                     newAutomaton.acceptsEpsilon());
    }
    
    public void testConcatOptional4() {
        final String[][] sample = {
                {"c", "a", "b", "d"},
                {"a", "b", "a", "b", "e"},
                {"a", "a", "e"},
                {"c", "a", "d"},
                {"d"},
                {"e"}
        };
        int[][] transitions = {{1, 0, 1, 1, 0}, {1, 0, 0, 0, 0}, {0, 0, 0, 0, 1}, {0, 0, 0, 0, 1}, {1, 1, 1, 1, 0}};
        GraphAutomatonFactory factory = new GraphAutomatonFactory();
        Automaton automaton = factory.create(sample);
        OpportunityFinder finder = new ConcatOptionalFinder();
        int[] result = finder.getFirst(automaton);
        AutomatonRewriter rewriter = new ConcatOptionalRewriter();
        Automaton newAutomaton = rewriter.rewrite(automaton, result);
        assertTrue("automaton", newAutomaton != null);
        assertEquals("size", transitions.length, newAutomaton.getNumberOfStates());
        assertEquals("label", "(. (a) (? (b)))", newAutomaton.getLabel(0));
        for (int i = 0; i < transitions.length; i++)
            for (int j = 0; j < transitions[i].length; j++)
                assertEquals(i + "-" + j,
                             transitions[i][j],
                             newAutomaton.get(i, j));
        assertEquals("epsilon",
                     automaton.acceptsEpsilon(),
                     newAutomaton.acceptsEpsilon());
    }
    
    public void testOptionalConcat1() {
        final String[][] sample = {
                {"a", "b"},
                {"b"}
        };
        int[][] transitions = {{0, 1}, {1, 0}};
        GraphAutomatonFactory factory = new GraphAutomatonFactory();
        Automaton automaton = factory.create(sample);
        OpportunityFinder finder = new OptionalConcatFinder();
        int[] result = finder.getFirst(automaton);
        AutomatonRewriter rewriter = new OptionalConcatRewriter();
        Automaton newAutomaton = rewriter.rewrite(automaton, result);
        assertTrue("automaton", newAutomaton != null);
        assertEquals("size", transitions.length, newAutomaton.getNumberOfStates());
        assertEquals("label", "(. (? (a)) (b))", newAutomaton.getLabel(0));
        for (int i = 0; i < transitions.length; i++)
            for (int j = 0; j < transitions[i].length; j++)
                assertEquals(i + "-" + j,
                             transitions[i][j],
                             newAutomaton.get(i, j));
        assertEquals("epsilon",
                     automaton.acceptsEpsilon(),
                     newAutomaton.acceptsEpsilon());
    }
    
    public void testOptionalConcat2() {
        final String[][] sample = {
                {"a", "b"},
                {"b"},
                {}
        };
        int[][] transitions = {{0, 1}, {1, 1}};
        GraphAutomatonFactory factory = new GraphAutomatonFactory();
        Automaton automaton = factory.create(sample);
        OpportunityFinder finder = new OptionalConcatFinder();
        int[] result = finder.getFirst(automaton);
        AutomatonRewriter rewriter = new OptionalConcatRewriter();
        Automaton newAutomaton = rewriter.rewrite(automaton, result);
        assertTrue("automaton", newAutomaton != null);
        assertEquals("size", transitions.length, newAutomaton.getNumberOfStates());
        assertEquals("label", "(. (? (a)) (b))", newAutomaton.getLabel(0));
        for (int i = 0; i < transitions.length; i++)
            for (int j = 0; j < transitions[i].length; j++)
                assertEquals(i + "-" + j,
                             transitions[i][j],
                             newAutomaton.get(i, j));
        assertEquals("epsilon",
                     automaton.acceptsEpsilon(),
                     newAutomaton.acceptsEpsilon());
    }
    
    public void testOptionalConcat3() {
        final String[][] sample = {
                {"a", "b"},
                {"b"},
                {"a", "b", "a", "b"},
                {"b", "b"},
                {}
        };
        int[][] transitions = {{1, 1}, {1, 1}};
        GraphAutomatonFactory factory = new GraphAutomatonFactory();
        Automaton automaton = factory.create(sample);
        OpportunityFinder finder = new OptionalConcatFinder();
        int[] result = finder.getFirst(automaton);
        AutomatonRewriter rewriter = new OptionalConcatRewriter();
        Automaton newAutomaton = rewriter.rewrite(automaton, result);
        assertTrue("automaton", newAutomaton != null);
        assertEquals("size", transitions.length, newAutomaton.getNumberOfStates());
        assertEquals("label", "(. (? (a)) (b))", newAutomaton.getLabel(0));
        for (int i = 0; i < transitions.length; i++)
            for (int j = 0; j < transitions[i].length; j++)
                assertEquals(i + "-" + j,
                             transitions[i][j],
                             newAutomaton.get(i, j));
        assertEquals("epsilon",
                     automaton.acceptsEpsilon(),
                     newAutomaton.acceptsEpsilon());
    }
    
    public void testOptionalConcat4() {
        final String[][] sample = {
                {"c", "a", "b", "d"},
                {"a", "b", "a", "b", "e"},
                {"b", "b", "e"},
                {"c", "b", "d"},
                {"d"},
                {"e"}
        };
        int[][] transitions = {{1, 0, 1, 1, 0}, {1, 0, 0, 0, 0}, {0, 0, 0, 0, 1}, {0, 0, 0, 0, 1}, {1, 1, 1, 1, 0}};
        GraphAutomatonFactory factory = new GraphAutomatonFactory();
        Automaton automaton = factory.create(sample);
        OpportunityFinder finder = new OptionalConcatFinder();
        int[] result = finder.getFirst(automaton);
        AutomatonRewriter rewriter = new OptionalConcatRewriter();
        Automaton newAutomaton = rewriter.rewrite(automaton, result);
        assertTrue("automaton", newAutomaton != null);
        assertEquals("size", transitions.length, newAutomaton.getNumberOfStates());
        assertEquals("label", "(. (? (a)) (b))", newAutomaton.getLabel(0));
        for (int i = 0; i < transitions.length; i++)
            for (int j = 0; j < transitions[i].length; j++)
                assertEquals(i + "-" + j,
                             transitions[i][j],
                             newAutomaton.get(i, j));
        assertEquals("epsilon",
                     automaton.acceptsEpsilon(),
                     newAutomaton.acceptsEpsilon());
    }
    
    public void testOptionalConcatOptional1() {
        final String[][] sample = {
                {"a", "b"},
                {"a"},
                {"b"},
                {}
        };
        int[][] transitions = {{0, 1}, {1, 1}};
        GraphAutomatonFactory factory = new GraphAutomatonFactory();
        Automaton automaton = factory.create(sample);
        OpportunityFinder finder = new OptionalConcatOptionalFinder();
        int[] result = finder.getFirst(automaton);
        AutomatonRewriter rewriter = new OptionalConcatOptionalRewriter();
        Automaton newAutomaton = rewriter.rewrite(automaton, result);
        assertTrue("automaton", newAutomaton != null);
        assertEquals("size", transitions.length, newAutomaton.getNumberOfStates());
        assertEquals("label", "(. (? (a)) (? (b)))", newAutomaton.getLabel(0));
        for (int i = 0; i < transitions.length; i++)
            for (int j = 0; j < transitions[i].length; j++)
                assertEquals(i + "-" + j,
                             transitions[i][j],
                             newAutomaton.get(i, j));
        assertEquals("epsilon",
                     automaton.acceptsEpsilon(),
                     newAutomaton.acceptsEpsilon());
    }
    
    public void testOptionalConcatOptional2() {
        final String[][] sample = {
                {"c", "a", "b"},
                {"c", "a"},
                {"c", "b"},
                {"c"}
        };
        int[][] transitions = {{0, 0, 1}, {1, 0, 1}, {0, 1, 0}};
        GraphAutomatonFactory factory = new GraphAutomatonFactory();
        Automaton automaton = factory.create(sample);
        OpportunityFinder finder = new OptionalConcatOptionalFinder();
        int[] result = finder.getFirst(automaton);
        AutomatonRewriter rewriter = new OptionalConcatOptionalRewriter();
        Automaton newAutomaton = rewriter.rewrite(automaton, result);
        assertTrue("automaton", newAutomaton != null);
        assertEquals("size", transitions.length, newAutomaton.getNumberOfStates());
        assertEquals("label", "(. (? (a)) (? (b)))", newAutomaton.getLabel(0));
        for (int i = 0; i < transitions.length; i++)
            for (int j = 0; j < transitions[i].length; j++)
                assertEquals(i + "-" + j,
                             transitions[i][j],
                             newAutomaton.get(i, j));
        assertEquals("epsilon",
                     automaton.acceptsEpsilon(),
                     newAutomaton.acceptsEpsilon());
    }
    
    public void testOptionalConcatOptional3() {
        final String[][] sample = {
                {"c", "a", "b"},
                {"c", "a"},
                {"c", "b"},
                {"c"},
                {"a", "b"},
                {"a"},
                {"b"},
                {}
        };
        int[][] transitions = {{0, 0, 1}, {1, 0, 1}, {1, 1, 1}};
        GraphAutomatonFactory factory = new GraphAutomatonFactory();
        Automaton automaton = factory.create(sample);
        OpportunityFinder finder = new OptionalConcatOptionalFinder();
        int[] result = finder.getFirst(automaton);
        AutomatonRewriter rewriter = new OptionalConcatOptionalRewriter();
        Automaton newAutomaton = rewriter.rewrite(automaton, result);
        assertTrue("automaton", newAutomaton != null);
        assertEquals("size", transitions.length, newAutomaton.getNumberOfStates());
        assertEquals("label", "(. (? (a)) (? (b)))", newAutomaton.getLabel(0));
        for (int i = 0; i < transitions.length; i++)
            for (int j = 0; j < transitions[i].length; j++)
                assertEquals(i + "-" + j,
                             transitions[i][j],
                             newAutomaton.get(i, j));
        assertEquals("epsilon",
                     automaton.acceptsEpsilon(),
                     newAutomaton.acceptsEpsilon());
    }
    
    public void testOptionalConcatOptional4() {
        final String[][] sample = {
                {"c", "a", "b", "d"},
                {"c", "a", "d"},
                {"c", "b", "d"},
                {"c", "d"}
        };
        int[][] transitions = {{0, 0, 1, 0}, {1, 0, 1, 0}, {0, 0, 0, 1}, {0, 1, 0, 0}};
        GraphAutomatonFactory factory = new GraphAutomatonFactory();
        Automaton automaton = factory.create(sample);
        OpportunityFinder finder = new OptionalConcatOptionalFinder();
        int[] result = finder.getFirst(automaton);
        AutomatonRewriter rewriter = new OptionalConcatOptionalRewriter();
        Automaton newAutomaton = rewriter.rewrite(automaton, result);
        assertTrue("automaton", newAutomaton != null);
        assertEquals("size", transitions.length, newAutomaton.getNumberOfStates());
        assertEquals("label", "(. (? (a)) (? (b)))", newAutomaton.getLabel(0));
        for (int i = 0; i < transitions.length; i++)
            for (int j = 0; j < transitions[i].length; j++)
                assertEquals(i + "-" + j,
                             transitions[i][j],
                             newAutomaton.get(i, j));
        assertEquals("epsilon",
                     automaton.acceptsEpsilon(),
                     newAutomaton.acceptsEpsilon());
    }
    
    public void testOptionalConcatOptional5() {
        final String[][] sample = {
                {"c"},
                {"e"},
                {"c", "a", "b", "d"},
                {"c", "a", "d"},
                {"c", "b", "d"},
                {"c", "d"},
                {"c"},
                {"e", "a", "b"},
                {"e", "a"},
                {"e", "b"},
                {"e"},
                {"e", "d"}
        };
        int[][] transitions = {{0, 0, 1, 0, 1}, {1, 0, 1, 0, 1}, {0, 0, 0, 0, 1}, {1, 0, 1, 0, 1}, {0, 1, 0, 1, 0}};
        GraphAutomatonFactory factory = new GraphAutomatonFactory();
        Automaton automaton = factory.create(sample);
        OpportunityFinder finder = new OptionalConcatOptionalFinder();
        int[] result = finder.getFirst(automaton);
        AutomatonRewriter rewriter = new OptionalConcatOptionalRewriter();
        Automaton newAutomaton = rewriter.rewrite(automaton, result);
        assertTrue("automaton", newAutomaton != null);
        assertEquals("size", transitions.length, newAutomaton.getNumberOfStates());
        assertEquals("label", "(. (? (a)) (? (b)))", newAutomaton.getLabel(0));
        for (int i = 0; i < transitions.length; i++)
            for (int j = 0; j < transitions[i].length; j++)
                assertEquals(i + "-" + j,
                             transitions[i][j],
                             newAutomaton.get(i, j));
        assertEquals("epsilon",
                     automaton.acceptsEpsilon(),
                     newAutomaton.acceptsEpsilon());
    }
    
    public void testOptional1() {
        final String[][] sample = {
                {"a"},
                {}
        };
        int[][] transitions = {{0, 1}, {1, 0}};
        GraphAutomatonFactory factory = new GraphAutomatonFactory();
        Automaton automaton = factory.create(sample);
        OpportunityFinder finder = new OptionalFinder();
        int[] result = finder.getFirst(automaton);
        AutomatonRewriter rewriter = new OptionalRewriter();
        Automaton newAutomaton = rewriter.rewrite(automaton, result);
        assertTrue("automaton", newAutomaton != null);
        assertEquals("size", transitions.length, newAutomaton.getNumberOfStates());
        assertEquals("label", "(? (a))", newAutomaton.getLabel(0));
        for (int i = 0; i < transitions.length; i++)
            for (int j = 0; j < transitions[i].length; j++)
                assertEquals(i + "-" + j,
                             transitions[i][j],
                             newAutomaton.get(i, j));
        assertTrue("epsilon", !newAutomaton.acceptsEpsilon());
    }
    
}
