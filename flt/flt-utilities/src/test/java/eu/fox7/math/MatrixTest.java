/**
 * Created on Sep 11, 2009
 * Modified on $Date: 2009-10-26 18:37:40 $
 */
package eu.fox7.math;

import java.math.BigInteger;

import eu.fox7.math.BigIntegerMatrix;
import eu.fox7.math.DimensionMismatchException;
import eu.fox7.math.DoubleMatrix;
import eu.fox7.math.Matrix;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * @author lucg5005
 * @version $Revision: 1.1 $
 *
 */
public class MatrixTest extends TestCase {

    public static Test suite() {
        return new TestSuite(MatrixTest.class);
    }

    public void testBigIntegerClone() {
		Matrix<BigInteger> m = fill(new long[][] {
				{0, 0, 0},
				{0, 14, 28},
				{0, 28, 56}
		});
    	Matrix<BigInteger> mClone = m.clone();
    	assertEquals("clone", m, mClone);
    	assertTrue("clone equals", m.equals(mClone));
    	/* check whether BigInteger is immutable */
    	m.set(0, 1, BigInteger.valueOf(15));
    	assertTrue("value change", !m.get(0, 1).equals(mClone.get(0, 1)));
    	assertTrue("no change propagation", !m.equals(mClone));
    }

    public void testDoubleClone() {
    	Matrix<Double> m = fill(new double[][] {
    			{0, 0, 0},
    			{0, 14, 28},
    			{0, 28, 56}
    	});
    	Matrix<Double> mClone = m.clone();
    	assertEquals("clone", m, mClone);
    	assertTrue("clone equals", m.equals(mClone));
    	/* check whether BigInteger is immutable */
    	m.set(0, 1, Double.valueOf(15));
    	assertTrue("value change", !m.get(0, 1).equals(mClone.get(0, 1)));
    	assertTrue("no change propagation", !m.equals(mClone));
    }
    
    public void testBigIntegerDimensions() {
		final int rows = 3;
		final int cols = 4;
		Matrix<BigInteger> m = new BigIntegerMatrix(rows, cols);
		assertEquals("rows", rows, m.getRowDimension());
		assertEquals("columns", cols, m.getColumnDimension());
	}

    public void testDoubleDimensions() {
    	final int rows = 3;
    	final int cols = 4;
    	Matrix<Double> m = new DoubleMatrix(rows, cols);
    	assertEquals("rows", rows, m.getRowDimension());
    	assertEquals("columns", cols, m.getColumnDimension());
    }
    
	public void testBigIntegerEquality() {
		final int rows = 3;
		final int cols = 4;
		Matrix<BigInteger> m1 = new BigIntegerMatrix(rows, cols);
		Matrix<BigInteger> m2 = new BigIntegerMatrix(rows, cols);
		for (int row = 0; row < rows; row++)
			for (int col = 0; col < cols; col++) {
				m1.set(row, col, BigInteger.valueOf(row*col));
				m2.set(row, col, BigInteger.valueOf(row*col));
			}
		assertEquals("equality", m1, m2);
		assertEquals("equality", m2, m1);
		for (int row = 0; row < rows; row++)
			for (int col = 0; col < cols; col++) {
				m1.set(row, col, BigInteger.valueOf(row + col));
				m2.set(row, col, BigInteger.valueOf(row*col));
			}
		assertTrue("nonequality", !m1.equals(m2));
		assertTrue("nonequality", !m2.equals(m1));
		m1 = new BigIntegerMatrix(rows, cols);
		m2 = new BigIntegerMatrix(cols, rows);
		for (int row = 0; row < rows; row++)
			for (int col = 0; col < cols; col++) {
				m1.set(row, col, BigInteger.valueOf(row*col));
				m2.set(col, row, BigInteger.valueOf(row*col));
			}
		assertTrue("nonequality", !m1.equals(m2));
	}

	public void testDoubleEquality() {
		final int rows = 3;
		final int cols = 4;
		Matrix<Double> m1 = new DoubleMatrix(rows, cols);
		Matrix<Double> m2 = new DoubleMatrix(rows, cols);
		for (int row = 0; row < rows; row++)
			for (int col = 0; col < cols; col++) {
				m1.set(row, col, Double.valueOf(row*col));
				m2.set(row, col, Double.valueOf(row*col));
			}
		assertEquals("equality", m1, m2);
		assertEquals("equality", m2, m1);
		for (int row = 0; row < rows; row++)
			for (int col = 0; col < cols; col++) {
				m1.set(row, col, Double.valueOf(row + col));
				m2.set(row, col, Double.valueOf(row*col));
			}
		assertTrue("nonequality", !m1.equals(m2));
		assertTrue("nonequality", !m2.equals(m1));
		m1 = new DoubleMatrix(rows, cols);
		m2 = new DoubleMatrix(cols, rows);
		for (int row = 0; row < rows; row++)
			for (int col = 0; col < cols; col++) {
				m1.set(row, col, Double.valueOf(row*col));
				m2.set(col, row, Double.valueOf(row*col));
			}
		assertTrue("nonequality", !m1.equals(m2));
	}
	
