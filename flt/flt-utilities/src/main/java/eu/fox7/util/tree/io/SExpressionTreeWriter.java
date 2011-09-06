/**
 * Created on Jun 24, 2009
 * Modified on $Date: 2009-10-26 18:37:39 $
 */
package eu.fox7.util.tree.io;

import eu.fox7.util.tree.SExpressionSerializer;
import eu.fox7.util.tree.Serializer;
import eu.fox7.util.tree.Tree;

import java.io.Writer;

/**
 * @author lucg5005
 * @version $Revision: 1.1 $
 *
 */
public class SExpressionTreeWriter implements TreeWriter {

	protected Writer writer;

	public SExpressionTreeWriter() {
		super();
	}

	public SExpressionTreeWriter(Writer writer) {
		this();
		this.writer = writer;
	}

	public void write(Tree tree) throws TreeWriteException {
		write(tree, writer);
	}

	public void write(Tree tree, Writer writer) throws TreeWriteException {
		Serializer<String> serializer = new SExpressionSerializer();
		try {
			writer.write(serializer.serialize(tree));
		} catch (java.io.IOException e) {
			throw new TreeWriteException("write operation failed", e);
		}
	}

}
