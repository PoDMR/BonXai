package eu.fox7.util.tree.io;

import eu.fox7.util.tree.Node;
import eu.fox7.util.tree.Tree;

import java.io.Writer;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Class <code>PrefixStringTreeWriter</code> serializes a
 * <code>Tree</code> object to a prefix string representation. <br/>
 *
 * The prefix string representation of the tree corresponding to
 * <code>(a (b) (c))</code> is <code>a b -1 c -1 -1</code>.
 *
 * Prefix string encodings can be useful to compare tree and for tree
 * mining applications.
 *
 * @author <a href="mailto:eu.fox7@luc.ac.be">Geert Jan Bex</a>
 * @version 1.0
 */
public class PrefixStringTreeWriter implements TreeWriter {

	protected Writer writer;

	/**
	 * Creates a new <code>PrefixStringTreeWriter</code> instance.
	 * Note that no <code>java.io.Writer</code> is specified, hence it
	 * should be supplied via the <code>write</code> method.
	 *
	 */
	public PrefixStringTreeWriter() {
		super();
	}

	/**
	 * Creates a new <code>PrefixStringTreeWriter</code> instance.
	 *
	 * @param writer a <code>Writer</code> value that is to be used as
	 * the <code>java.io.Writer</code> in all subsequent
	 * <code>write</code> calls
	 */
	public PrefixStringTreeWriter(Writer writer) {
		this.writer = writer;
	}

	/**
	 * <code>write</code> method serializes the tree using the
	 * <code>java.io.Writer</code> that was provided through the
	 * constructor.
	 *
	 * @param tree a <code>Tree</code> value to be serialized
	 * @exception TreeWriteException if an error occurs
	 */
	public void write(Tree tree) throws TreeWriteException {
		write(tree, this.writer);
	}

	/**
	 * <code>write</code> method writes the serialization of the tree
	 * to the specified writer.
	 *
	 * @param tree a <code>Tree</code> value to serialize
	 * @param writer a <code>Writer</code> value to write the
	 * serialization to
	 * @exception TreeWriteException if an error occurs
	 */
	public void write(Tree tree, Writer writer) throws TreeWriteException {
		if (tree.isEmpty()) return;
		try {
			writer.write(nodeString(tree.getRoot()));
		} catch (java.io.IOException e) {
			throw new TreeWriteException("write operation failed", e);
		}
	}

	protected String nodeString(Node node) {
		StringBuffer str = new StringBuffer();
		List<String> list = prefixListRepresentation(node);
		for (Iterator<String> it = list.iterator(); it.hasNext(); ) {
			String key = it.next();
			if (key == null) {
				str.append("-1");
			} else {
				str.append(key);
			}
			if (it.hasNext()) str.append(" ");
		}
		return str.toString();
	}

	public static List<String> prefixListRepresentation(Tree tree) {
		if (tree.isEmpty()) {
			return new LinkedList<String>();
		} else {
			return prefixListRepresentation(tree.getRoot());
		}
	}

	public static List<String> prefixListRepresentation(Node node) {
		List<String> list = new LinkedList<String>();
		if (node != null) {
			list.add(node.key());
			for (Iterator<Node> it = node.children(); it.hasNext(); ) {
				list.addAll(prefixListRepresentation(it.next()));
			}
			list.add(null);
		}
		return list;
	}

}
