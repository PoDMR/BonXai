package eu.fox7.util.tree;

import eu.fox7.util.tree.iterators.BreadthFirstIterator;
import eu.fox7.util.tree.iterators.LeafIterator;
import eu.fox7.util.tree.iterators.PostOrderIterator;
import eu.fox7.util.tree.iterators.PreOrderIterator;

import java.io.Reader;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Class <code>Tree</code> represents tree objects that consist of
 * <code>Node</code> objects.  A number of useful methods are
 * implemented as well.  The main work is done in the
 * <code>Node</code> class though. <br/>
 *
 * A simple example of the use of this class is given here.  First,
 * create a <code>Tree</code> object:
 *
 * <pre>
 *   Tree tree = new Tree();
 * </pre>
 *
 * Next, create a number of <code>Node</code> objects that will make
 * up the content of the tree:
 *
 * <pre>
 *   Node n1 = new Node("a");
 *   Node n2 = new Node("b");
 *   n1.addChild(n2);
 *   Node n3 = new Node("c");
 *   n1.addChild(n3);
 *   n2.setValue(new Integer(5));
 * </pre>
 *
 * Finally, add the root node to the tree:
 *
 * <pre>
 *   tree.setRoot(n1);
 * </pre>
 * 
 * The resulting tree can be visualized in several ways (e.g. for
 * debugging purposes):
 * 
 * <pre>
 *   System.out.println(tree.toString());
 *   System.out.println(tree.toDot());
 * </pre>
 *
 * The latter produces a representation in <code>dot</code> format
 * that can be visualized using <a
 * href="http://www.research.att.com/sw/tools/graphviz/">GraphViz</a>
 * while the former would produce the following string representation
 * of the running example:
 *
 * <pre>
 *   key: a
 *   value: []
 *     key: b
 *     value: [5]
 *     key: c
 *     value: []
 * </pre>
 *
 * It can be serialized/deserialized into/from an S-Expression (note
 * that this mechanism is intended for persistent storage, not for
 * cloning a tree):
 * 
 * <pre>
 *   String serializedTree = tree.toSExpression();
 *   Tree newTree1 = new Tree(new StringReader(serializedTree));
 *   Tree newTree2 = Tree.parse(serializedTree);
 * </pre>
 * 
 * The running example would be serialized to &quot;(a (b)
 * (c))&quot;. Note that no attempt is made to serialize/deserialize
 * the value of the nodes since this can not be done
 * generically. However, it is straightforward to implement custom
 * serialization/deserialization mechanisms by defining classes that
 * implement the <a href="TreeReader.html"><code>TreeReader</code></a>
 * and <a href="TreeWriter.html"><code>TreeWriter</code></a>
 * interfaces respectively (see the
 * <code>PrefixStringTreeReader</code> and
 * <code>PrefixStringTreeWriter</code> for examples). <br/>
 *
 * The root of the tree can be accessed and it can be checked whether
 * the tree is empty:
 *
 * <pre>
 *   Node n = tree.root();
 *   boolean b = tree.isEmpty();
 * </pre>
 *
 * <code>n == n1</code> and <code>b == false</code>. <br/>
 *
 * Several <code>Iterator</code> classes have been defined to iterate
 * over the tree's nodes in post-order, pre-order and breadth-first
 * order, as well as over the leaves.  E.g.
 *
 * <pre>
 *   for (Iterator it = tree.preOrderIterator(); it.hasNext(); ) {
 *     Node node = (Node) it.next();
 *     ...
 *   }
 * </pre>
 *
 * Although a tree can be cloned using a constructur with a tree as
 * argument, this is not the preferred method since there's no way to
 * clone values in a generic way (this constructor could be deprecated
 * in future versions).  A more general approach is to define a class
 * that implements the <a
 * href="NodeModifier.html"><code>NodeModifier</code></a> interface
 * such that cloning of values can be dealt with properly. <br/>
 *
 * Similarly, the <a
 * href="NodeVisitor.html"><code>NodeVisitor</code></a> interface can
 * be implemented to modify a tree &quot;in place&quot;.
 *
 * @author <a href="mailto:eu.fox7@luc.ac.be">Geert Jan Bex</a>
 * @version 1.0
 * @todo <ul>
 *
 *   <li>Move S-Expression serialization out of the <code>Tree</code>
 *       and <code>Node</code> classes</li>
 *
 *   <li>Remove dependency on <code>Handle</code> from the
 *       <code>Tree</code> class</li>
 *
 *   <li>Document <code>Handle</code> class</li>
 *
 *   <li>Implement XML serialization/deserialization</li>
 *
 *   <li>Implement XQuery on trees?</li>
 *
 * </ul>
 */
