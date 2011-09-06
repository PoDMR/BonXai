import eu.fox7.flt.regex.infer.CRXDeriverTest;
import eu.fox7.flt.regex.infer.rwr.GraphAutomatonTest;
import eu.fox7.flt.regex.infer.rwr.OpportunityFinderTest;
import eu.fox7.flt.regex.infer.rwr.RepairRewriteStepTest;
import eu.fox7.flt.regex.infer.rwr.RepairerTest;
import eu.fox7.flt.regex.infer.rwr.RewriteStepTest;
import eu.fox7.flt.regex.infer.rwr.RewriterTest;
import eu.fox7.flt.schema.infer.ixsd.MergerTest;
import eu.fox7.flt.schema.infer.ixsd.XMLSamplerTest;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Created on Oct 26, 2009
 * Modified on $Date: 2009-11-12 21:45:55 $
 */

/**
 * @author lucg5005
 * @version $Revision: 1.4 $
 *
 */
public class TestAllFLTLearning extends TestCase {

	static public void main(String args[]) {
		junit.textui.TestRunner.run(suite());
	}

	public TestAllFLTLearning(String name) {
		super(name);
	}

	public static Test suite() {
		TestSuite suite = new TestSuite();
		suite.addTest(CRXDeriverTest.suite());
		suite.addTest(GraphAutomatonTest.suite());
		suite.addTest(OpportunityFinderTest.suite());
		suite.addTest(RepairerTest.suite());
		suite.addTest(RepairRewriteStepTest.suite());
		suite.addTest(RewriterTest.suite());
		suite.addTest(RewriteStepTest.suite());
		suite.addTest(XMLSamplerTest.suite());
		suite.addTest(MergerTest.suite());
		return suite;
	}

}
