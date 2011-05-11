import java.io.BufferedReader;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import gjb.flt.regex.measures.CostDistribution;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
/*
 * Created on Feb 24, 2005
 * Modified on $Date: 2009-10-27 14:14:01 $
 */

/**
 * @author gjb
 * @version $Revision: 1.1 $
 */
public class CostDistributionTest extends TestCase {

	static public void main(String args[]) {
		junit.textui.TestRunner.run(suite());
	}

	public CostDistributionTest(String name) {
		super(name);
	}

	public static Test suite() {
		return new TestSuite(CostDistributionTest.class);
	}

	public void test_costDistribution() throws Exception {
	    String[] str = {
	        "1 2 3",
	        "1 3",
	        "1 2 2 2",
	        "2 1",
	        "  # comment string",
	        "1 3",
	        "1 2 3",
	        "",
	        "1 2",
	        "1 3"};
	    int numberOfComments = 1;
	    Map<Integer,Integer> costs = new HashMap<Integer,Integer>();
	    costs.put(2, 3);
	    costs.put(4, 3);
	    costs.put(8, 1);
	    costs.put(Integer.MAX_VALUE, 2);
	    
	    Map<Integer,Integer> uniqueCosts = new HashMap<Integer,Integer>();
	    uniqueCosts.put(2, 1);
	    uniqueCosts.put(4, 2);
	    uniqueCosts.put(8, 1);
	    uniqueCosts.put(Integer.MAX_VALUE, 2);
	    
	    StringReader strReader = new StringReader(StringUtils.join(str, "\n"));
	    BufferedReader reader = new BufferedReader(strReader);
	    CostDistribution cd = new CostDistribution("(. (1) (* (2)) (? (3)))",
	                                               null, reader);
	    int numberOfCost = 0;
	    for (Iterator<Integer> it = cd.costIterator(); it.hasNext(); ) {
	        Integer cost = it.next();
	        assertTrue("presence in ocst of " + cost,
	                   costs.keySet().contains(cost));
	        assertTrue("presence in uniqueCcst of " + cost,
	                   uniqueCosts.keySet().contains(cost));
	        Integer number = new Integer(cd.getCost(cost.intValue()));
	        Integer target = costs.get(cost);
	        assertEquals("correctness of cost " + cost, target, number);
	        Integer uniqueNumber = new Integer(cd.getUniqueCost(cost.intValue()));
	        Integer uniqueTarget = uniqueCosts.get(cost);
	        assertEquals("correctness of uniqueCost " + cost,
	                     uniqueTarget, uniqueNumber);
	        numberOfCost++;
	    }
	    assertEquals("all costs", costs.keySet().size(), numberOfCost);
	    assertEquals("all uniqueCosts", uniqueCosts.keySet().size(), numberOfCost);
	    assertEquals("sample size",
	                 str.length - numberOfComments, cd.getTotalSampleSize());
	    assertEquals("min cost", 2, cd.getMinCost());
	    assertEquals("max cost", Integer.MAX_VALUE, cd.getMaxCost());
	    assertEquals("max match cost", 8, cd.getMaxMatchCost());
	}

}