public class Tree<TValue> {

	/**
	 * Node <code>root</code> stores the root of the tree,
	 * <code>null</code> for for empty trees.
	 *
	 */
	protected Node<TValue> root;
  
	/**
	 * Creates a new (empty) <code>Tree</code> instance.
	 *
	 */
	public Tree() {
		this.root = null;
	}

	/**
	 * Creates a new <code>Tree</code> instance having a root node
	 * with the specified key.
	 *
	 * @param key a <code>String</code> value specifying the key of
	 * the root node
	 * @deprecated
	 */
	public Tree(String key) {
		this.root = new Node<TValue>(key);
	}

	/**
	 * Creates a new <code>Tree</code> instance having a root node
	 * with the specified value.
	 *
	 * @param value an <code>Object</code> value specifying the value
	 * of the root node
	 * @deprecated
	 */
	public Tree(TValue value) {
		this.root = new Node<TValue>(value);
	}

	/**
	 * Creates a new <code>Tree</code> instance having a root node
	 * with the specified key and value.
	 *
	 * @param key a <code>String</code> value specifying the root
	 * node's key
	 * @param value an <code>Object</code> value specifying the root
	 * node's value
	 */
	public Tree(String key, TValue value) {
		this.root = new Node<TValue>(key, value);
	}

	/**
	 * Creates a new <code>Tree</code> instance based on another tree
	 * that is deep-cloned.
	 *
	 * @param tree a <code>Tree</code> value to clone
	 */
	public Tree(Tree<TValue> tree) {
		super();
		if (tree.getRoot() != null) {
			setRoot(tree.getRoot().deepClone());
		}
	}

	/**
	 * Creates a new <code>Tree</code> instance based on another tree
	 * that is deep-cloned, the <code>Map</code> parameter will
	 * contain the relation between the nodes of the original tree and
	 * the clone bidirectionally; if <em>n</em> is a node of the
	 * original tree and <em>n'</em> is the corresponding node of the
	 * clone, than both (<em>n</em>, <em>n'</em>) and (<em>n'</em>,
	 * <em>n</em>) are members of the <code>Map</code>.
	 *
	 * @param tree a <code>Tree</code> value to clone
	 * @param nodeMap a <code>Map</code> value containing the relation
	 * between the nodes of the original tree and those of the clone
	 */
	public Tree(Tree<TValue> tree, Map<Node<TValue>,Node<TValue> > nodeMap) {
		super();
		if (tree.getRoot() != null) {
			setRoot(tree.getRoot().deepClone(nodeMap));
		}
	}

	/**
	 * Creates a new <code>Tree</code> instance from an S-expression.
	 * The provided <code>Reader</code> is supposed to be opened and
	 * closed in the context of the caller.
	 *
	 * @param reader a <code>Reader</code> value to read the
	 * S-expression from
	 * @exception java.io.IOException if an the read operations
	 * generate an exception
	 * @exception SExpressionParseException if the S-expression is not
	 * syntactilly correct
	 */
	public Tree(Reader reader)
		throws java.io.IOException, SExpressionParseException {
		super();
		StringBuffer str = new StringBuffer();
		int c;
		while ((c = reader.read()) >= 0) {
			str.append((char) c);
		}
		this.setRoot(Tree.parse(str.toString()).getRoot());
	}
			
