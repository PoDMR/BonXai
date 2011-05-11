import gjb.flt.automata.factories.sparse.GlushkovFactory;
import gjb.flt.automata.impl.sparse.SparseNFA;
import gjb.flt.automata.measures.StringCost;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/*
 * Created on Feb 20, 2005
 *
 */

/**
 * @author gjb
 * @version $Revision: 1.1 $
 */
public class StringCostTest extends TestCase {

	protected GlushkovFactory glushkovFactory;

	public StringCostTest(String name) {
		super(name);
	}

	public static Test suite() {
		return new TestSuite(StringCostTest.class);
	}

	public void setUp() {
		glushkovFactory = new GlushkovFactory();
	}

	public void test_cost1() throws Exception {
	    SparseNFA nfa = glushkovFactory.create("(. (a) (* (| (b) (c))))");
	    StringCost measure = new StringCost(nfa);
	    String[] input1 = {"a", "b", "c"};
	    assertEquals("string '" + exampleToString(input1) + "'",
	                 6, measure.compute(input1));
	}

	public void test_cost2() throws Exception {
	    SparseNFA nfa = glushkovFactory.create("(. (a) (* (| (b) (c))) (c))");
	    StringCost measure = new StringCost(nfa);
	    String[] input1 = {"a", "b", "c", "c", "b", "c"};
	    assertEquals("string '" + exampleToString(input1) + "'",
	                 10, measure.compute(input1));
	}

	public void test_cost3() throws Exception {
	    SparseNFA nfa = glushkovFactory.create("(. (a) (* (| (b) (c))) (c) (d))");
	    StringCost measure = new StringCost(nfa);
	    String[] input1 = {"a", "b", "c", "c", "b", "c", "c", "d"};
	    assertEquals("string '" + exampleToString(input1) + "'",
	                 12, measure.compute(input1));

	}

	public void test_cost4() throws Exception {
	    SparseNFA nfa = glushkovFactory.create("(. (* (a)) (* (| (b) (c))) (? (c)))");
	    StringCost measure = new StringCost(nfa);
	    String[] input1 = {"a", "b", "c", "c", "b", "c"};
	    assertEquals("string '" + exampleToString(input1) + "'",
	                 14, measure.compute(input1));

	}

	public void test_cost5() throws Exception {
	    SparseNFA nfa = glushkovFactory.create("(. (* (a)) (* (| (b) (c))) (? (c)))");
	    StringCost measure = new StringCost(nfa);
	    String[] input1 = {};
	    assertEquals("string '" + exampleToString(input1) + "'",
	                 3, measure.compute(input1));

	}

	public void test_cost6() throws Exception {
	    SparseNFA nfa = glushkovFactory.create("(. (a) (b) (c))");
	    StringCost measure = new StringCost(nfa);
	    String[] input1 = {"a", "b", "c"};
	    assertEquals("string '" + exampleToString(input1) + "'",
	                 0, measure.compute(input1));

	}

	public void test_cost7() throws Exception {
	    SparseNFA nfa = glushkovFactory.create("(. (a) (b) (c))");
	    StringCost measure = new StringCost(nfa);
	    String[] input1 = {"a", "d", "c"};
	    assertEquals("string '" + exampleToString(input1) + "'",
	                 StringCost.INFINITY, measure.compute(input1));

	}

    public void test_cost8() throws Exception {
        SparseNFA nfa = glushkovFactory.create("(. (* (a)) (* (| (b) (c))) (? (d)))");
	    StringCost measure = new StringCost(nfa);
        String[] input1 = {"a", "b", "c", "c", "b", "c"};
        assertEquals("string '" + exampleToString(input1) + "'",
                     16, measure.compute(input1));

    }

    public void test_cost9() throws Exception {
        SparseNFA nfa = glushkovFactory.create("(. (* (a)) (* (| (b) (c))) (? (d)))");
	    StringCost measure = new StringCost(nfa);
        String[] input1 = {"a", "b", "c", "c", "b", "c", "d"};
        assertEquals("string '" + exampleToString(input1) + "'",
                     16, measure.compute(input1));
    }

    public void test_cost10() {
        try {
            SparseNFA nfa = glushkovFactory.create("(. (a) (+ (b)) (* (c)) ($))");
    	    StringCost measure = new StringCost(nfa);
            String[] input1 = {"a", "b", "b", "c", "$"};
            assertEquals("string '" + exampleToString(input1) + "'",
                         5, measure.compute(input1));
        } catch (Exception e) {
            e.printStackTrace();
            fail("exception");
        }
    }

    public void test_cost11() {
        try {
            SparseNFA nfa = glushkovFactory.create("(. (a) (+ (b)) (* (c)))");
    	    StringCost measure = new StringCost(nfa);
            String[] input1 = {"a", "b", "b", "c"};
            assertEquals("string '" + exampleToString(input1) + "'",
                         5, measure.compute(input1));
        } catch (Exception e) {
            e.printStackTrace();
            fail("exception");
        }
    }

    public void test_computeCost() {
        int[] target = {0, 1, 2, 2, 3, 3, 3, 3, 4, 4, 4, 4};
        for (int i = 0; i < target.length; i++) {
            assertEquals("value = " + i, target[i], StringCost.computeCost(i + 1));
        }
    }

    protected static String exampleToString(String[] str) {
	    StringBuffer sb = new StringBuffer();
	    if (str.length > 0) {
	        sb.append(str[0]);
	        for (int i = 1; i < str.length; i++) {
	            sb.append(", ").append(str[i]);
	        }
	    }
	    return sb.toString();
	}

}
