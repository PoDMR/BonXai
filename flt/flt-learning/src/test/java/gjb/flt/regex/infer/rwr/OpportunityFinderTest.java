/*
 * Created on Sep 8, 2008
 * Modified on $Date: 2009-10-26 15:29:20 $
 */
package gjb.flt.regex.infer.rwr;

import gjb.flt.regex.infer.rwr.impl.Automaton;
import gjb.flt.regex.infer.rwr.impl.GraphAutomatonFactory;
import gjb.flt.regex.infer.rwr.rewriters.ConcatFinder;
import gjb.flt.regex.infer.rwr.rewriters.ConcatOptionalFinder;
import gjb.flt.regex.infer.rwr.rewriters.DisjunctionFinder;
import gjb.flt.regex.infer.rwr.rewriters.OpportunityFinder;
import gjb.flt.regex.infer.rwr.rewriters.OptionalConcatFinder;
import gjb.flt.regex.infer.rwr.rewriters.OptionalConcatOptionalFinder;
import gjb.flt.regex.infer.rwr.rewriters.RepetitionFinder;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * @author gjb
 * @version $Revision: 1.1 $
 * 
 */
public class OpportunityFinderTest extends TestCase {

    public static Test suite() {
        return new TestSuite(OpportunityFinderTest.class);
    }

    public void testDisjunction1() {
        final String[][] sample = {
                {"a", "b", "d"},
                {"a", "c", "d"}
        };
        GraphAutomatonFactory factory = new GraphAutomatonFactory();
        Automaton automaton = factory.create(sample);
        OpportunityFinder finder = new DisjunctionFinder();
        int[] result = finder.getFirst(automaton);
        assertTrue("result", result != null);
        assertEquals("size", 2, result.length);
        assertTrue("contains b",
                   automaton.getLabel(result[0]).equals("(b)") ||
                   automaton.getLabel(result[1]).equals("(b)"));
        assertTrue("contains c",
                   automaton.getLabel(result[0]).equals("(c)") ||
                   automaton.getLabel(result[1]).equals("(c)"));
    }

    public void testDisjunction2() {
        final String[][] sample = {
                {"b", "d"},
                {"c", "d"}
        };
        GraphAutomatonFactory factory = new GraphAutomatonFactory();
        Automaton automaton = factory.create(sample);
        OpportunityFinder finder = new DisjunctionFinder();
        int[] result = finder.getFirst(automaton);
        assertTrue("result", result != null);
        assertEquals("size", 2, result.length);
        assertTrue("contains b",
                   automaton.getLabel(result[0]).equals("(b)") ||
                   automaton.getLabel(result[1]).equals("(b)"));
        assertTrue("contains c",
                   automaton.getLabel(result[0]).equals("(c)") ||
                   automaton.getLabel(result[1]).equals("(c)"));
    }

    public void testDisjunction3() {
        final String[][] sample = {
                {"a", "b"},
                {"a", "c"}
        };
        GraphAutomatonFactory factory = new GraphAutomatonFactory();
        Automaton automaton = factory.create(sample);
        OpportunityFinder finder = new DisjunctionFinder();
        int[] result = finder.getFirst(automaton);
        assertTrue("result", result != null);
        assertEquals("size", 2, result.length);
        assertTrue("contains b",
                   automaton.getLabel(result[0]).equals("(b)") ||
                   automaton.getLabel(result[1]).equals("(b)"));
        assertTrue("contains c",
                   automaton.getLabel(result[0]).equals("(c)") ||
                   automaton.getLabel(result[1]).equals("(c)"));
    }
    
    public void testDisjunction4() {
        final String[][] sample = {
                {"b"},
                {"c"}
        };
        GraphAutomatonFactory factory = new GraphAutomatonFactory();
        Automaton automaton = factory.create(sample);
        OpportunityFinder finder = new DisjunctionFinder();
        int[] result = finder.getFirst(automaton);
        assertTrue("result", result != null);
        assertEquals("size", 2, result.length);
        assertTrue("contains b",
                   automaton.getLabel(result[0]).equals("(b)") ||
                   automaton.getLabel(result[1]).equals("(b)"));
        assertTrue("contains c",
                   automaton.getLabel(result[0]).equals("(c)") ||
                   automaton.getLabel(result[1]).equals("(c)"));
    }