	/**
	 * <code>parse</code> factory method parses a <code>string</code>
	 * containing an S-expression and returns the correspoding tree.
	 *
	 * @param str a <code>String</code> value containing an
	 * S-expression
	 * @return a <code>Tree</code> value represented by the
	 * S-expression
	 * @exception SExpressionParseException if the S-exception
	 * contains a syntax error
	 */
	public static Tree parse(String str) throws SExpressionParseException {
		Tree tree = new Tree();
		Node root = Node.parse(str);
		if (root != null) {
			tree.setRoot(root);
		}
		return tree;
	}

	/**
	 * <code>root</code> access method for the tree's root node.
	 *
	 * @return a <code>Node</code> value that is the root of the tree,
	 * <code>null</code> for an empty tree
	 */
	public Node<TValue> getRoot() {
		return this.root;
	}

	/**
	 * code>setRoot</code> access method sets a new root node for the
	 * tree, returning the old root, or <code>null</code> if the tree
	 * was originally empty.
	 *
	 * @param newRoot a <code>Node</code> value specifying the new root of the tree
	 * @return a <code>Node</code> value representing the tree's old root
	 */
	public Node<TValue> setRoot(Node<TValue> newRoot) {
		Node<TValue> oldRoot = this.getRoot();
		this.root = newRoot;
		if (newRoot != null) {
			this.root.setTree(this);
		}
		if (oldRoot != null) {
			oldRoot.setTree(null);
		}
		return oldRoot;
	}

	/**
	 * <code>isEmpty</code> method checks whether the tree is empty.
	 *
	 * @return a <code>boolean</code> value <code>true</code> if the
	 * tree is empty, <code>false</code> otherwise
	 */
	public boolean isEmpty() {
		return this.getRoot() == null;
	}

	/**
	 * <code>height</code> method computes the height of the tree,
	 * which means the maximum length of all paths from root to leaves.
	 *
	 * @return an <code>int</code> value representing the tree's
	 * height, <code>0</code> for an empty tree
	 */
	public int getHeight() {
		if (isEmpty()) {
			return 0;
		} else {
			return getRoot().getHeight();
		}
	}

	/**
	 * <code>maximumNrOfChildren</code> method searches the node of
	 * the tree with the largest number of children and returns that
	 * number.
	 *
	 * @return an <code>int</code> value representing the largest
	 * number of children any node of the tree has
	 */
	public int getMaximumNrOfChildren() {
		if (isEmpty()) {
			return 0;
		} else {
			int max = 0;
			for (Iterator<Node> it = preOrderIterator(); it.hasNext(); ) {
				int nrOfChildren = it.next().getNumberOfChildren();
				if (nrOfChildren > max) {
					max = nrOfChildren;
				}
			}
			return max;
		}
	}

	/**
	 * <code>width</code> method computes the largest number of nodes
	 * at any given generation (or level) of the tree.
	 *
	 * @return an <code>int</code> value representing the width
	 */
	public int getWidth() {
		if (isEmpty()) {
			return 0;
		} else {
			Handle handle = new Handle(this);
			List<Integer> count = new ArrayList<Integer>();
			width(handle, count);
			int max = 0;
			for (int i = 0; i < count.size(); i++) {
				int value = count.get(i);
				if (value > max) {
					max = value;
				}
			}
			return max;
		}
	}

	/**
	 * method that computes the total number of nodes in the tree
	 * @return int number of nodes
	 */
	public int getNumberOfNodes() {
        int number = 0;
        for (Iterator<Node> nodeIt = preOrderIterator(); nodeIt.hasNext(); ) {
            nodeIt.next();
            number++;
        }
        return number;
    }

    protected static void width(Handle handle, List<Integer> counter) {
		int depth = handle.depth();
		if (depth >= counter.size()) {
			counter.add(depth, 1);
		} else {
			int value = counter.get(depth) + 1;
			counter.set(depth, value);
		}
		for (int i = 0; i < handle.nrOfChildren(); i++) {
			handle.down(i);
			width(handle, counter);
			handle.up();
		}
	}

	/**
	 * <code>postOrderIterator</code> method yields an iterator over
	 * the tree's nodes in postorder (root last).  This is a
	 * depth-first iterator.
	 *
	 * @return an <code>Iterator</code> value over the nodes in
	 * postorder
	 */
	public Iterator<Node> postOrderIterator() {
		return new PostOrderIterator(this);
	}

