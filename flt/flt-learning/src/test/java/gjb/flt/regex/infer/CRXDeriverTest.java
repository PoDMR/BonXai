/**
 * Created on Oct 26, 2009
 * Modified on $Date: 2009-10-27 09:30:13 $
 */
package gjb.flt.regex.infer;

import gjb.flt.regex.infer.crx.LargeSampleCRXInferrer;
import gjb.flt.regex.infer.crx.SmallSampleCRXInferrer;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * @author lucg5005
 * @version $Revision: 1.4 $
 *
 */
public class CRXDeriverTest extends TestCase {

    public static Test suite() {
        return new TestSuite(CRXDeriverTest.class);
    }

	public void testLargeSampleDerivation() {
		simpleTest(new LargeSampleCRXInferrer(), "large sample SRX deriver",
		           "(. (a) (b) (+ (| (c) (d))))");
	}

	public void testSmallSampleDerivation() {
		simpleTest(new SmallSampleCRXInferrer(), "small sample SRX deriver",
		           "(. (? (a)) (b) (+ (| (c) (d))))");
	}

	private void simpleTest(Inferrer deriver, String message, String target) {
	    final String[][] sample = {
				{"a", "b", "c", "c"},
				{"b", "c", "d", "c"},
				{"b", "d", "d"}
		};
		for (String[] example : sample)
			deriver.addExample(example);
		try {
	        assertEquals(message, target, deriver.infer());
        } catch (InferenceException e) {
        	e.printStackTrace();
        	fail("unexpected exception");
        }
    }
	
}
