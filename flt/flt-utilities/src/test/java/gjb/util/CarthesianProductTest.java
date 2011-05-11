package gjb.util;
import gjb.util.CarthesianProduct;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
/*
 * Created on Dec 10, 2004
 *
 */

/**
 * @author gjb
 *
 */
public class CarthesianProductTest extends TestCase {

    public static void main(String[] args) {
        junit.textui.TestRunner.run(CarthesianProductTest.class);
    }

	public static Test suite() {
		return new TestSuite(CarthesianProductTest.class);
	}

	public void test_simpleCarthesianProduct() {
	    List<List<Integer>> lists = new LinkedList<List<Integer>>();
	    for (int i = 0; i < 3; i++) {
	        List<Integer> list = new LinkedList<Integer>();
	        for (int j = 0; j < 4 - i; j++) {
	            list.add(j);
	        }
	        lists.add(list);
	    }
	    CarthesianProduct prod = new CarthesianProduct(lists);
	    int count = 0;
	    for (Iterator it = prod.iterator(); it.hasNext(); it.next())
            count++;
	    assertEquals(24, count);
	}
	
	public void test_emptyCarthesianProduct() {
        List<List<Integer>> lists = new LinkedList<List<Integer>>();
	    for (int i = 0; i < 3; i++) {
            List<Integer> list = new LinkedList<Integer>();
	        for (int j = 0; j < 2 - i; j++) {
	            list.add(j);
	        }
	        lists.add(list);
	    }
	    CarthesianProduct prod = new CarthesianProduct(lists);
	    int count = 0;
	    for (Iterator it = prod.iterator(); it.hasNext(); it.next())
	        count++;
	    assertEquals(0, count);
	}
	

}
