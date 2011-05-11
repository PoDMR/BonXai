/*
 * Created on Sep 9, 2008
 * Modified on $Date: 2009-10-26 15:29:20 $
 */
package gjb.flt.regex.infer.rwr;

import gjb.flt.regex.infer.rwr.impl.Automaton;
import gjb.flt.regex.infer.rwr.impl.GraphAutomatonFactory;
import gjb.flt.regex.infer.rwr.repairers.ConcatOptionalRepairFinder;
import gjb.flt.regex.infer.rwr.repairers.ConcatOptionalRepairer;
import gjb.flt.regex.infer.rwr.repairers.DisjunctionRepairFinder;
import gjb.flt.regex.infer.rwr.repairers.DisjunctionRepairer;
import gjb.flt.regex.infer.rwr.repairers.OptionalConcatOptionalRepairFinder;
import gjb.flt.regex.infer.rwr.repairers.OptionalConcatOptionalRepairer;
import gjb.flt.regex.infer.rwr.repairers.OptionalConcatRepairFinder;
import gjb.flt.regex.infer.rwr.repairers.OptionalConcatRepairer;
import gjb.flt.regex.infer.rwr.repairers.RepairOpportunityFinder;
import gjb.flt.regex.infer.rwr.rewriters.ConcatOptionalFinder;
import gjb.flt.regex.infer.rwr.rewriters.DisjunctionFinder;
import gjb.flt.regex.infer.rwr.rewriters.OpportunityFinder;
import gjb.flt.regex.infer.rwr.rewriters.OptionalConcatFinder;
import gjb.flt.regex.infer.rwr.rewriters.OptionalConcatOptionalFinder;

import java.util.List;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * @author gjb
 * @version $Revision: 1.1 $
 * 
 */
public class RepairRewriteStepTest extends TestCase {

    public static Test suite() {
        return new TestSuite(RepairRewriteStepTest.class);
    }

    public void testRepairDisjunction1() {
        final String[][] sample = {
                {"a", "c"},
                {"a", "b"},
                {"b"}
        };
        GraphAutomatonFactory factory = new GraphAutomatonFactory();
        Automaton automaton = factory.create(sample);
        RepairOpportunityFinder finder = new DisjunctionRepairFinder();
        int[] result = finder.getFirst(automaton);
        assertTrue("result", result != null);
        assertEquals("a", 0, result[0]);
        assertEquals("b", 1, result[1]);
        List<int[]> results = finder.getAll(automaton);
        assertEquals("size", 2, results.size());
        assertTrue("pair 1", results.get(0)[0] == 0 && results.get(0)[1] == 1);
        assertTrue("pair 2", results.get(1)[0] == 1 && results.get(1)[1] == 2);
    }

    public void testRepairDisjunction2() {
        final String[][] sample = {
                {"c", "b"},
                {"c", "a"},
                {"a"}
        };
        GraphAutomatonFactory factory = new GraphAutomatonFactory();
        Automaton automaton = factory.create(sample);
        RepairOpportunityFinder finder = new DisjunctionRepairFinder();
        int[] result = finder.getFirst(automaton);
        assertTrue("result", result != null);
        assertEquals("a", 0, result[0]);
        assertEquals("b", 1, result[1]);
        List<int[]> results = finder.getAll(automaton);
        assertEquals("size", 2, results.size());
        assertTrue("pair 1", results.get(0)[0] == 0 && results.get(0)[1] == 1);
        assertTrue("pair 2", results.get(1)[0] == 0 && results.get(1)[1] == 2);
    }
    