    public void testDisjunction5() {
        final String[][] sample = {
                {"+ (b)"},
                {"c"},
                {"c", "+ (b)", "c"},
                {"c", "c", "+ (b)"}
        };
        GraphAutomatonFactory factory = new GraphAutomatonFactory();
        Automaton automaton = factory.create(sample);
        OpportunityFinder finder = new DisjunctionFinder();
        int[] result = finder.getFirst(automaton);
        assertTrue("result", result != null);
        assertEquals("size", 2, result.length);
        assertTrue("contains b",
                   automaton.getLabel(result[0]).equals("(+ (b))") ||
                   automaton.getLabel(result[1]).equals("(+ (b))"));
        assertTrue("contains c",
                   automaton.getLabel(result[0]).equals("(c)") ||
                   automaton.getLabel(result[1]).equals("(c)"));
    }

    public void testDisjunction6() {
        final String[][] sample = {
                {"a", "+ (b)", "d"},
                {"a", "c", "d"},
                {"a", "c", "+ (b)", "c", "d"},
                {"a", "c", "c", "+ (b)", "d"}
        };
        GraphAutomatonFactory factory = new GraphAutomatonFactory();
        Automaton automaton = factory.create(sample);
        OpportunityFinder finder = new DisjunctionFinder();
        int[] result = finder.getFirst(automaton);
        assertTrue("result", result != null);
        assertEquals("size", 2, result.length);
        assertTrue("contains b",
                   automaton.getLabel(result[0]).equals("(+ (b))") ||
                   automaton.getLabel(result[1]).equals("(+ (b))"));
        assertTrue("contains c",
                   automaton.getLabel(result[0]).equals("(c)") ||
                   automaton.getLabel(result[1]).equals("(c)"));
    }

    public void testDisjunction7() {
        final String[][] sample = {
                {"a", "+ (b)"},
                {"a", "c"}
        };
        GraphAutomatonFactory factory = new GraphAutomatonFactory();
        Automaton automaton = factory.create(sample);
        OpportunityFinder finder = new DisjunctionFinder();
        int[] result = finder.getFirst(automaton);
        assertTrue("result", result != null);
        assertEquals("size", 2, result.length);
        assertTrue("contains (+ (b))",
                   automaton.getLabel(result[0]).equals("(+ (b))") ||
                   automaton.getLabel(result[1]).equals("(+ (b))"));
        assertTrue("contains c",
                   automaton.getLabel(result[0]).equals("(c)") ||
                   automaton.getLabel(result[1]).equals("(c)"));
    }
    
    public void testDisjunction8() {
        final String[][] sample = {
                {"a", "+ (b)", "d"},
                {"a", "+ (c)", "d"}
        };
        GraphAutomatonFactory factory = new GraphAutomatonFactory();
        Automaton automaton = factory.create(sample);
        OpportunityFinder finder = new DisjunctionFinder();
        int[] result = finder.getFirst(automaton);
        assertTrue("result", result != null);
        assertEquals("size", 2, result.length);
        assertTrue("contains b",
                   automaton.getLabel(result[0]).equals("(+ (b))") ||
                   automaton.getLabel(result[1]).equals("(+ (b))"));
        assertTrue("contains c",
                   automaton.getLabel(result[0]).equals("(+ (c))") ||
                   automaton.getLabel(result[1]).equals("(+ (c))"));
    }

    public void testDisjunction9() {
        final String[][] sample = {
                {"a", "e", "d"},
                {"b", "d"},
                {"c", "d"}
        };
        GraphAutomatonFactory factory = new GraphAutomatonFactory();
        Automaton automaton = factory.create(sample);
        OpportunityFinder finder = new DisjunctionFinder();
        int[] result = finder.getFirst(automaton);
        assertTrue("result", result != null);
        assertEquals("size", 2, result.length);
        assertTrue("contains b",
                   automaton.getLabel(result[0]).equals("(b)") ||
                   automaton.getLabel(result[1]).equals("(b)"));
        assertTrue("contains c",
                   automaton.getLabel(result[0]).equals("(c)") ||
                   automaton.getLabel(result[1]).equals("(c)"));
    }

