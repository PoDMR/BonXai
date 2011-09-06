package eu.fox7.util.tree.io;

import eu.fox7.util.tree.Tree;

import java.io.Writer;

/**
 * <code>TreeWriter</code> is an interface that should be implemented
 * by classes that are used to convert a Tree object to some
 * serialized format.  For an example implementation, see <a
 * href="PrefixStringTreeWriter.html"><code>PrefixStringTreeWriter</code></a>.
 *
 * @author <a href="mailto:eu.fox7@luc.ac.be">Geert Jan Bex</a>
 * @version 1.0
 */
public interface TreeWriter {

	/**
	 * <code>write</code> method must be implemented to write the
	 * specified tree to the <code>java.io.Writer</code> associated
	 * with the <code>TreeWriter</code> object.
	 *
	 * @param tree a <code>Tree</code> value to write
	 * @exception TreeWriteException if an error occurs
	 */
	public void write(Tree tree) throws TreeWriteException;

	/**
	 * <code>write</code> method must be implemented to write the
	 * specified tree to the supplied <code>java.io.Writer</code>.
	 *
	 * @param tree a <code>Tree</code> value to write
	 * @param writer a <code>java.io.Writer</code> value to write the
	 * serialization to
	 * @exception TreeWriteException if an error occurs
	 */
	public void write(Tree tree, Writer writer)
		throws TreeWriteException;

}
