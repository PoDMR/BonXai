/**
 * Created on Sep 11, 2009
 * Modified on $Date: 2009-10-26 18:37:39 $
 */
package gjb.math;

import java.math.BigInteger;

/**
 * @author lucg5005
 * @version $Revision: 1.1 $
 *
 */
public class BigIntegerMatrix extends AbstractMatrix<BigInteger> {

	public BigIntegerMatrix(int rows, int columns) {
		super();
		values = new BigInteger[rows][columns];
		for (int row = 0; row < rows; row++)
			for (int col = 0; col < columns; col++)
				values[row][col] = BigInteger.ZERO;
	}

	@Override
	public Matrix<BigInteger> clone() {
		Matrix<BigInteger> m = new BigIntegerMatrix(getRowDimension(),
				                                    getColumnDimension());
		for (int row = 0; row < getRowDimension(); row++)
			for (int col = 0; col < getColumnDimension(); col++)
				m.set(row, col, get(row, col));
		return m;
	}

	public Matrix<BigInteger> plus(BigInteger term) {
		Matrix<BigInteger> sum = new BigIntegerMatrix(this.getRowDimension(),
		                                              this.getColumnDimension());
		for (int row = 0; row < this.getRowDimension(); row++)
			for (int col = 0; col < this.getColumnDimension(); col++)
				sum.set(row, col, this.get(row, col).add(term));
		return sum;
	}

	public Matrix<BigInteger> plus(Matrix<BigInteger> m) {
		if (this.getRowDimension() != m.getRowDimension() ||
				this.getColumnDimension() != m.getColumnDimension())
			throw new DimensionMismatchException(this.getRowDimension(),
					                             this.getColumnDimension(),
					                             m.getRowDimension(),
					                             m.getColumnDimension());
		Matrix<BigInteger> sum = new BigIntegerMatrix(this.getRowDimension(),
		                                              this.getColumnDimension());
		for (int row = 0; row < this.getRowDimension(); row++)
			for (int col = 0; col < this.getColumnDimension(); col++)
				sum.set(row, col, this.get(row, col).add(m.get(row, col)));
		return sum;
	}

	public Matrix<BigInteger> times(BigInteger factor) {
		Matrix<BigInteger> prod = new BigIntegerMatrix(this.getRowDimension(),
                                                     this.getColumnDimension());
		for (int row = 0; row < this.getRowDimension(); row++)
			for (int col = 0; col < this.getColumnDimension(); col++)
				prod.set(row, col, this.get(row, col).multiply(factor));
		return prod;
	}

	public Matrix<BigInteger> times(Matrix<BigInteger> m) {
		if (this.getColumnDimension() != m.getRowDimension())
			throw new DimensionMismatchException(this.getRowDimension(),
                                                 this.getColumnDimension(),
                                                 m.getRowDimension(),
                                                 m.getColumnDimension());
		Matrix<BigInteger> prod = new BigIntegerMatrix(this.getRowDimension(),
		                                               m.getColumnDimension());
		for (int row = 0; row < this.getRowDimension(); row++)
			for (int col = 0; col < m.getColumnDimension(); col++) {
				BigInteger sum = BigInteger.ZERO;
				for (int k = 0; k < this.getColumnDimension(); k++)
					sum = sum.add(this.get(row, k).multiply(m.get(k, col)));
				prod.set(row, col, sum);
			}
		return prod;
	}

}
