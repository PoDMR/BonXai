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
public class DimensionMismatchException extends RuntimeException {

	private static final long serialVersionUID = -3065856548471074503L;
	protected int r1, c1, r2, c2;

	public DimensionMismatchException(int r1, int c1, int r2, int c2) {
		this.r1 = r1;
		this.c1 = c1;
		this.r2 = r2;
		this.c2 = c2;
	}

	@Override
	public String getMessage() {
		return "dimensions: " + r1 + " x " + c1 + " vs. " + r2 + " x " + c2;
	}

}
