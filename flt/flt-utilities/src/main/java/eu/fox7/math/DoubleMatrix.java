/**
 * Created on Sep 22, 2009
 * Modified on $Date: 2009-10-26 18:37:39 $
 */
package eu.fox7.math;

/**
 * @author lucg5005
 * @version $Revision: 1.1 $
 *
 */
public class DoubleMatrix extends AbstractMatrix<Double> {

	public DoubleMatrix(int rows, int columns) {
		super();
		values = new Double[rows][columns];
		for (int row = 0; row < rows; row++)
			for (int col = 0; col < columns; col++)
				values[row][col] = Double.valueOf(0.0);
	}

	@Override
	public Matrix<Double> clone() {
		Matrix<Double> m = new DoubleMatrix(getRowDimension(),
		                                    getColumnDimension());
		for (int row = 0; row < getRowDimension(); row++)
			for (int col = 0; col < getColumnDimension(); col++)
				m.set(row, col, get(row, col));
		return m;
	}

	public Matrix<Double> plus(Double term) {
		Matrix<Double> sum = new DoubleMatrix(this.getRowDimension(),
		                                      this.getColumnDimension());
		for (int row = 0; row < this.getRowDimension(); row++)
			for (int col = 0; col < this.getColumnDimension(); col++)
				sum.set(row, col, this.get(row, col) + term);
		return sum;
	}

	public Matrix<Double> plus(Matrix<Double> m) {
		if (this.getRowDimension() != m.getRowDimension() ||
				this.getColumnDimension() != m.getColumnDimension())
			throw new DimensionMismatchException(this.getRowDimension(),
					                             this.getColumnDimension(),
					                             m.getRowDimension(),
					                             m.getColumnDimension());
		Matrix<Double> sum = new DoubleMatrix(this.getRowDimension(),
		                                      this.getColumnDimension());
		for (int row = 0; row < this.getRowDimension(); row++)
			for (int col = 0; col < this.getColumnDimension(); col++)
				sum.set(row, col, this.get(row, col) + m.get(row, col));
		return sum;
	}

	public Matrix<Double> times(Double factor) {
		Matrix<Double> prod = new DoubleMatrix(this.getRowDimension(),
		                                       this.getColumnDimension());
		for (int row = 0; row < this.getRowDimension(); row++)
			for (int col = 0; col < this.getColumnDimension(); col++)
				prod.set(row, col, this.get(row, col)*factor);
		return prod;
	}

	public Matrix<Double> times(Matrix<Double> m) {
		if (this.getColumnDimension() != m.getRowDimension())
			throw new DimensionMismatchException(this.getRowDimension(),
                                                 this.getColumnDimension(),
                                                 m.getRowDimension(),
                                                 m.getColumnDimension());
		Matrix<Double> prod = new DoubleMatrix(this.getRowDimension(),
		                                       m.getColumnDimension());
		for (int row = 0; row < this.getRowDimension(); row++)
			for (int col = 0; col < m.getColumnDimension(); col++) {
				double sum = 0.0;
				for (int k = 0; k < this.getColumnDimension(); k++)
					sum += this.get(row, k)*m.get(k, col);
				prod.set(row, col, sum);
			}
		return prod;
	}

}