    public void testDisjunction10() {
        final String[][] sample = {
                {"b"},
                {"c"},
                {}
        };
        GraphAutomatonFactory factory = new GraphAutomatonFactory();
        Automaton automaton = factory.create(sample);
        OpportunityFinder finder = new DisjunctionFinder();
        int[] result = finder.getFirst(automaton);
        assertTrue("result", result != null);
        assertEquals("size", 2, result.length);
        assertTrue("contains b",
                   automaton.getLabel(result[0]).equals("(b)") ||
                   automaton.getLabel(result[1]).equals("(b)"));
        assertTrue("contains c",
                   automaton.getLabel(result[0]).equals("(c)") ||
                   automaton.getLabel(result[1]).equals("(c)"));
    }

    public void testDisjunction11() {
        final String[][] sample = {
                {"b"},
                {"c"},
                {"b", "c", "c", "b", "b", "c"},
                {}
        };
        GraphAutomatonFactory factory = new GraphAutomatonFactory();
        Automaton automaton = factory.create(sample);
        OpportunityFinder finder = new DisjunctionFinder();
        int[] result = finder.getFirst(automaton);
        assertTrue("result", result != null);
        assertEquals("size", 2, result.length);
        assertTrue("contains b",
                   automaton.getLabel(result[0]).equals("(b)") ||
                   automaton.getLabel(result[1]).equals("(b)"));
        assertTrue("contains c",
                   automaton.getLabel(result[0]).equals("(c)") ||
                   automaton.getLabel(result[1]).equals("(c)"));
    }
    
    public void testDisjunctionFailure1() {
        final String[][] sample = {
                {"a", "b", "d"},
                {"a", "c", "d"},
                {"a", "b", "c", "d"}
        };
        GraphAutomatonFactory factory = new GraphAutomatonFactory();
        Automaton automaton = factory.create(sample);
        OpportunityFinder finder = new DisjunctionFinder();
        int[] result = finder.getFirst(automaton);
        assertEquals("no result", null, result);
    }

    public void testDisjunctionFailure2() {
        final String[][] sample = {
                {"a", "(+ (b))", "d"},
                {"a", "c", "d"},
                {"a", "c", "(+ (b))", "c", "d"}
        };
        GraphAutomatonFactory factory = new GraphAutomatonFactory();
        Automaton automaton = factory.create(sample);
        OpportunityFinder finder = new DisjunctionFinder();
        int[] result = finder.getFirst(automaton);
        assertEquals("no result", null, result);
    }
    
    public void testDisjunctionFailure3() {
        final String[][] sample = {
                {"a"},
                {"b"},
                {"b", "b"}
        };
        GraphAutomatonFactory factory = new GraphAutomatonFactory();
        Automaton automaton = factory.create(sample);
        OpportunityFinder finder = new DisjunctionFinder();
        int[] result = finder.getFirst(automaton);
        assertTrue("result", result == null);
    }
    
    public void testConcat1() {
        final String[][] sample = {
                {"a", "b"}
        };
        GraphAutomatonFactory factory = new GraphAutomatonFactory();
        Automaton automaton = factory.create(sample);
        OpportunityFinder finder = new ConcatFinder();
        int[] result = finder.getFirst(automaton);
        assertTrue("result", result != null);
        assertEquals("size", 2, result.length);
        assertTrue("contains a",
                   automaton.getLabel(result[0]).equals("(a)"));
        assertTrue("contains b",
                   automaton.getLabel(result[1]).equals("(b)"));
    }
    
    public void testConcat2() {
        final String[][] sample = {
                {"a", "b", "c"}
        };
        GraphAutomatonFactory factory = new GraphAutomatonFactory();
        Automaton automaton = factory.create(sample);
        OpportunityFinder finder = new ConcatFinder();
        int[] result = finder.getFirst(automaton);
        assertTrue("result", result != null);
        assertEquals("size", 2, result.length);
        assertTrue("contains a",
                   automaton.getLabel(result[0]).equals("(a)"));
        assertTrue("contains b",
                   automaton.getLabel(result[1]).equals("(b)"));
    }
    
