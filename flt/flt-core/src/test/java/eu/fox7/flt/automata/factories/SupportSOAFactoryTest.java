/**
 * Created on Oct 28, 2009
 * Modified on $Date: 2009-11-12 21:44:56 $
 */
package eu.fox7.flt.automata.factories;

import eu.fox7.flt.automata.factories.sparse.SupportSOAFactory;
import eu.fox7.flt.automata.impl.sparse.SupportNFA;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * @author lucg5005
 * @version $Revision: 1.1 $
 *
 */
public class SupportSOAFactoryTest extends TestCase {

    public static Test suite() {
        return new TestSuite(SupportSOAFactoryTest.class);
    }

	public void testSample1() {
		final String[][] sample = {
				{"a", "a"},
				{},
				{"a", "b", "c", "b", "c", "b"},
				{"a", "b"},
				{"a", "a", "a", "b"},
				{}
		};
		SupportSOAFactory factory = new SupportSOAFactory();
		for (String[] example : sample)
			factory.add(example);
		SupportNFA soa = factory.getAutomaton();
		assertEquals("total support", sample.length, soa.getTotalSupport());
		assertEquals("support a--a", 2, soa.getSupport("a", "a", "a"));
		assertEquals("support a--b", 3, soa.getSupport("b", "a", "b"));
		assertEquals("support c--b", 1, soa.getSupport("b", "c", "b"));
		assertEquals("support a--c", 0, soa.getSupport("c", "a", "c"));
		assertEquals("support empty string", 2, soa.getEmptyStringSupport());
	}

	public void testEmptySample() {
		final String[][] sample = {};
		SupportSOAFactory factory = new SupportSOAFactory();
		for (String[] example : sample)
			factory.add(example);
		SupportNFA soa = factory.getAutomaton();
		assertEquals("total support", sample.length, soa.getTotalSupport());
		assertEquals("support empty string", 0, soa.getEmptyStringSupport());
	}

}
