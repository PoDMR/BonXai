import eu.fox7.flt.automata.factories.sparse.CFGApproximationFactory;
import eu.fox7.flt.automata.factories.sparse.Determinizer;
import eu.fox7.flt.automata.impl.sparse.SparseNFA;
import eu.fox7.flt.automata.impl.sparse.StateDFA;
import eu.fox7.flt.grammar.CFG;
import eu.fox7.flt.regex.NoRegularExpressionDefinedException;
import eu.fox7.flt.regex.Regex;
import eu.fox7.flt.regex.UnknownOperatorException;
import eu.fox7.flt.regex.factories.StateEliminationFactory;
import eu.fox7.flt.regex.matchers.Matcher;

import java.io.StringReader;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class GNFATest extends TestCase {
  
	protected CFG cfg;
	protected String grammar = "A -> a A b | c";

	public GNFATest(String name) {
		super(name);
	}

	public static Test suite() {
		return new TestSuite(GNFATest.class);
	}

	protected void setUp() throws Exception {
	    super.setUp();
		StringReader reader = new StringReader(grammar);
		cfg = new CFG(reader);
		reader.close();
	}

	public void test_Regex_2() {
		CFGApproximationFactory cfgApprox = new CFGApproximationFactory(cfg, 2);
		SparseNFA nfa = cfgApprox.nfa();
		StateDFA dfa = Determinizer.dfa(nfa);
		StateEliminationFactory factory = new StateEliminationFactory();
		Regex regex = factory.create(dfa, false);
		Matcher matcher = new Matcher(regex);
		try {
	        assertTrue(matcher.matches(new String[] {"c"}));
	        assertTrue(matcher.matches(new String[] {"a", "c", "b"}));
	        assertTrue(matcher.matches(new String[] {"a", "c", "b", "b", "b", "b"}));
	        assertTrue(matcher.matches(new String[] {"a", "a", "a", "a", "c", "b"}));
	        assertTrue(matcher.matches(new String[] {"a", "a", "a", "a", "a", "c", "b", "b", "b"}));
	        assertTrue(!matcher.matches(new String[] {"a", "c"}));
	        assertTrue(!matcher.matches(new String[] {"c", "b"}));
	        assertTrue(!matcher.matches(new String[] {"a", "a", "c", "b", "a"}));
	        assertTrue(!matcher.matches(new String[] {"c", "a"}));
	        assertTrue(!matcher.matches(new String[] {"a", "a", "b", "b"}));
        } catch (NoRegularExpressionDefinedException e) {
	        e.printStackTrace();
	        fail("unexpected exception");
        } catch (UnknownOperatorException e) {
	        e.printStackTrace();
	        fail("unexpected exception");
        }
	}

	public void test_Regex_3() {
		CFGApproximationFactory cfgApprox = new CFGApproximationFactory(cfg, 3);
		SparseNFA nfa = cfgApprox.nfa();
		StateDFA dfa = Determinizer.dfa(nfa);
		StateEliminationFactory factory = new StateEliminationFactory();
		Regex regex = factory.create(dfa, false);
		Matcher matcher = new Matcher(regex);
        try {
	        assertTrue(matcher.matches(new String[] {"c"}));
	        assertTrue(matcher.matches(new String[] {"a", "c", "b"}));
	        assertTrue(matcher.matches(new String[] {"a", "a", "c", "b", "b"}));
	        assertTrue(!matcher.matches(new String[] {"a", "c", "b", "b", "b", "b"}));
	        assertTrue(!matcher.matches(new String[] {"a", "a", "a", "a", "c", "b"}));
	        assertTrue(matcher.matches(new String[] {"a", "a", "c", "b", "b", "b"}));
	        assertTrue(matcher.matches(new String[] {"a", "a", "a", "c", "b", "b"}));
	        assertTrue(matcher.matches(new String[] {"a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "c", "b", "b"}));
	        assertTrue(matcher.matches(new String[] {"a", "a", "c", "b", "b", "b", "b", "b", "b", "b", "b", "b", "b"}));
	        assertTrue(matcher.matches(new String[] {"a", "a", "a", "a", "a", "c", "b", "b", "b"}));
	        assertTrue(!matcher.matches(new String[] {"a", "c"}));
	        assertTrue(!matcher.matches(new String[] {"c", "b"}));
	        assertTrue(!matcher.matches(new String[] {"a", "a", "c", "b", "a"}));
	        assertTrue(!matcher.matches(new String[] {"c", "a"}));
	        assertTrue(!matcher.matches(new String[] {"a", "a", "b", "b"}));
        } catch (NoRegularExpressionDefinedException e) {
	        e.printStackTrace();
	        fail("unexpected exception");
        } catch (UnknownOperatorException e) {
	        e.printStackTrace();
	        fail("unexpected exception");
        }
	}

}
