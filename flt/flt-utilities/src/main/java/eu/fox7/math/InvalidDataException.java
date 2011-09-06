/**
 * 
 */
package eu.fox7.math;

/**
 * @author lucg5005
 *
 */
public class InvalidDataException extends Exception {

	private static final long serialVersionUID = 2401362639002028332L;
	protected String msg;

	public InvalidDataException(String msg) {
		this.msg = msg;
	}
	
	public String getMessage() {
		return msg;
	}

}