    public void testRepairDisjunctionStep1() {
        final String[][] sample = {
                {"a", "c"},
                {"a", "b"},
                {"b"}
        };
        GraphAutomatonFactory factory = new GraphAutomatonFactory();
        Automaton automaton = factory.create(sample);
        OpportunityFinder finder = new DisjunctionFinder();
        int[] result = finder.getFirst(automaton);
        assertTrue("stuck", result == null);
        RepairOpportunityFinder repairFinder = new DisjunctionRepairFinder();
        result = repairFinder.getFirst(automaton);
        assertTrue("repair", result != null);
        AutomatonRewriter rewriter = new DisjunctionRepairer();
        automaton = rewriter.rewrite(automaton, result);
        result = finder.getFirst(automaton);
        assertTrue("result", result != null);
        assertEquals("a", 0, result[0]);
        assertEquals("b", 1, result[1]);
    }
    
    public void testRepairDisjunctionStep2() {
        final String[][] sample = {
                {"a", "c"},
                {"a", "b"},
                {"b"}
        };
        GraphAutomatonFactory factory = new GraphAutomatonFactory();
        Automaton automaton = factory.create(sample);
        int[] result = {1, 2};
        AutomatonRewriter rewriter = new DisjunctionRepairer();
        automaton = rewriter.rewrite(automaton, result);
        OpportunityFinder finder = new DisjunctionFinder();
        result = finder.getFirst(automaton);
        assertTrue("result", result != null);
        assertEquals("b", 1, result[0]);
        assertEquals("c", 2, result[1]);
    }

    public void testRepairConcatOptionalStep1() {
        final String[][] sample = {
                {"a"},
                {"a", "b", "a"}
        };
        GraphAutomatonFactory factory = new GraphAutomatonFactory();
        Automaton automaton = factory.create(sample);
        RepairOpportunityFinder repairFinder = new ConcatOptionalRepairFinder();
        List<int[]> results = repairFinder.getAll(automaton);
        assertEquals("size", 1, results.size());
        assertEquals("a", 0, results.get(0)[0]);
        assertEquals("b", 1, results.get(0)[1]);
        AutomatonRewriter rewriter = new ConcatOptionalRepairer();
        automaton = rewriter.rewrite(automaton, results.get(0));
        OpportunityFinder finder = new ConcatOptionalFinder();
        int[] result = finder.getFirst(automaton);
        assertTrue("result", result != null);
        assertEquals("a", 0, result[0]);
        assertEquals("b", 1, result[1]);
    }
    
    public void testRepairConcatOptionalStep2() {
        final String[][] sample = {
                {"b"},
                {"b", "a", "b"}
        };
        GraphAutomatonFactory factory = new GraphAutomatonFactory();
        Automaton automaton = factory.create(sample);
        RepairOpportunityFinder repairFinder = new ConcatOptionalRepairFinder();
        List<int[]> results = repairFinder.getAll(automaton);
        assertEquals("size", 1, results.size());
        assertEquals("b", 1, results.get(0)[0]);
        assertEquals("a", 0, results.get(0)[1]);
        AutomatonRewriter rewriter = new ConcatOptionalRepairer();
        automaton = rewriter.rewrite(automaton, results.get(0));
        OpportunityFinder finder = new ConcatOptionalFinder();
        int[] result = finder.getFirst(automaton);
        assertTrue("result", result != null);
        assertEquals("b", 1, result[0]);
        assertEquals("a", 0, result[1]);
    }
    
    public void testRepairOptionalConcat1() {
        final String[][] sample = {
                {"a", "b"},
                {"c", "b"},
                {"b"}
        };
        GraphAutomatonFactory factory = new GraphAutomatonFactory();
        Automaton automaton = factory.create(sample);
        RepairOpportunityFinder finder = new OptionalConcatRepairFinder();
        int[] result = finder.getFirst(automaton);
        assertTrue("result", result != null);
        assertEquals("a", 0, result[0]);
        assertEquals("b", 1, result[1]);
        List<int[]> results = finder.getAll(automaton);
        assertEquals("size", 2, results.size());
        assertTrue("pair 1", results.get(0)[0] == 0 && results.get(0)[1] == 1);
        assertTrue("pair 2", results.get(1)[0] == 2 && results.get(1)[1] == 1);
    }

