/*
 * Created on Jun 4, 2007
 * Modified on $Date: 2009-11-12 21:44:56 $
 */
package eu.fox7.flt.automata.measures;

import eu.fox7.flt.automata.FeatureNotSupportedException;
import eu.fox7.flt.automata.factories.dense.BigIntegerMatrixRepresentationFactory;
import eu.fox7.flt.automata.factories.sparse.GlushkovFactory;
import eu.fox7.flt.automata.impl.dense.MatrixRepresentation;
import eu.fox7.flt.automata.impl.sparse.SparseNFA;
import eu.fox7.flt.regex.UnknownOperatorException;
import eu.fox7.util.tree.SExpressionParseException;

import java.math.BigInteger;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * @author eu.fox7
 * @version $Revision: 1.1 $
 * 
 */
public class MatrixRepresentationTest extends TestCase {

	protected BigIntegerMatrixRepresentationFactory factory = new BigIntegerMatrixRepresentationFactory();
	protected GlushkovFactory glushkovFactory = new GlushkovFactory();

    public static Test suite() {
        return new TestSuite(MatrixRepresentationTest.class);
    }

    public void test_emptyExpression() {
        try {
	        SparseNFA nfa = glushkovFactory.create("(EMPTY)");
	        MatrixRepresentation<BigInteger> m = factory.create(nfa);
	        for (int length = 0; length < 20; length++)
	            assertEquals("length " + length,
	                         BigInteger.ZERO,
	                         m.getNumberOfAcceptedStrings(length));
        } catch (SExpressionParseException e) {
	        e.printStackTrace();
	        fail("unexpected exception");
        } catch (UnknownOperatorException e) {
	        e.printStackTrace();
	        fail("unexpected exception");
        } catch (FeatureNotSupportedException e) {
	        e.printStackTrace();
	        fail("unexpected exception");
        }
    }

    public void test_epsilonExpression() {
    	try {
    		SparseNFA nfa = glushkovFactory.create("(EPSILON)");
    		MatrixRepresentation<BigInteger> m = factory.create(nfa);
            assertEquals("length 0",
                         BigInteger.ONE,
                         m.getNumberOfAcceptedStrings(0));
    		for (int length = 1; length < 20; length++)
	            assertEquals("length " + length,
	                         BigInteger.ZERO,
	                         m.getNumberOfAcceptedStrings(length));
    	} catch (SExpressionParseException e) {
    		e.printStackTrace();
    		fail("unexpected exception");
    	} catch (UnknownOperatorException e) {
    		e.printStackTrace();
    		fail("unexpected exception");
    	} catch (FeatureNotSupportedException e) {
    		e.printStackTrace();
    		fail("unexpected exception");
    	}
    }
    
    public void test_symbolExpression() {
    	try {
    		SparseNFA nfa = glushkovFactory.create("(a)");
    		MatrixRepresentation<BigInteger> m = factory.create(nfa);
    		assertEquals("length 0",
    		             BigInteger.ZERO,
    		             m.getNumberOfAcceptedStrings(0));
    		assertEquals("length 1",
    		             BigInteger.ONE,
    		             m.getNumberOfAcceptedStrings(1));
    		for (int length = 2; length < 20; length++)
    			assertEquals("length " + length,
    			             BigInteger.ZERO,
    			             m.getNumberOfAcceptedStrings(length));
    	} catch (SExpressionParseException e) {
    		e.printStackTrace();
    		fail("unexpected exception");
    	} catch (UnknownOperatorException e) {
    		e.printStackTrace();
    		fail("unexpected exception");
    	} catch (FeatureNotSupportedException e) {
    		e.printStackTrace();
    		fail("unexpected exception");
    	}
    }
    
    public void test_simpleExpression01() {
        try {
	        SparseNFA nfa = glushkovFactory.create("(* (. (a) (b)))");
	        MatrixRepresentation<BigInteger> m = factory.create(nfa);
	        for (int length = 0; length < 20; length++) {
	            double number = m.getNumberOfAcceptedStrings(length).doubleValue();
	            if (length % 2 == 0)
	                assertTrue("length " + length + ": " + number,
	                           0.9999 < number && number < 1.0001);
	            else
	                assertTrue("length " + length + ": " + number,
	                           number < 0.0001);
	        }
        } catch (SExpressionParseException e) {
	        e.printStackTrace();
	        fail("unexpected exception");
        } catch (UnknownOperatorException e) {
	        e.printStackTrace();
	        fail("unexpected exception");
        } catch (FeatureNotSupportedException e) {
	        e.printStackTrace();
	        fail("unexpected exception");
        }
    }

    public void test_simpleExpression02() {
        try {
	        SparseNFA nfa = glushkovFactory.create("(* (. (a) (| (b) (c))))");
	        MatrixRepresentation<BigInteger> m = factory.create(nfa);
	        for (int length = 0; length < 20; length++) {
	            double number = m.getNumberOfAcceptedStrings(length).doubleValue();
	            if (length % 2 == 0)
	                assertTrue("length " + length + ": " + number,
	                           Math.pow(2.0, length/2.0) - 0.0001 < number &&
	                           number < Math.pow(2.0, length/2.0) + 0.0001);
	            else
	                assertTrue("length " + length + ": " + number,
	                           number < 0.0001);
	        }
        } catch (SExpressionParseException e) {
	        e.printStackTrace();
	        fail("unexpected exception");
        } catch (UnknownOperatorException e) {
	        e.printStackTrace();
	        fail("unexpected exception");
        } catch (FeatureNotSupportedException e) {
	        e.printStackTrace();
	        fail("unexpected exception");
        }
    }
    
    public void test_simpleAmbiguousExpression01() {
        try {
	        SparseNFA nfa = glushkovFactory.create("(| (a) (. (a) (a)) (. (a) (a) (a)))");
	        MatrixRepresentation<BigInteger> m = factory.create(nfa);
	        assertTrue("length = 0",
	                   m.getNumberOfAcceptedStrings(0).doubleValue() < 0.0001);
	        for (int length = 1; length <= 3; length++) {
	            double number = m.getNumberOfAcceptedStrings(length).doubleValue();
	            assertTrue("length " + length + ": " + number,
	                           1.0 - 0.0001 < number &&
	                           number < 1.0 + 0.0001);
	        }
	        for (int length = 4; length < 20; length++) {
	            double number = m.getNumberOfAcceptedStrings(length).doubleValue();
	            assertTrue("length " + length + ": " + number,
	                       number < 0.0001);
	        }
        } catch (SExpressionParseException e) {
	        e.printStackTrace();
	        fail("unexpected exception");
        } catch (UnknownOperatorException e) {
	        e.printStackTrace();
	        fail("unexpected exception");
        } catch (FeatureNotSupportedException e) {
	        e.printStackTrace();
	        fail("unexpected exception");
        }
    }
    
}
