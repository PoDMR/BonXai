/**
 * Created on Nov 5, 2009
 * Modified on $Date: 2009-11-05 14:02:16 $
 */
package eu.fox7.flt.schema.infer.ixsd;

import org.apache.commons.lang.StringUtils;

import eu.fox7.flt.automata.FeatureNotSupportedException;
import eu.fox7.flt.automata.NotDFAException;
import eu.fox7.flt.automata.factories.sparse.GlushkovFactory;
import eu.fox7.flt.automata.impl.sparse.StateNFA;
import eu.fox7.flt.automata.measures.EquivalenceTest;
import eu.fox7.flt.regex.Glushkov;
import eu.fox7.flt.regex.UnknownOperatorException;
import eu.fox7.flt.schema.infer.ixsd.ContextMap;
import eu.fox7.flt.schema.infer.ixsd.ContextMapConverter;
import eu.fox7.flt.treeautomata.factories.SupportContentAutomatonFactory;
import eu.fox7.flt.treeautomata.impl.ContextAutomaton;
import eu.fox7.util.sampling.SampleException;
import eu.fox7.util.tree.SExpressionParseException;

import junit.framework.TestCase;

/**
 * @author lucg5005
 * @version $Revision: 1.1 $
 *
 */
public class ContextMapTest extends TestCase {

	protected final String[][] sample1 = {
			// document 1
			{"/", "a"},
			{"/a", "b"},
			{"/a/b", "c,d"},
			{"/a/b/c", "d"},
			{"/a/b/d", ""},
			{"/a/b/c/d", ""},
			// document 2
			{"/", "a"},
			{"/a", "c,d"},
			{"/a/c", "d"},
			{"/a/d", ""},
			{"/a/c/d", ""},
			// document 3
			{"/", "a"},
			{"/a", "c,d"},
			{"/a/c", "d"},
			{"/a/d", "d"},
			{"/a/c/d", "d"},
			{"/a/d/d", ""},
			{"/a/c/d/d", ""},
			// document 4
			{"/", "a"},
			{"/a", "b"},
			{"/a/b", "c,d"},
			{"/a/b/c", "d"},
			{"/a/b/c/d", "d"},
			{"/a/b/c/d/d", "d"},
			{"/a/b/c/d/d/d", ""},
			{"/a/b/d", "d"},
			{"/a/b/d/d", ""}
	};

	public void testSample1DerivedContentModels() {
		final String[][] contentModels = {
				{"./a", "(| (b) (. (c) (d)))"},
				{"a/b", "(. (c) (d))"},
				{"a/c", "(d)"},
				{"a/d", "(? (d))"},
				{"b/c", "(d)"},
				{"b/d", "(? (d))"},
				{"c/d", "(? (d))"},
				{"d/d", "(? (d))"}
		};
		final int contextSize = 2;
		ContextMap contextMap = computeContextMap(sample1, contextSize);
		assertEquals("nr docs", 4, contextMap.getNumberOfDocuments());
		assertEquals("nr contexts", contentModels.length, contextMap.getNumberOfContexts());
		GlushkovFactory glushkov = new GlushkovFactory();
		for (int i = 0; i < contentModels.length; i++) {
			try {
	            StateNFA targetNFA = glushkov.create(contentModels[i][1]);
	            StateNFA nfa = contextMap.getContentModel(contentModels[i][0]);
	            assertTrue(contentModels[i][0],
	                       EquivalenceTest.areEquivalent(targetNFA, nfa));
            } catch (UnknownOperatorException e) {
	            e.printStackTrace();
	            fail("unexpected exception");
            } catch (FeatureNotSupportedException e) {
	            e.printStackTrace();
	            fail("unexpected exception");
            } catch (SExpressionParseException e) {
	            e.printStackTrace();
	            fail("unexpected exception");
            } catch (NotDFAException e) {
	            e.printStackTrace();
	            fail("unexpected exception");
            }
		}
	}

	public void testSample1ContextAutomatonConversion() {
		final String[] symbolValues = {"a", "b", "c", "d"};
		final String initialStateValue = Glushkov.INITIAL_STATE;
		final String[] stateValues = {
				initialStateValue,
				"a_0",
				"b_a", "c_a", "d_a",
				"c_b", "d_b",
				"d_c",
				"d_d"
		};
		final String[] finalStateValues = {"d_a", "d_b", "d_c", "d_d"};
		final String[][] transitions = {
				{initialStateValue, "a", "a_0"},
				{"a_0", "b", "b_a"},
				{"a_0", "c", "c_a"},
				{"a_0", "d", "d_a"},
				{"b_a", "c", "c_b"},
				{"b_a", "d", "d_b"},
				{"c_a", "d", "d_c"},
				{"d_a", "d", "d_d"},
				{"c_b", "d", "d_c"},
				{"d_b", "d", "d_d"},
				{"d_c", "d", "d_d"},
				{"d_d", "d", "d_d"}
		};
		final int contextSize = 2;
		ContextMap contextMap = computeContextMap(sample1, contextSize);
		ContextAutomaton contextFA = ContextMapConverter.convertToContextFA(contextMap);
		assertEquals("nr symbols", symbolValues.length, contextFA.getNumberOfSymbols());
		for (String symbolValue : symbolValues)
			assertTrue("symbol " + symbolValue, contextFA.hasSymbol(symbolValue));
		assertEquals("nr states",
		             stateValues.length,
		             contextFA.getNumberOfStates());
		for (String stateValue : stateValues)
			assertTrue("state " + stateValue, contextFA.hasState(stateValue));
		assertTrue("initial state", contextFA.isInitialState(initialStateValue));
		assertEquals("nr final states",
		             finalStateValues.length,
		             contextFA.getNumberOfFinalStates());
		for (String finalStateValue : finalStateValues)
			assertTrue("final state " + finalStateValue,
			           contextFA.isFinalState(finalStateValue));
		assertEquals("nr transitions",
		             transitions.length,
		             contextFA.getNumberOfTransitions());
		for (String[] transition : transitions)
			assertTrue("transition " + StringUtils.join(transition, ", "),
			           contextFA.hasTransition(transition[1],
			                                   transition[0],
			                                   transition[2]));
	}

	protected ContextMap computeContextMap(String[][] sample, int contextSize) {
	    ContextMap contextMap = new ContextMap(new SupportContentAutomatonFactory(), contextSize);
		for (String[] example : sample) {
			String[] context = computeContext(example[0]);
			String[] content = computeContent(example[1]);
			try {
	            contextMap.add(context, content);
            } catch (SampleException e) {
	            e.printStackTrace();
	            fail("unexpected exception");
            }
		}
	    return contextMap;
    }

	protected String[] computeContext(String contextStr) {
		String str = contextStr.substring(1);
		if (str.length() == 0)
			return new String[0];
		else
			return str.split("/");
	}

	protected String[] computeContent(String contentStr) {
		if (contentStr.length() == 0)
			return new String[0];
		else
			return contentStr.split(",");
	}

}
