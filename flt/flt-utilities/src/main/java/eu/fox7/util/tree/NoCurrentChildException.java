package eu.fox7.util.tree;

/**
 * Class <code>NoCurrentChildException</code> is thrown by certain
 * operations in the <a href="Handle.html"><code>Handle</code></a>
 * class when the current child is not yet set or has been unset by
 * previous operations.
 *
 * @author <a href="mailto:eu.fox7@luc.ac.be">Geert Jan Bex</a>
 * @version 1.0
 */
public class NoCurrentChildException extends TreeException {

    private static final long serialVersionUID = 1L;

    /**
	 * Creates a new <code>NoCurrentChildException</code> instance.
	 *
	 */
	public NoCurrentChildException() {
	super();
  }

	/**
	 * <code>getMessage</code> method returns the exception message.
	 *
	 * @return a <code>String</code> value
	 */
	public String getMessage() {
	return "no current child defined";
  }

}