    public void testRepairOptionalConcatStep1() {
        final String[][] sample = {
                {"a"},
                {"a", "b", "a"}
        };
        GraphAutomatonFactory factory = new GraphAutomatonFactory();
        Automaton automaton = factory.create(sample);
        RepairOpportunityFinder repairFinder = new OptionalConcatRepairFinder();
        List<int[]> results = repairFinder.getAll(automaton);
        assertEquals("size", 1, results.size());
        assertEquals("b", 1, results.get(0)[0]);
        assertEquals("a", 0, results.get(0)[1]);
        AutomatonRewriter rewriter = new OptionalConcatRepairer();
        automaton = rewriter.rewrite(automaton, results.get(0));
        OpportunityFinder finder = new OptionalConcatFinder();
        int[] result = finder.getFirst(automaton);
        assertTrue("result", result != null);
        assertEquals("b", 1, result[0]);
        assertEquals("a", 0, result[1]);
    }
    
    public void testRepairOptionalConcatStep2() {
        final String[][] sample = {
                {"b"},
                {"b", "a", "b"}
        };
        GraphAutomatonFactory factory = new GraphAutomatonFactory();
        Automaton automaton = factory.create(sample);
        RepairOpportunityFinder repairFinder = new OptionalConcatRepairFinder();
        List<int[]> results = repairFinder.getAll(automaton);
        assertEquals("size", 1, results.size());
        assertEquals("a", 0, results.get(0)[0]);
        assertEquals("b", 1, results.get(0)[1]);
        AutomatonRewriter rewriter = new OptionalConcatRepairer();
        automaton = rewriter.rewrite(automaton, results.get(0));
        OpportunityFinder finder = new OptionalConcatFinder();
        int[] result = finder.getFirst(automaton);
        assertTrue("result", result != null);
        assertEquals("a", 0, result[0]);
        assertEquals("b", 1, result[1]);
    }
    
    public void testRepairOptionalConcatOptionalStep1() {
        final String[][] sample = {
                {"a"},
                {"a", "b"},
                {"b"}
        };
        GraphAutomatonFactory factory = new GraphAutomatonFactory();
        Automaton automaton = factory.create(sample);
        RepairOpportunityFinder repairFinder = new OptionalConcatOptionalRepairFinder();
        List<int[]> results = repairFinder.getAll(automaton);
        assertEquals("size", 1, results.size());
        assertEquals("a", 0, results.get(0)[0]);
        assertEquals("b", 1, results.get(0)[1]);
        AutomatonRewriter rewriter = new OptionalConcatOptionalRepairer();
        automaton = rewriter.rewrite(automaton, results.get(0));
        OpportunityFinder finder = new OptionalConcatOptionalFinder();
        int[] result = finder.getFirst(automaton);
        assertTrue("result", result != null);
        assertEquals("a", 0, result[0]);
        assertEquals("b", 1, result[1]);
    }
    
    public void testRepairOptionalConcatOptionalStep2() {
        final String[][] sample = {
                {"c", "a", "e"},
                {"d", "a", "f"},
                {"c", "a", "b", "e"},
                {"d", "a", "b", "f"},
                {"c", "b", "e"},
                {"d", "b", "f"}
        };
        GraphAutomatonFactory factory = new GraphAutomatonFactory();
        Automaton automaton = factory.create(sample);
        RepairOpportunityFinder repairFinder = new OptionalConcatOptionalRepairFinder();
        List<int[]> results = repairFinder.getAll(automaton);
        assertEquals("size", 9, results.size());
        assertEquals("a", 0, results.get(0)[0]);
        assertEquals("b", 1, results.get(0)[1]);
        AutomatonRewriter rewriter = new OptionalConcatOptionalRepairer();
        automaton = rewriter.rewrite(automaton, results.get(0));
        OpportunityFinder finder = new OptionalConcatOptionalFinder();
        int[] result = finder.getFirst(automaton);
        assertTrue("result", result != null);
        assertEquals("a", 0, result[0]);
        assertEquals("b", 1, result[1]);
    }
    
}
