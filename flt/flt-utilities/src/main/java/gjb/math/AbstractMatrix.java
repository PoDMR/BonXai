/**
 * Created on Sep 11, 2009
 * Modified on $Date: 2009-10-26 18:37:39 $
 */
package gjb.math;

/**
 * @author lucg5005
 * @version $Revision: 1.1 $
 *
 */
public abstract class AbstractMatrix<T> implements Matrix<T> {

	protected T[][] values;

	AbstractMatrix() {
		super();
	}

	@Override
	abstract public Matrix<T> clone();

	public int getRowDimension() {
		return values.length;
	}

	public int getColumnDimension() {
		return values.length > 0 ? values[0].length : 0;
	}

	public T get(int i, int j) {
		return values[i][j];
	}

	public void set(int i, int j, T value) {
		values[i][j] = value;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean equals(Object m) {
		if (!(m instanceof AbstractMatrix))
			return false;
		Matrix<T> matrix = (Matrix<T>) m;
		if (this.getRowDimension() != matrix.getRowDimension() ||
				this.getColumnDimension() != matrix.getColumnDimension())
			return false;
		for (int row = 0; row < this.getRowDimension(); row++)
			for (int col = 0; col < this.getColumnDimension(); col++)
				if (!this.get(row, col).equals(matrix.get(row, col)))
					return false;
		return true;
	}

	@Override
	public String toString() {
		StringBuilder str = new StringBuilder();
		str.append(getRowDimension()).append(" x ").append(getColumnDimension()).append(":\n");
		for (int row = 0; row < getRowDimension(); row++) {
			for (int col = 0; col < getColumnDimension(); col++) {
				if (col > 0)
					str.append("\t");
				str.append(get(row, col).toString());
			}
			str.append("\n");
		}
		return str.toString();
	}

}