    public void testConcat3() {
        final String[][] sample = {
                {"a", "b", "c"},
                {"a", "b", "d"}
        };
        GraphAutomatonFactory factory = new GraphAutomatonFactory();
        Automaton automaton = factory.create(sample);
        OpportunityFinder finder = new ConcatFinder();
        int[] result = finder.getFirst(automaton);
        assertTrue("result", result != null);
        assertEquals("size", 2, result.length);
        assertTrue("contains a",
                   automaton.getLabel(result[0]).equals("(a)"));
        assertTrue("contains b",
                   automaton.getLabel(result[1]).equals("(b)"));
    }
    
    public void testConcat4() {
        final String[][] sample = {
                {"e", "a", "b", "c"},
                {"f", "a", "b", "d"}
        };
        GraphAutomatonFactory factory = new GraphAutomatonFactory();
        Automaton automaton = factory.create(sample);
        OpportunityFinder finder = new ConcatFinder();
        int[] result = finder.getFirst(automaton);
        assertTrue("result", result != null);
        assertEquals("size", 2, result.length);
        assertTrue("contains a",
                   automaton.getLabel(result[0]).equals("(a)"));
        assertTrue("contains b",
                   automaton.getLabel(result[1]).equals("(b)"));
    }
    
    public void testConcat5() {
        final String[][] sample = {
                {"e", "a", "b", "c"},
                {"f", "a", "b", "d"},
                {"f", "d"}
        };
        GraphAutomatonFactory factory = new GraphAutomatonFactory();
        Automaton automaton = factory.create(sample);
        OpportunityFinder finder = new ConcatFinder();
        int[] result = finder.getFirst(automaton);
        assertTrue("result", result != null);
        assertEquals("size", 2, result.length);
        assertTrue("contains a",
                   automaton.getLabel(result[0]).equals("(a)"));
        assertTrue("contains b",
                   automaton.getLabel(result[1]).equals("(b)"));
    }
    
    public void testConcat6() {
        final String[][] sample = {
                {"+ (a)", "b"}
        };
        GraphAutomatonFactory factory = new GraphAutomatonFactory();
        Automaton automaton = factory.create(sample);
        OpportunityFinder finder = new ConcatFinder();
        int[] result = finder.getFirst(automaton);
        assertTrue("result", result != null);
        assertEquals("size", 2, result.length);
        assertTrue("contains (+ (a))",
                   automaton.getLabel(result[0]).equals("(+ (a))"));
        assertTrue("contains b",
                   automaton.getLabel(result[1]).equals("(b)"));
    }

    public void testConcat7() {
        final String[][] sample = {
                {"a", "b"},
                {}
        };
        GraphAutomatonFactory factory = new GraphAutomatonFactory();
        Automaton automaton = factory.create(sample);
        OpportunityFinder finder = new ConcatFinder();
        int[] result = finder.getFirst(automaton);
        assertTrue("result", result != null);
        assertEquals("size", 2, result.length);
        assertTrue("contains a",
                   automaton.getLabel(result[0]).equals("(a)"));
        assertTrue("contains b",
                   automaton.getLabel(result[1]).equals("(b)"));
    }
    
    public void testConcat8() {
        final String[][] sample = {
                {"a", "b"},
                {"a", "b", "a", "b"}
        };
        GraphAutomatonFactory factory = new GraphAutomatonFactory();
        Automaton automaton = factory.create(sample);
        OpportunityFinder finder = new ConcatFinder();
        int[] result = finder.getFirst(automaton);
        assertTrue("result", result != null);
        assertEquals("size", 2, result.length);
        assertTrue("contains a",
                   automaton.getLabel(result[0]).equals("(a)"));
        assertTrue("contains b",
                   automaton.getLabel(result[1]).equals("(b)"));
    }
    
    public void testConcat9() {
        final String[][] sample = {
                {"b", "a"}
        };
        GraphAutomatonFactory factory = new GraphAutomatonFactory();
        Automaton automaton = factory.create(sample);
        OpportunityFinder finder = new ConcatFinder();
        int[] result = finder.getFirst(automaton);
        assertTrue("result", result != null);
        assertEquals("size", 2, result.length);
        assertTrue("contains b",
                   automaton.getLabel(result[0]).equals("(b)"));
        assertTrue("contains a",
                   automaton.getLabel(result[1]).equals("(a)"));
    }
    
