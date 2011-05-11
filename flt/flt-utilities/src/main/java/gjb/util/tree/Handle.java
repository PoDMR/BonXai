package gjb.util.tree;

import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;
import java.util.Stack;

/**
 * Class <code>Handle</code> is modelled after the <a
 * href="http://search.cpan.org/~krburton/Tree-MultiNode-1.0.10/MultiNode.pm">Tree::MultiNode</a>
 * Perl module, see this for API information.  It is not sure whether
 * or not this module will be retained in the package due to its
 * limited added value, hence no effort has been made to document it
 * and its use is discouraged.
 *
 * @author <a href="mailto:gjb@luc.ac.be">Geert Jan Bex</a>
 * @version 1.0
 */
public class Handle {

	protected Tree tree;
	protected Node current;
	protected int currentChild;
	protected int currentDepth;
	protected Stack<Integer> historyCurrent = new Stack<Integer>();
	static String indentation = "  ";

	public Handle(Tree tree) {
		this.tree = tree;
		if (tree.isEmpty()) {
			current = new Node();
			tree.setRoot(current);
		} else {
			current = tree.getRoot();
		}
		setCurrentChildUndefined();
		currentDepth = 0;
	}

	public Handle(Handle handle) {
		this.tree = handle.tree();
		this.current = handle.current;
		this.currentChild = handle.currentChild;
		this.currentDepth = handle.currentDepth;
	}

	public boolean isCurrentChildDefined() {
		return currentChild >= 0;
	}

	protected void setCurrentChildUndefined() {
		currentChild = -1;
	}

	public Tree tree() {
		return tree;
	}

	public String key() {
		return current.key();
	}

	public String setKey(String newKey) {
		return current.setKey(newKey);
	}

	public Object value() {
		return current.value();
	}

	public Object setValue(Object newValue) {
		return current.setValue(newValue);
	}

	protected Node current() {
		return current;
	}

	public Node child()
		throws IndexOutOfBoundsException, NoCurrentChildException {
		if (!this.isCurrentChildDefined()) {
			throw new NoCurrentChildException();
		}
		return current.child(currentChild);
	}

	public Node child(int index)
		throws IndexOutOfBoundsException {
		return current.child(index);
	}

	public boolean addChild() {
		return current.addChild(new Node());
	}

	public boolean addChild(String key) {
		return current.addChild(new Node(key));
	}

	public boolean addChild(Object value) {
		return current.addChild(new Node(value));
	}

	public boolean addChild(String key, Object value) {
		return current.addChild(new Node(key, value));
	}

	public Node addChild(int index)
		throws IndexOutOfBoundsException {
		return current.setChild(index, new Node());
	}

	public Node addChild(String key, int index)
		throws IndexOutOfBoundsException {
		return current.setChild(index, new Node(key));
	}

	public Node addChild(Object value, int index)
		throws IndexOutOfBoundsException {
		return current.setChild(index, new Node(value));
	}

	public Node addChild(String key, Object value, int index)
		throws IndexOutOfBoundsException {
		return current.setChild(index, new Node(key, value));
	}

	public boolean insert(Handle handle) {
		boolean insertOkay = current.addChild(new Node(handle.current()));
		last();
		down();
		for (int i = 0; i < handle.nrOfChildren(); i++) {
			handle.down(i);
			insertOkay = insertOkay && insert(handle);
			handle.up();
		}
		up();
		return insertOkay;
	}

	public Node insert(Handle handle, int index)
		throws IndexOutOfBoundsException {
		Node node = current.setChild(index, new Node(handle.current()));
		down(index);
		for (int i = 0; i < handle.nrOfChildren(); i++) {
			handle.down(i);
			insert(handle);
			handle.up();
		}
		up();
		return node;
	}
		
	public Node removeChild()
		throws NoCurrentChildException {
		if (!this.isCurrentChildDefined()) {
			throw new NoCurrentChildException();
		} else {
			Node oldNode = current.removeChild(currentChild);
			this.setCurrentChildUndefined();
			return oldNode;
		}
	}

	public Node removeChild(int index)
		throws IndexOutOfBoundsException {
		Node oldNode = current.removeChild(index);
		this.setCurrentChildUndefined();
		return oldNode;
	}

	public String childKey()
		throws IndexOutOfBoundsException, NoCurrentChildException {
		return this.child().key();
	}

	public String childKey(int index)
		throws IndexOutOfBoundsException {
		return this.child(index).key();
	}

	public String setChildKey(String newKey)
		throws IndexOutOfBoundsException, NoCurrentChildException {
		return this.child().setKey(newKey);
	}

	public String setChildKey(String newKey, int index)
		throws IndexOutOfBoundsException {
		return this.child(index).setKey(newKey);
	}

	public Object childValue()
		throws IndexOutOfBoundsException, NoCurrentChildException {
		return this.child().value();
	}

