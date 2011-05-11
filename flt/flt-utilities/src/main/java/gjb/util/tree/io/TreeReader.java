package gjb.util.tree.io;

import gjb.util.tree.Tree;

import java.io.Reader;

/**
 * <code>TreeReader</code> is an interface that should be implemented
 * by classes that are used to convert a serialized format to a Tree
 * object.  For an example implementation, see <a
 * href="PrefixStringTreeReader.html"><code>PrefixStringTreeReader</code></a>.
 *
 * @author <a href="mailto:gjb@luc.ac.be">Geert Jan Bex</a>
 * @version 1.0
 */
public interface TreeReader {

	/**
	 * read method to perform the read operation from the file specified
	 * by its path
	 * @param reader reader to read the serialization from
	 * @return the tree that was deserialized
	 * @exception gjb.util.tree.io.TreeReadException thrown when a read
	 *                                            operation fails, the
	 *                                            exception that raised it
	 *                                            is encapsulated.
	 */
	public Tree read(Reader reader)	throws gjb.util.tree.io.TreeReadException;

}