    public void testConcatFailure1() {
        final String[][] sample = {
                {"e", "a", "b", "c"},
                {"f", "a", "b", "d"},
                {"f", "d"},
                {"f", "a", "c"}
        };
        GraphAutomatonFactory factory = new GraphAutomatonFactory();
        Automaton automaton = factory.create(sample);
        OpportunityFinder finder = new ConcatFinder();
        int[] result = finder.getFirst(automaton);
        assertEquals("result", null, result);
    }
    
    public void testConcatFailure2() {
        final String[][] sample = {
                {"e", "a", "b", "c"},
                {"f", "a", "b", "d"},
                {"f", "d"},
                {"e", "b", "c"}
        };
        GraphAutomatonFactory factory = new GraphAutomatonFactory();
        Automaton automaton = factory.create(sample);
        OpportunityFinder finder = new ConcatFinder();
        int[] result = finder.getFirst(automaton);
        assertEquals("result", null, result);
    }
    
    public void testConcatFailure3() {
        final String[][] sample = {
                {"a", "b"},
                {"b"},
                {"a"},
                {}
        };
        GraphAutomatonFactory factory = new GraphAutomatonFactory();
        Automaton automaton = factory.create(sample);
        OpportunityFinder finder = new ConcatFinder();
        int[] result = finder.getFirst(automaton);
        assertTrue("result", result == null);
    }
    
    public void testRepetition1() {
        final String[][] sample = {
                {"a", "a", "b"}
        };
        GraphAutomatonFactory factory = new GraphAutomatonFactory();
        Automaton automaton = factory.create(sample);
        OpportunityFinder finder = new RepetitionFinder();
        int[] result = finder.getFirst(automaton);
        assertTrue("result", result != null);
        assertEquals("size", 1, result.length);
        assertTrue("contains a",
                   automaton.getLabel(result[0]).equals("(a)"));
    }
    
    public void testRepetition2() {
        final String[][] sample = {
                {"e", "a", "a", "b"},
                {"f", "a", "c"}
        };
        GraphAutomatonFactory factory = new GraphAutomatonFactory();
        Automaton automaton = factory.create(sample);
        OpportunityFinder finder = new RepetitionFinder();
        int[] result = finder.getFirst(automaton);
        assertTrue("result", result != null);
        assertEquals("size", 1, result.length);
        assertTrue("contains a",
                   automaton.getLabel(result[0]).equals("(a)"));
    }
    
    public void testConcatOptional1() {
        final String[][] sample = {
                {"a", "b"},
                {"a"}
        };
        GraphAutomatonFactory factory = new GraphAutomatonFactory();
        Automaton automaton = factory.create(sample);
        OpportunityFinder finder = new ConcatOptionalFinder();
        int[] result = finder.getFirst(automaton);
        assertTrue("result", result != null);
        assertEquals("size", 2, result.length);
        assertTrue("contains (+ (a))",
                   automaton.getLabel(result[0]).equals("(a)"));
        assertTrue("contains b",
                   automaton.getLabel(result[1]).equals("(b)"));
    }

    public void testConcatOptional2() {
        final String[][] sample = {
                {"a", "b", "c"},
                {"a", "c"}
        };
        GraphAutomatonFactory factory = new GraphAutomatonFactory();
        Automaton automaton = factory.create(sample);
        OpportunityFinder finder = new ConcatOptionalFinder();
        int[] result = finder.getFirst(automaton);
        assertTrue("result", result != null);
        assertEquals("size", 2, result.length);
        assertTrue("contains (+ (a))",
                   automaton.getLabel(result[0]).equals("(a)"));
        assertTrue("contains b",
                   automaton.getLabel(result[1]).equals("(b)"));
    }
    