	public void testBigIntegerNumberSum() {
		int rows = 3;
		int cols = 4;
		Matrix<BigInteger> m = new BigIntegerMatrix(rows, cols);
		for (int row = 0; row < rows; row++)
			for (int col = 0; col < cols; col++)
				m.set(row, col, BigInteger.valueOf(row*col));
		BigInteger lambda = BigInteger.valueOf(5);
		Matrix<BigInteger> mSum = m.plus(lambda);
		for (int row = 0; row < rows; row++)
			for (int col = 0; col < cols; col++)
				assertEquals(row + "," + col,
						     m.get(row, col).add(lambda),
						     mSum.get(row, col));
	}

	public void testDoubleNumberSum() {
		int rows = 3;
		int cols = 4;
		Matrix<Double> m = new DoubleMatrix(rows, cols);
		for (int row = 0; row < rows; row++)
			for (int col = 0; col < cols; col++)
				m.set(row, col, Double.valueOf(row*col));
		Double lambda = Double.valueOf(5);
		Matrix<Double> mSum = m.plus(lambda);
		for (int row = 0; row < rows; row++)
			for (int col = 0; col < cols; col++)
				assertEquals(row + "," + col,
				             m.get(row, col) + lambda,
				             mSum.get(row, col));
	}
	
	public void testBigIntegerSum() {
		final int rows = 3;
		final int cols = 4;
		Matrix<BigInteger> m1 = new BigIntegerMatrix(rows, cols);
		Matrix<BigInteger> m2 = new BigIntegerMatrix(rows, cols);
		for (int row = 0; row < rows; row++)
			for (int col = 0; col < cols; col++) {
				m1.set(row, col, BigInteger.valueOf(row*col));
				m2.set(row, col, BigInteger.valueOf(row*col + 4));
			}
		Matrix<BigInteger> mSum = m1.plus(m2);
		for (int row = 0; row < rows; row++)
			for (int col = 0; col < cols; col++)
				assertEquals(row + "," + col,
						     m1.get(row, col).add(m2.get(row, col)),
						     mSum.get(row, col));
	}

	public void testDoubleSum() {
		final int rows = 3;
		final int cols = 4;
		Matrix<Double> m1 = new DoubleMatrix(rows, cols);
		Matrix<Double> m2 = new DoubleMatrix(rows, cols);
		for (int row = 0; row < rows; row++)
			for (int col = 0; col < cols; col++) {
				m1.set(row, col, Double.valueOf(row*col));
				m2.set(row, col, Double.valueOf(row*col + 4));
			}
		Matrix<Double> mSum = m1.plus(m2);
		for (int row = 0; row < rows; row++)
			for (int col = 0; col < cols; col++)
				assertEquals(row + "," + col,
				             m1.get(row, col) + m2.get(row, col),
				             mSum.get(row, col));
	}
	
	public void testBigIntegerNumberProduct() {
		int rows = 3;
		int cols = 4;
		Matrix<BigInteger> m = new BigIntegerMatrix(rows, cols);
		for (int row = 0; row < rows; row++)
			for (int col = 0; col < cols; col++)
				m.set(row, col, BigInteger.valueOf(row*col));
		BigInteger lambda = BigInteger.valueOf(5);
		Matrix<BigInteger> mProd = m.times(lambda);
		for (int row = 0; row < rows; row++)
			for (int col = 0; col < cols; col++)
				assertEquals(row + "," + col,
						     m.get(row, col).multiply(lambda),
						     mProd.get(row, col));
	}

	public void testDoubleNumberProduct() {
		int rows = 3;
		int cols = 4;
		Matrix<Double> m = new DoubleMatrix(rows, cols);
		for (int row = 0; row < rows; row++)
			for (int col = 0; col < cols; col++)
				m.set(row, col, Double.valueOf(row*col));
		Double lambda = Double.valueOf(5);
		Matrix<Double> mProd = m.times(lambda);
		for (int row = 0; row < rows; row++)
			for (int col = 0; col < cols; col++)
				assertEquals(row + "," + col,
				             m.get(row, col)*lambda,
				             mProd.get(row, col));
	}
	