	/**
	 * <code>preOrderIterator</code> method returns an iterator over
	 * the tree's nodes in preorder (root first).  This is a
	 * depth-first iterator.
	 *
	 * @return an <code>Iterator</code> value over the nodes in
	 * preorder
	 */
	public Iterator<Node> preOrderIterator() {
		return new PreOrderIterator(this);
	}

	/**
	 * <code>breadthFirstIterator</code> method returns an iterator of
	 * the tree's nodes in breadth-first order.
	 *
	 * @return an <code>Iterator</code> value breadth-first over the
	 * tree's nodes
	 */
	public Iterator<Node> breadthFirstIterator() {
		return new BreadthFirstIterator(this);
	}

	/**
	 * <code>leaves</code> method returns an iterator over all leaves
	 * of the tree, left to right order.
	 *
	 * @return an <code>Iterator</code> value over the tree's leaves
	 */
	public Iterator<Node> leaves() {
		return new LeafIterator(this);
	}

	/**
	 * <code>visit</code> method visits all nodes in pre-order and
	 * applies the <code>Visitor</code>'s <code>visit</code> method to
	 * each.
	 *
	 * @param visitor a <code>NodeVisitor</code> value object that
	 * implements the <code>Visitor</code> interface
	 * @param parameters a <code>Map</code> value contains parameters
	 * for the <code>Visitor</code>'s <code>visit</visit> method
	 */
	@Deprecated
	public void visit(NodeVisitor visitor, Map<String,Object> parameters)
	        throws NodeTransformException {
		TreeVisitor treeVisitor = new TreeVisitor(this);
		treeVisitor.visit(visitor, parameters);
	}

	/**
	 * <code>modify</code> method copies the tree while applying the
	 * specified modifier to each of the nodes of the original tree.
	 * The <code>Map</code> can be used to provide parameters to the
	 * <code>NodeModifier</code>'s <code>modify</code> method.
	 *
	 * @param modifier a <code>NodeModifier</code> value that will
	 * copy the original nodes
	 * @param parameters a <code>Map</code> value for the modifier
	 * method
	 * @return a copied and potentially modified <code>Tree</code>
	 * value
	 */
	@Deprecated
	public Tree<TValue> modify(NodeModifier modifier, Map<String,Object> parameters)
	        throws NodeTransformException {
		TreeModifier treeModifier = new TreeModifier(this);
		return treeModifier.modify(modifier, parameters);
	}

	/**
	 * <code>findFirstNodeWithKey</code> method to find the first node
	 * with the specified key.  Search is depth-first preorder.
	 *
	 * @param key a <code>String</code> value to serach for
	 * @return a <code>Node</code> value representing the first node
	 * with the specified key
	 */
	public Node<TValue> findFirstNodeWithKey(String key) {
		for (Iterator<Node> it = preOrderIterator(); it.hasNext(); ) {
			Node<TValue> node = it.next();
			if (node.key().equals(key)) {
				return node;
			}
		}
		return null;
	}

	/**
	 * <code>toString</code> method to generate a string
	 * representation of the tree, mainly for debugging and
	 * visualization purposes, not for persistence.
	 *
	 * @return a <code>String</code> value that visualises the tree
	 */
	public String toString() {
        return (new StringSerializer()).serialize(this);
	}

	/**
	 * <code>toSExpression</code> method to generate an S-expression
	 * (a la Lisp) of the tree.  Can be used for persistence.
	 *
	 * @return a <code>String</code> value that is an S-expression
	 * representing the tree
	 */
	public String toSExpression() {
		if (getRoot() != null) {
			return getRoot().toSExpression();
		} else {
			return "()";
		}
	}

	/**
	 * <code>toDot</code> method generates a Dot representation of the
	 * tree for visualization with GraphViz.
	 *
	 * @return a <code>String</code> value representing a Dot graph
	 * description of the tree
	 */
	public String toDot() {
		if (!isEmpty()) {
			Handle handle = new Handle(this);
			return handle.toDot();
		} else {
			StringBuffer str = new StringBuffer();
			str.append("digraph g  {\n\n");
			str.append("}\n");
			return str.toString();
		}
	}

}