    public void testConcatOptional3() {
        final String[][] sample = {
                {"a", "b", "c"},
                {"a", "b", "d"},
                {"a", "c"},
                {"a", "d"}
        };
        GraphAutomatonFactory factory = new GraphAutomatonFactory();
        Automaton automaton = factory.create(sample);
        OpportunityFinder finder = new ConcatOptionalFinder();
        int[] result = finder.getFirst(automaton);
        assertTrue("result", result != null);
        assertEquals("size", 2, result.length);
        assertTrue("contains (+ (a))",
                   automaton.getLabel(result[0]).equals("(a)"));
        assertTrue("contains b",
                   automaton.getLabel(result[1]).equals("(b)"));
    }
    
    public void testConcatOptional4() {
        final String[][] sample = {
                {"e", "a", "b", "c"},
                {"a", "b", "d"},
                {"a", "c"},
                {"a", "d"}
        };
        GraphAutomatonFactory factory = new GraphAutomatonFactory();
        Automaton automaton = factory.create(sample);
        OpportunityFinder finder = new ConcatOptionalFinder();
        int[] result = finder.getFirst(automaton);
        assertTrue("result", result != null);
        assertEquals("size", 2, result.length);
        assertTrue("contains (+ (a))",
                   automaton.getLabel(result[0]).equals("(a)"));
        assertTrue("contains b",
                   automaton.getLabel(result[1]).equals("(b)"));
    }
    
    public void testConcatOptional5() {
        final String[][] sample = {
                {"e", "a", "b", "c"},
                {"a", "b", "d"},
                {"a", "b", "a", "c"},
                {"a", "c"},
                {"a", "d"},
                {"e", "a", "a", "d"}
        };
        GraphAutomatonFactory factory = new GraphAutomatonFactory();
        Automaton automaton = factory.create(sample);
        OpportunityFinder finder = new ConcatOptionalFinder();
        int[] result = finder.getFirst(automaton);
        assertTrue("result", result != null);
        assertEquals("size", 2, result.length);
        assertTrue("contains (+ (a))",
                   automaton.getLabel(result[0]).equals("(a)"));
        assertTrue("contains b",
                   automaton.getLabel(result[1]).equals("(b)"));
    }
    
    public void testConcatOptional6() {
        final String[][] sample = {
                {"a", "b"},
                {"a"},
                {}
        };
        GraphAutomatonFactory factory = new GraphAutomatonFactory();
        Automaton automaton = factory.create(sample);
        OpportunityFinder finder = new ConcatOptionalFinder();
        int[] result = finder.getFirst(automaton);
        assertTrue("result", result != null);
        assertEquals("size", 2, result.length);
        assertTrue("contains (+ (a))",
                   automaton.getLabel(result[0]).equals("(a)"));
        assertTrue("contains b",
                   automaton.getLabel(result[1]).equals("(b)"));
    }

    public void testConcatOptionalFailure1() {
        final String[][] sample = {
                {"e", "a", "b", "c"},
                {"a", "b", "a"},
                {"a", "b", "a", "c"},
                {"a", "c"},
                {"a", "d"},
                {"e", "a", "a", "d"}
        };
        GraphAutomatonFactory factory = new GraphAutomatonFactory();
        Automaton automaton = factory.create(sample);
        OpportunityFinder finder = new ConcatOptionalFinder();
        int[] result = finder.getFirst(automaton);
        assertTrue("result", result == null);
    }
    
    public void testConcatOptionalFailure2() {
        final String[][] sample = {
                {"e", "a", "b", "c"},
                {"a", "b", "d"},
                {"a", "c"},
                {"a", "d"},
                {"e", "a", "a", "d"}
        };
        GraphAutomatonFactory factory = new GraphAutomatonFactory();
        Automaton automaton = factory.create(sample);
        OpportunityFinder finder = new ConcatOptionalFinder();
        int[] result = finder.getFirst(automaton);
        assertTrue("result", result == null);
    }
    
    public void testConcatOptionalFailure3() {
        final String[][] sample = {
                {"a", "b"},
                {"b"}
        };
        GraphAutomatonFactory factory = new GraphAutomatonFactory();
        Automaton automaton = factory.create(sample);
        OpportunityFinder finder = new ConcatOptionalFinder();
        int[] result = finder.getFirst(automaton);
        assertTrue("result", result == null);
    }
    
