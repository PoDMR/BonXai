package eu.fox7.util.tree.io;

import eu.fox7.util.tree.Node;
import eu.fox7.util.tree.Tree;

import java.io.Reader;
import java.io.IOException;
import java.util.StringTokenizer;

/**
 * Class <code>PrefixStringTreeReader</code> deserializes a
 * <code>Tree</code> object from a prefix string representation. <br/>
 *
 * The prefix string representation <code>a b -1 c -1 -1</code> is
 * equivalent to the S-Expression <code>(a (b) (c))</code>. <br/>
 *
 * Prefix string encodings can be useful to compare tree and for tree
 * mining applications.
 *
 * @author <a href="mailto:eu.fox7@luc.ac.be">Geert Jan Bex</a>
 * @version 1.0
 */
public class PrefixStringTreeReader implements TreeReader {

	/**
	 * <code>read</code> factory method takes a
	 * <code>java.io.Reader</code> as input, reads a tree
	 * representation in prefix string format from it and constructs
	 * the corresponding <code>Tree</code> object.
	 *
	 * @param reader a <code>Reader</code> value to read the
	 * representation from
	 * @return a <code>Tree</code> value, this is the corresponding tree
	 * @exception TreeReadException if the presentation contains a
	 * syntax error or an <code>IOException</code> occurs
	 */
	public Tree read(Reader reader) throws TreeReadException {
		Tree tree = new Tree();
		Node currentNode = null;
		StringBuffer strBuffer = new StringBuffer();
		int c;
		try {
			while ((c = reader.read()) != -1) {
				strBuffer.append((char) c);
			}
		} catch (IOException e) {
			throw new TreeReadException("read exception", e);
		}
		if (strBuffer.toString().trim().equals("")) {
			return tree;
		}
		StringTokenizer st = new StringTokenizer(strBuffer.toString());
		int position = 0;
		while (st.hasMoreTokens()) {
			String token = st.nextToken();
			position++;
			if (!token.equals("-1")) {
				Node newNode = new Node(token);
				if (currentNode == null) {
					tree.setRoot(newNode);
				} else {
					currentNode.addChild(newNode);
				}
				currentNode = newNode;
			} else {
				if (!currentNode.equals(tree.getRoot())) {
					currentNode = currentNode.getParent();
				} else {
					break;
				}
			}
		}
		if (st.hasMoreTokens()) {
			throw new TreeReadException("unexpected token at position " +
										position + ": '" + st.nextToken() +
										"' in '" + strBuffer.toString() + "'");
		}
		if (!currentNode.equals(tree.getRoot())) {
			throw new TreeReadException("too few tokens in '" +
										strBuffer.toString() + "'");
		}
		return tree;
	}

}
