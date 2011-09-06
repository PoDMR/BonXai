/**
 * Created on Sep 11, 2009
 * Modified on $Date: 2009-10-26 18:37:39 $
 */
package eu.fox7.math;

/**
 * @author lucg5005
 * @version $Revision: 1.1 $
 *
 */
public interface Matrix<T> {

	public Matrix<T> clone();
	public int getRowDimension();
	public int getColumnDimension();
	public T get(int i, int j);
	public void set(int i, int j, T value);
	public Matrix<T> plus(T term);
	public Matrix<T> plus(Matrix<T> m);
	public Matrix<T> times(T factor);
	public Matrix<T> times(Matrix<T> m);
	
}