    public void testConcatOptionalFailure4() {
        final String[][] sample = {
                {"a", "b"},
                {"b"},
                {"a"},
                {}
        };
        GraphAutomatonFactory factory = new GraphAutomatonFactory();
        Automaton automaton = factory.create(sample);
        OpportunityFinder finder = new ConcatOptionalFinder();
        int[] result = finder.getFirst(automaton);
        assertTrue("result", result == null);
    }
    
    public void testOptionalConcat1() {
        final String[][] sample = {
                {"a", "b"},
                {"b"}
        };
        GraphAutomatonFactory factory = new GraphAutomatonFactory();
        Automaton automaton = factory.create(sample);
        OpportunityFinder finder = new OptionalConcatFinder();
        int[] result = finder.getFirst(automaton);
        assertTrue("result", result != null);
        assertEquals("size", 2, result.length);
        assertTrue("contains (+ (a))",
                   automaton.getLabel(result[0]).equals("(a)"));
        assertTrue("contains b",
                   automaton.getLabel(result[1]).equals("(b)"));
    }

    public void testOptionalConcat2() {
        final String[][] sample = {
                {"a", "b"},
                {"b"},
                {}
        };
        GraphAutomatonFactory factory = new GraphAutomatonFactory();
        Automaton automaton = factory.create(sample);
        OpportunityFinder finder = new OptionalConcatFinder();
        int[] result = finder.getFirst(automaton);
        assertTrue("result", result != null);
        assertEquals("size", 2, result.length);
        assertTrue("contains (+ (a))",
                   automaton.getLabel(result[0]).equals("(a)"));
        assertTrue("contains b",
                   automaton.getLabel(result[1]).equals("(b)"));
    }
    
    public void testOptionalConcat3() {
        final String[][] sample = {
                {"c", "a", "b"},
                {"c", "b"},
                {"d", "a", "b"},
                {"d", "b"}
        };
        GraphAutomatonFactory factory = new GraphAutomatonFactory();
        Automaton automaton = factory.create(sample);
        OpportunityFinder finder = new OptionalConcatFinder();
        int[] result = finder.getFirst(automaton);
        assertTrue("result", result != null);
        assertEquals("size", 2, result.length);
        assertTrue("contains (+ (a))",
                   automaton.getLabel(result[0]).equals("(a)"));
        assertTrue("contains b",
                   automaton.getLabel(result[1]).equals("(b)"));
    }

    public void testOptionalConcat4() {
        final String[][] sample = {
                {"c", "a", "b", "e"},
                {"c", "b"},
                {"d", "a", "b"},
                {"d", "b"}
        };
        GraphAutomatonFactory factory = new GraphAutomatonFactory();
        Automaton automaton = factory.create(sample);
        OpportunityFinder finder = new OptionalConcatFinder();
        int[] result = finder.getFirst(automaton);
        assertTrue("result", result != null);
        assertEquals("size", 2, result.length);
        assertTrue("contains (+ (a))",
                   automaton.getLabel(result[0]).equals("(a)"));
        assertTrue("contains b",
                   automaton.getLabel(result[1]).equals("(b)"));
    }
    
    public void testOptionalConcatFailure1() {
        final String[][] sample = {
                {"a", "b"},
                {"b"},
                {"a"},
                {}
        };
        GraphAutomatonFactory factory = new GraphAutomatonFactory();
        Automaton automaton = factory.create(sample);
        OpportunityFinder finder = new OptionalConcatFinder();
        int[] result = finder.getFirst(automaton);
        assertTrue("result", result == null);
    }
    
    public void testOptionalConcat5() {
        final String[][] sample = {
                {"c", "a", "b", "e"},
                {"c", "b"},
                {"d", "a", "b"},
                {"d", "b", "b"},
                {"c", "b", "a", "b", "e"}
        };
        GraphAutomatonFactory factory = new GraphAutomatonFactory();
        Automaton automaton = factory.create(sample);
        OpportunityFinder finder = new OptionalConcatFinder();
        int[] result = finder.getFirst(automaton);
        assertTrue("result", result != null);
        assertEquals("size", 2, result.length);
        assertTrue("contains (+ (a))",
                   automaton.getLabel(result[0]).equals("(a)"));
        assertTrue("contains b",
                   automaton.getLabel(result[1]).equals("(b)"));
    }
    