	public Object childValue(int index)
		throws IndexOutOfBoundsException {
		return this.child(index).value();
	}

	public Object setChildValue(Object newValue)
		throws IndexOutOfBoundsException, NoCurrentChildException {
		return this.child().setValue(newValue);
	}

	public Object setChildValue(Object newValue, int index)
		throws IndexOutOfBoundsException {
		return this.child(index).setValue(newValue);
	}

	public int nrOfChildren() {
		return current.getNumberOfChildren();
	}

	public boolean hasChildren() {
		return this.nrOfChildren() != 0;
	}

	public Iterator<Node> children() {
		return this.current.children();
	}

	public int position() {
		return currentChild;
	}

	public void setPosition(int index)
		throws IndexOutOfBoundsException {
		if (index < 0 || current.getNumberOfChildren() <= index) {
			throw new IndexOutOfBoundsException();
		} else {
			currentChild = index;
		}
	}

	public void first()
		throws IndexOutOfBoundsException {
		if (!this.hasChildren()) {
			throw new IndexOutOfBoundsException();
		} else {
			currentChild = 0;
		}
	}

	public void last()
		throws IndexOutOfBoundsException {
		if (!this.hasChildren()) {
			throw new IndexOutOfBoundsException();
		} else {
			currentChild = current.getNumberOfChildren() - 1;
		}
	}

	public boolean next() {
		if (!this.hasChildren()) {
			return false;
		} else if (!this.isCurrentChildDefined()) {
			this.first();
			return true;
		} else if (currentChild < current.getNumberOfChildren() - 1) {
			currentChild++;
			return true;
		} else {
			return false;
		}
	}

	public boolean previous() {
		if (!this.hasChildren()) {
			return false;
		} else if (!this.isCurrentChildDefined()) {
			this.last();
			return true;
		} else if (0 < currentChild) {
			currentChild--;
			return true;
		} else {
			return false;
		}
	}

	public boolean down()
		    throws IndexOutOfBoundsException {
		if (!this.isCurrentChildDefined()) {
			return false;
		} else if (current.child(currentChild) != null) {
			current = current.child(currentChild);
			historyCurrent.push(new Integer(currentChild));
			setCurrentChildUndefined();
			currentDepth++;
			return true;
		} else {
			return false;
		}
	}

	public boolean down(int index)
		throws IndexOutOfBoundsException {
		this.setPosition(index);
		return this.down();
	}

	public boolean up() {
		if (current.getParent() != null) {
			current = current.getParent();
			currentChild = historyCurrent.pop();
			currentDepth--;
			return true;
		} else {
			return false;
		}
	}

	public boolean top() {
		current = tree.getRoot();
		setCurrentChildUndefined();
		currentDepth = 0;
		return true;
	}

	public int depth() {
		return currentDepth;
	}

	public static void setIndentation(String indentation) {
		Handle.indentation = indentation;
	}

	public static String indentation() {
		return Handle.indentation;
	}

	public String toString() {
		StringBuffer str = new StringBuffer();
		String indent = "";
		for (int i = 0; i < this.depth(); i++) {
			indent += Handle.indentation();
		}
		str.append(indent).append("key: ");
		if (this.key() != null) {
			str.append(this.key());
		}
		str.append("\n");
		str.append(indent).append("value: [");
		if (this.value() != null) {
			str.append(this.value().toString());
		}
		str.append("]\n");
		for (int i = 0; i < this.nrOfChildren(); i++) {
			this.down(i);
			str.append(this.toString());
			this.up();
		}
		return str.toString();
	}

	public String toSExpression() {
		return this.tree().toSExpression();
	}

	public String toDot() {
		StringBuffer str = new StringBuffer();
		StringBuffer strVertices = new StringBuffer();
		StringBuffer strEdges = new StringBuffer();
		toDot(strVertices, strEdges, new HashMap<Node,String>(), new Counter());
		str.append("digraph g  {\n\n");
		str.append(strVertices).append("\n").append(strEdges);
		str.append("}\n");
		return str.toString();
	}

	protected void toDot(StringBuffer strVertices, StringBuffer strEdges,
						 Map<Node,String> map, Counter index) {
		String nodeID = "n" + index.value();
		index.incr();
		strVertices.append("  ").append(nodeID);
		strVertices.append( " [label=\"").append(key()).append("\"];\n");
		map.put(current, nodeID);
		for (int i = 0; i < nrOfChildren(); i++) {
			down(i);
			toDot(strVertices, strEdges, map, index);
			strEdges.append("  ").append(nodeID).append(" -> ");
			strEdges.append(map.get(current)).append(";\n");
			up();
		}
	}

	protected static class Counter {

		protected int counter;

		public Counter() {
			counter = 0;
		}

		public void incr() {
			counter++;
		}

		public int value() {
			return counter;
		}

	}

}