	public void testBigIntegerProduct() {
		final int rows = 3;
		final int cols = 4;
		Matrix<BigInteger> mTarget = fill(new long[][] {
				{0, 0, 0},
				{0, 14, 28},
				{0, 28, 56}
		});
		Matrix<BigInteger> m1 = new BigIntegerMatrix(rows, cols);
		Matrix<BigInteger> m2 = new BigIntegerMatrix(cols, rows);
		for (int row = 0; row < rows; row++)
			for (int col = 0; col < cols; col++) {
				m1.set(row, col, BigInteger.valueOf(row*col));
				m2.set(col, row, BigInteger.valueOf(row*col));
			}
		Matrix<BigInteger> mProd = m1.times(m2);
		assertEquals("rows", m1.getRowDimension(), mProd.getRowDimension());
		assertEquals("columns", m2.getColumnDimension(), mProd.getColumnDimension());
		assertEquals("multiplication", mProd, mTarget);
	}

	public void testDoubleProduct() {
		final int rows = 3;
		final int cols = 4;
		Matrix<Double> mTarget = fill(new double[][] {
				{0, 0, 0},
				{0, 14, 28},
				{0, 28, 56}
		});
		Matrix<Double> m1 = new DoubleMatrix(rows, cols);
		Matrix<Double> m2 = new DoubleMatrix(cols, rows);
		for (int row = 0; row < rows; row++)
			for (int col = 0; col < cols; col++) {
				m1.set(row, col, Double.valueOf(row*col));
				m2.set(col, row, Double.valueOf(row*col));
			}
		Matrix<Double> mProd = m1.times(m2);
		assertEquals("rows", m1.getRowDimension(), mProd.getRowDimension());
		assertEquals("columns", m2.getColumnDimension(), mProd.getColumnDimension());
		assertEquals("multiplication", mProd, mTarget);
	}
	
	public void testBigIntegerDimensionExceptions() {
		Matrix<BigInteger> m1 = fill(new long[][] {
				{0, 0, 0},
				{0, 14, 28},
				{0, 28, 56}
		});
		Matrix<BigInteger> m2 = fill(new long[][] {
				{0, 0, 0, 8},
				{0, 14, 28, 9},
				{0, 28, 56, 10}
		});
		try {
			m1.plus(m2);
			fail("expected exception");
		} catch (DimensionMismatchException e) {}
		m2 = fill(new long[][] {
				{17, 21, 12, 5},
				{0, 0, 0, 8},
				{0, 14, 28, 9},
				{0, 28, 56, 10}
		});
		try {
			m1.times(m2);
			fail("expected exception");
		} catch (DimensionMismatchException e) {}
	}

	public void testDoubleDimensionExceptions() {
		Matrix<Double> m1 = fill(new double[][] {
				{0, 0, 0},
				{0, 14, 28},
				{0, 28, 56}
		});
		Matrix<Double> m2 = fill(new double[][] {
				{0, 0, 0, 8},
				{0, 14, 28, 9},
				{0, 28, 56, 10}
		});
		try {
			m1.plus(m2);
			fail("expected exception");
		} catch (DimensionMismatchException e) {}
		m2 = fill(new double[][] {
				{17, 21, 12, 5},
				{0, 0, 0, 8},
				{0, 14, 28, 9},
				{0, 28, 56, 10}
		});
		try {
			m1.times(m2);
			fail("expected exception");
		} catch (DimensionMismatchException e) {}
	}
	
	protected Matrix<BigInteger> fill(long[][] values) {
		int rows = values.length;
		int cols = values.length > 0 ? values[0].length : 0;
		Matrix<BigInteger> m = new BigIntegerMatrix(rows, cols);
		for (int i = 0; i < rows; i++)
			for (int j = 0; j < cols; j++)
				m.set(i, j, BigInteger.valueOf(values[i][j]));
		return m;
	}

	protected Matrix<Double> fill(double[][] values) {
		int rows = values.length;
		int cols = values.length > 0 ? values[0].length : 0;
		Matrix<Double> m = new DoubleMatrix(rows, cols);
		for (int i = 0; i < rows; i++)
			for (int j = 0; j < cols; j++)
				m.set(i, j, Double.valueOf(values[i][j]));
		return m;
		
	}

}