    public void testOptionalConcatOptional1() {
        final String[][] sample = {
                {"a", "b"},
                {"b"},
                {"a"},
                {}
        };
        GraphAutomatonFactory factory = new GraphAutomatonFactory();
        Automaton automaton = factory.create(sample);
        OpportunityFinder finder = new OptionalConcatOptionalFinder();
        int[] result = finder.getFirst(automaton);
        assertTrue("result", result != null);
        assertEquals("size", 2, result.length);
        assertTrue("contains a",
                   automaton.getLabel(result[0]).equals("(a)"));
        assertTrue("contains b",
                   automaton.getLabel(result[1]).equals("(b)"));
    }

    public void testOptionalConcatOptional4() {
        final String[][] sample = {
                {"c", "a", "b"},
                {"c", "a"},
                {"c", "b"},
                {"c"}
        };
        GraphAutomatonFactory factory = new GraphAutomatonFactory();
        Automaton automaton = factory.create(sample);
        OpportunityFinder finder = new OptionalConcatOptionalFinder();
        int[] result = finder.getFirst(automaton);
        assertTrue("result", result != null);
        assertEquals("size", 2, result.length);
        assertTrue("contains a",
                   automaton.getLabel(result[0]).equals("(a)"));
        assertTrue("contains b",
                   automaton.getLabel(result[1]).equals("(b)"));
    }

    public void testOptionalConcatOptional2() {
        final String[][] sample = {
                {"c", "a", "b", "e"},
                {"c", "b", "e"},
                {"d", "a", "f"},
                {"d", "a", "e"},
                {"d", "b", "f"},
                {"c", "e"},
                {"d", "e"},
                {"c", "f"},
                {"d", "f"}
        };
        GraphAutomatonFactory factory = new GraphAutomatonFactory();
        Automaton automaton = factory.create(sample);
        OpportunityFinder finder = new OptionalConcatOptionalFinder();
        int[] result = finder.getFirst(automaton);
        assertTrue("result", result != null);
        assertEquals("size", 2, result.length);
        assertTrue("contains a",
                   automaton.getLabel(result[0]).equals("(a)"));
        assertTrue("contains b",
                   automaton.getLabel(result[1]).equals("(b)"));
    }
    
    public void testOptionalConcatOptional3() {
        final String[][] sample = {
                {"c", "a", "b", "e"},
                {"c", "a", "e"},
                {"c", "b", "e"},
                {"c", "e"},
                {"d", "a", "b", "f"},
                {"d", "a", "f"},
                {"d", "b", "f"},
                {"d", "f"},
                {"c", "f"},
                {"d", "e"},
                {"a", "b", "e"},
                {"a", "e"},
                {"b", "e"},
                {"e"},
                {"f"}
        };
        GraphAutomatonFactory factory = new GraphAutomatonFactory();
        Automaton automaton = factory.create(sample);
        OpportunityFinder finder = new OptionalConcatOptionalFinder();
        int[] result = finder.getFirst(automaton);
        assertTrue("result", result != null);
        assertEquals("size", 2, result.length);
        assertTrue("contains a",
                   automaton.getLabel(result[0]).equals("(a)"));
        assertTrue("contains b",
                   automaton.getLabel(result[1]).equals("(b)"));
    }
    
    public void testOptionalConcatOptionalFailure1() {
        final String[][] sample = {
                {"a", "b"},
                {"b"},
                {"a"}
        };
        GraphAutomatonFactory factory = new GraphAutomatonFactory();
        Automaton automaton = factory.create(sample);
        OpportunityFinder finder = new OptionalConcatOptionalFinder();
        int[] result = finder.getFirst(automaton);
        assertTrue("result", result == null);
    }

    public void testOptionalConcatOptionalFailure2() {
        final String[][] sample = {
                {"b"},
                {"c"},
                {"b", "c", "c", "b", "b", "c"},
                {}
        };
        GraphAutomatonFactory factory = new GraphAutomatonFactory();
        Automaton automaton = factory.create(sample);
        OpportunityFinder finder = new OptionalConcatOptionalFinder();
        int[] result = finder.getFirst(automaton);
        assertTrue("result", result == null);
    }
    
}
