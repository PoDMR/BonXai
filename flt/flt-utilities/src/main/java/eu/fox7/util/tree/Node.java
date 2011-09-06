package eu.fox7.util.tree;

import eu.fox7.util.tree.iterators.BreadthFirstIterator;
import eu.fox7.util.tree.iterators.LeafIterator;
import eu.fox7.util.tree.iterators.PostOrderIterator;
import eu.fox7.util.tree.iterators.PreOrderIterator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.*;

/**
 * Class <code>Node</code> represents nodes in tree objects.  Nodes
 * have keys (strings) and values (objects) and hold references to
 * their parent and children.  If they are part of a tree, a reference
 * to the latter is also stored.  A number of useful methods are
 * implemented as well. <br/>
 *
 * A <code>Node</code> can be created using various constructors, e.g.
 *
 * <pre>
 *   Node n1 = new Node();
 *   Node n2 = new Node("alpha", new Integer(5));
 *   Node n3 = new Node("beta");
 *   Node n4 = new Node("gamma");
 * </pre>
 *
 * A node contains two types of information, a <code>key</code> and a
 * <code>value</code>, the former is intended as a label and can only
 * be a <code>java.lang.String</code> while the latter can be any
 * <code>java.lang.Object</code> and can be thought of as data.  Both
 * have setters/getters:
 * 
 * <pre>
 *   n1.setKey("aleph");
 *   n1.setValue(new java.util.Date());
 *   String label = n2.key();
 *   Integer i = (Integer) n2.value();
 * </pre>
 *
 * so <code>label.equals("alpha")</code> and <code>i.intValue() ==
 * 5</code>. <br/>
 *
 * Child nodes can be added to a node in several ways, the most
 * obvious being one by one without position information:
 * 
 * <pre>
 *   n1.addChild(n2);
 *   n1.addChild(n3);
 *   n1.addChild(n4);
 * </pre>
 *
 * In the previous example, nodes <code>n2</code>, <code>n3</code> and
 * <code>n4</code> will be the first, second and third child of node
 * <code>n1</code> respectively. <br/>
 *
 * However, nodes can be inserted at a specified position by providing
 * an index to the <code>addChild</code> method, or can replace an
 * existing child using the <code>setChild</code> method.
 * 
 * <pre>
 *   n1.addChild(1, new Node("beta'"));
 *   n1.setChild(2, new Node("epsilon"));
 * </pre>
 *
 * It is easy to iterate over the children of a node:
 * <pre>
 *   for (Iterator it = n1.children(); it.hasNext(); ) {
 *     Node child = (Node) it.next();
 *     ...
 *   }
 * </pre>
 *
 * Individual children can be accessed by position:
 * <pre>
 *   Node n = n1.child(1);
 * </pre>
 *
 * where <code>n.key().equals("beta'")</code>. <br/>
 *
 * Children can be removed from a node either by position or by
 * reference:
 * 
 * <pre>
 *   n1.removeChild(2);
 *   n1.removeChild(n2);
 * </pre>
 *
 * Several properties of nodes can be queried such as the
 * <code>Tree</code> object they are part of (if any), their parent
 * <code>Node</code> (if any), their number of children, their
 * position in the list of children of their parent, their previous or
 * next sibling (if any). E.g.
 *
 * <pre>
 *   Tree t = n1.tree();
 *   Node n = n3.parent();
 *   int N = n1.nrOfChildren();
 *   int i = n3.childIndex();
 *   int j = n1.childIndex();
 *   Node p = n3.previousSibling();
 * </pre>
 *
 * <code>t == null</code> since the node has not been added to a tree,
 * <code>n == n1</code>, <code>N == 2</code>, <code>i == 1</code>,
 * <code>j == -1</code> since it has no parent, hence no child index
 * and <code>p.key().equals("beta'")</code>. <br/>
 *
 * Similar <code>Iterator</code>s as for <a
 * href="Tree.html"><code>Tree</code></a> have been defined. <br/>
 *
 * For convenience, a number of properties can be checked directly
 * using <code>isLeaf()</code>, <code>hasParent()</code>,
 * <code>hasChildren()</code>, <code>hasPreviousSibling()</code>,
 * <code>hasNextSibling()</code>.
 *
 * @author <a href="mailto:eu.fox7@luc.ac.be">Geert Jan Bex</a>
 * @version 1.0
 */
public class Node <TValue> implements java.io.Serializable {

    private static final long serialVersionUID = 1L;
    /**
	 * variable <code>parent</code> holds a reference to the node's
	 * parent, <code>null</code> if there's none.
	 *
	 */
	protected Node<TValue> parent;
	/**
	 * variable <code>key</code> holds the node's key,
	 * <code>null</code> if there's none.
	 *
	 */
	protected String key;
	/**
	 * variable <code>value</code> holds the node's value,
	 * <code>null</code> if there's none.
	 *
	 */
	protected TValue value;
	/**
	 * variable <code>children</code> is a <code>List</code>
	 * containing the node's children, empty if there are none.
	 *
	 */
	protected List<Node<TValue> > children;
	/**
	 * variable <code>tree</code> holds a reference to the tree the
	 * node is part of, <code>null</code> otherwise
	 *
	 */
	protected Tree<TValue> tree;

	/**
	 * Creates a new <code>Node</code> instance.
	 *
	 */
	public Node() {
		super();
		this.init();
	}

	/**
	 * Creates a new <code>Node</code> instance with the given string as
	 * key.
	 *
	 * @param key a <code>String</code> value for the node's key
	 */
	public Node(String key) {
		super();
		this.init();
		this.key = key;
	}
  
	/**
	 * Creates a new <code>Node</code> instance with the given object
	 * as value.
	 *
	 * @param value an <code>Object</code> that is to be the node's
	 * value
	 */
	public Node(TValue value) {
		super();
		this.init();
		this.value = value;
	}

	/**
	 * Creates a new <code>Node</code> instance with the given string
	 * as key and the given object as value.
	 *
	 * @param key a <code>String</code> value for the node's key
	 * @param value an <code>Object</code> value for the node's value
	 */
	public Node(String key, TValue value) {
		super();
		this.init();
		this.key = key;
		this.value = value;
	}

	/**
	 * Creates a new <code>Node</code> instance based on an existing
	 * node; the new node will have the same key and value as the
	 * original node; note that the value will be a reference to the
	 * same object, the object itself is not cloned.
	 *
	 * @param node a <code>Node</code> value to clone
	 */
	public Node(Node<TValue> node) {
		super();
		this.init();
		this.key = node.key();
		this.value = node.value();
	}

	/**
	 * Creates a new <code>Node</code> instance based on an existing
	 * node; the new node will have the same key and value as the
	 * original node; note that the value will be a reference to the
	 * same object, the object itself is not cloned; a one-to-one
	 * mapping of the original and the new node is maintained in the
	 * Map (bidirectional).
	 *
	 * @param node a <code>Node</code> value to clone
	 * @param nodeMap a <code>Map</code> value to maintain a
	 * bidirectional mapping in
	 */
	public Node(Node<TValue> node, Map<Node<TValue>,Node<TValue> > nodeMap) {
		super();
		this.init();
		this.key = node.key();
		this.value = node.value();
		nodeMap.put(node, this);
		nodeMap.put(this, node);
	}

	/**
	 * <code>deepClone</code> method recursively clones a node and its
	 * descendants.
	 *
	 * @return a <code>Node</code> value that is a deep clone of the
	 * original node
	 */
	public Node<TValue> deepClone() {
		Node<TValue> newNode = new Node<TValue>(this);
		for (Iterator<Node<TValue> > it = children(); it.hasNext(); ) {
			Node<TValue> child = it.next();
			newNode.addChild(child.deepClone());
		}
		return newNode;
	}

	/**
	 * <code>deepClone</code> method recursively clones a node and its
	 * descendants; a bidirectional mapping between the new and the
	 * original nodes is maintained in the provided Map.
	 *
	 * @param nodeMap a <code>Map</code> value to maintain the
	 * bidirectional map in
	 * @return a <code>Node</code> value that is the deep clone of the
	 * original node
	 */
	public Node<TValue> deepClone(Map<Node<TValue>,Node<TValue> > nodeMap) {
		Node<TValue> newNode = new Node<TValue>(this, nodeMap);
		for (Iterator<Node<TValue> > it = children(); it.hasNext(); ) {
			Node<TValue> child = it.next();
			newNode.addChild(child.deepClone(nodeMap));
		}
		return newNode;
	}

	Node<TValue> modify(NodeModifier modifier, Map<String,Object> parameters)
	        throws NodeTransformException {
		Node<TValue> newNode = modifier.modify(this, parameters);
		for (Iterator<Node<TValue> > it = this.children(); it.hasNext(); ) {
			Node<TValue> child = it.next();
			newNode.addChild(child.modify(modifier, parameters));
		}
		return newNode;
	}

	/**
	 * <code>parse</code> static method parses a node description in
	 * S-Expression format.
	 *
	 * @param str a <code>String</code> value that is an S-Expression
	 * @return a <code>Node</code> value that is described by the
	 * S-Expression
	 * @exception SExpressionParseException if an parse exception
	 * occurs due to a syntax error in the S-Expression
	 */
	public static <TValue> Node<TValue> parse(String str) throws SExpressionParseException {
		Pattern emptyNodePattern = Pattern.compile("\\A\\s*\\(\\s*\\)\\s*\\Z");
		Matcher emptyNodeMatcher = emptyNodePattern.matcher(str);
		if (emptyNodeMatcher.matches()) {
			return null;
		}
		Pattern nodePattern = Pattern.compile("\\A\\s*\\(\\s*(\\S+)(?:\\s+(.+)\\s*)?\\)\\s*\\Z");
		Matcher nodeMatcher = nodePattern.matcher(str);
		if (nodeMatcher.matches()) {
			String key = nodeMatcher.group(1);
			String childrenStr = nodeMatcher.group(2);
			Node<TValue> node = new Node<TValue>(key);
			if (childrenStr != null) {
				List<Node<TValue> > children = Node.parseChildren(childrenStr);
				for (Iterator<Node<TValue> > it = children.iterator(); it.hasNext(); ) {
					node.addChild(it.next());
				}
			}
			return node;
		} else {
			throw new SExpressionParseException(str);
		}
	}

	protected static<TValue> List<Node<TValue> > parseChildren(String str)
		    throws SExpressionParseException {
		List<Node<TValue> > children = new LinkedList<Node<TValue> >();
		int counter = 0;
		int previousPos = 0;
		for (int pos = 0; pos < str.length(); pos++) {
			if (str.charAt(pos) == '(') {
				counter++;
			} else if (str.charAt(pos) == ')') {
				counter--;
			} else {
				continue;
			}
			if (counter < 0) {
				throw new SExpressionParseException(str);
			}
			if (counter == 0) {
				children.add(Node.<TValue>parse(str.substring(previousPos, pos + 1)));
				previousPos = pos + 1;
			}
		}
		if (counter != 0) {
			throw new SExpressionParseException(str);
		}
		return children;
	}

	/**
	 * code>init</code> method sets the initial values for the node's
	 * attributes; it should never be called outside of a constructor.
	 *
	 */
	protected void init() {
		this.parent = null;
		this.children = new ArrayList<Node<TValue> >();
		this.key = null;
		this.value = null;
		this.tree = null;
	}

	/**
	 * <code>parent</code> method returns the parent of the node,
	 * <code>null</code> if there's none.
	 *
	 * @return a <code>Node</code> value or <code>null</code> if
	 * there's none
	 */
	public Node<TValue> getParent() {
		return this.parent;
	}

	/**
	 * <code>hasParent</code> method checks whether the node has a
	 * parent and returns true in that case, false otherwise.
	 *
	 * @return a <code>boolean</code> value, true if the node has a
	 * parent, false otherwise
	 */
	public boolean hasParent() {
		return this.parent != null;
	}

	/**
	 * <code>setParent</code> method sets the parent of the node; to
	 * minimize possible errors, this method is protected.
	 *
	 * @param parent a <code>Node</code> value that is the node's parent
	 * @return a <code>Node</code> value the node that previously was
	 * the node's parent, null if the node had no parent
	 */
	protected Node<TValue> setParent(Node<TValue> parent) {
		Node<TValue> oldParent = this.getParent();
		this.parent = parent;
		return oldParent;
	}

    public boolean isDecendantOf(Node<TValue> ancestor) {
        Node<TValue> node = this;
        while (node != ancestor && node.hasParent())
            node = node.getParent();
        return node == ancestor;
    }

    public static<TValue> Node<TValue> getLeastCommonAncestor(Node<TValue> node1, Node<TValue> node2) {
        if (node1.isDecendantOf(node2))
            return node2;
        boolean found = false;
        while (!(found = node2.isDecendantOf(node1)) && node1.hasParent())
            node1 = node1.getParent();
        return found ? node1 : null;
    }

    /**
	 * <code>tree</code> method returns the <code>Tree</code> the node
	 * belongs to if any, <code>null</code> otherwise.
	 *
	 * @return a <code>Tree</code> value the node is part of,
	 * <code>null</code> if the node isn't part of a <code>Tree</code>
	 */
	public Tree<TValue> getTree() {
		return this.tree;
	}

	/**
	 * <code>setTreef</code> method sets the <code>Tree</code> the
	 * node is to be part of; to minimize possible errors, this method
	 * is protected.
	 *
	 * @param tree a <code>Tree</code> value that the node is part of
	 * @return a <code>Tree</code> value the node was previously part
	 * of, null if the node was not part of a <code>Tree</code>
	 */
	protected Tree<TValue> setTree(Tree<TValue> tree) {
		Tree<TValue> oldTree = this.getTree();
		this.tree = tree;
		for (Iterator<Node<TValue> > it = children(); it.hasNext(); ) {
			it.next().setTree(tree);
		}
		return oldTree;
	}

	/**
	 * <code>key</code> method returns the node's key.
	 *
	 * @return a <code>String</code> value that is the node's key
	 */
	public String key() {
		return this.key;
	}

	/**
	 * <code>getKey</code> method returns the node's key; supplied to
	 * make the API JavaBean complient.
	 *
	 * @return a <code>String</code> value that is the node's key
	 */
	public String getKey() {
		return key();
	}

	/**
	 * <code>setKey</code> method sets the value of the node's key.
	 *
	 * @param key a <code>String</code> value for the node's key
	 * @return a <code>String</code> value the previous key,
	 * <code>null</code> if the node had no key yet.
	 */
	public String setKey(String key) {
		String oldKey = this.key;
		this.key = key;
		return oldKey;
	}

	/**
	 * <code>value</code> method returns the node's value.
	 *
	 * @return an <code>Object</code> value that is node's value
	 */
	public TValue value() {
		return this.value;
	}

	/**
	 * <code>getValue</code> method returns the node's value; supplied to
	 * make the API JavaBean complient.
	 *
	 * @return an <code>Object</code> value that is node's value
	 */
	public TValue getValue() {
		return value();
	}

	/**
	 * <code>setValue</code> method sets the node's value to the given
	 * object.
	 *
	 * @param value an <code>Object</code> value to set the node's value to
	 * @return an <code>Object</code> value that was the previous
	 * value, <code>null</code> if there was none
	 */
	public TValue setValue(TValue value) {
		TValue oldValue = this.value;
		this.value = value;
		return oldValue;
	}

	/**
	 * <code>height</code> method yields the depth of a node in its
	 * tree.  The root node is at height 1, for each consecutive
	 * generation the height is incremented.
	 *
	 * @return an <code>int</code> value corresponding to the node's
	 * height
	 */
	public int getHeight() {
		int maxHeight = 0;
		for (Iterator<Node<TValue> > it = children(); it.hasNext(); ) {
			int childHeight = it.next().getHeight();
			if (childHeight > maxHeight) {
				maxHeight = childHeight;
			}
		}
		return 1 + maxHeight;
	}

	public int getDepth() {
	    int depth = 1;
	    Node<TValue> node = this;
	    while (node.hasParent()) {
	        node = node.getParent();
	        depth++;
	    }
	    return depth;
	}

	/**
	 * <code>postOrderIterator</code> method returns an iterator of
	 * the node and its children in post-order.
	 *
	 * @return an <code>Iterator</code> value, post order over the
	 * node and its children
	 */
	@Deprecated
	public Iterator<Node> postOrderIterator() {
		return new PostOrderIterator(this);
	}

	/**
	 * <code>postOrderIterator</code> method returns an iterator of
	 * the node and its children in post-order.
	 *
	 * @param node a <code>Node</code> value to determine the iterator
	 * for
	 * @return an <code>Iterator</code> value, post order over the
	 * node and its children
	 */
	@Deprecated
	public static Iterator<Node> postOrderIterator(Node node) {
		return new PostOrderIterator(node);
	}

	/**
	 * <code>preOrderIterator</code> method returns an iterator of
	 * the node and its children in pre-order (depth-first).
	 *
	 * @return an <code>Iterator</code> value, pre order over the
	 * node and its children
	 */
	@Deprecated
	public Iterator<Node> preOrderIterator() {
		return new PreOrderIterator(this);
	}

	/**
	 * <code>preOrderIterator</code> method returns an iterator of
	 * the node and its children in pre-order (depth-first).
	 *
	 * @param node a <code>Node</code> value to determine the iterator
	 * for
	 * @return an <code>Iterator</code> value, pre order over the
	 * node and its children
	 */
	@Deprecated
	public static Iterator<Node> preOrderIterator(Node node) {
		return new PreOrderIterator(node);
	}

	/**
	 * <code>leaves</code> method returns an iterator over the leaves
	 * of the tree with this node as root in left-to-right order.
	 *
	 * @return an <code>Iterator</code> value, left-to-right over the
	 * leaves
	 */
	public Iterator<Node> leaves() {
		return new LeafIterator(this);
	}

	/**
	 * <code>leaves</code> method returns an iterator over the leaves
	 * of the tree with this node as root in left-to-right order.
	 *
	 * @param node a <code>Node</code> value to determine the iterator
	 * for
	 * @return an <code>Iterator</code> value, left-to-right over the
	 * leaves
	 */
	public static Iterator<Node> leaves(Node node) {
		return new LeafIterator(node);
	}

	/**
	 * <code>breadthFirstIteratorrea</code> method returns an iterator
	 * over the node and its children in breadth-first order.
	 *
	 * @return an <code>Iterator</code> value, braadth-first over the
	 * node and its children
	 */
	public Iterator<Node> breadthFirstIterator() {
		return new BreadthFirstIterator(this);
	}

	/**
	 * <code>breadthFirstIteratorrea</code> method returns an iterator
	 * over the node and its children in breadth-first order.
	 *
	 * @param node a <code>Node</code> value to determine the iterator
	 * for
	 * @return an <code>Iterator</code> value, breadth-first over the
	 * node and its children
	 */
	public static Iterator<Node> breadthFirstIterator(Node node) {
		return new BreadthFirstIterator(node);
	}

	/**
	 * <code>nrOfChildren</code> method returns the number of children
	 * of the node.
	 *
	 * @return an <code>int</code> value that is the node's number of
	 * children
	 */
	public int getNumberOfChildren() {
		return this.children.size();
	}

	/**
	 * <code>hasChildren</code> method returns <code>true</code> if
	 * the node has children, <code>false</code> otherwise.
	 *
	 * @return a <code>boolean</code> value, <code>true</code> if the
	 * node has children, <code>false</code> otherwise
	 */
	public boolean hasChildren() {
		return getNumberOfChildren() > 0;
	}

	/**
	 * <code>isLeaf</code> method returns <code>true</code> if the
	 * node is a leaf, <code>false</code> otherwise.
	 *
	 * @return a <code>boolean</code> value, <code>true</code> if the
	 * node is a leaf, <code>false</code> otherwise
	 */
	public boolean isLeaf() {
		return getNumberOfChildren() == 0;
	}

	/**
	 * <code>child</code> method returns the node's child at the
	 * specified position; if the index is less than zero or larger or
	 * equal than the number of children, an exception is thrown.
	 *
	 * @param index an <code>int</code> value, 0-based index of the
	 * child to return
	 * @return a <code>Node</code> value, the child node at specified
	 * position
	 * @exception IndexOutOfBoundsException if an invalid index is
	 * specified
	 */
	public Node<TValue> child(int index) throws IndexOutOfBoundsException {
		return this.children.get(index);
	}
  
	/**
	 * <code>getChild</code> method returns the node's child at the
	 * specified position; if the index is less than zero or larger or
	 * equal than the number of children, an exception is thrown;
	 * supplied to make the API JavaBean compliant.
	 *
	 * @param index an <code>int</code> value, 0-based index of the
	 * child to return
	 * @return a <code>Node</code> value, the child node at specified
	 * position
	 * @exception IndexOutOfBoundsException if an invalid index is
	 * specified
	 */
	public Node<TValue> getChild(int index) throws IndexOutOfBoundsException {
		return child(index);
	}

	/**
	 * <code>getChildren</code> method returns the list containing the
	 * node's children.  Although this is a public method, one should
	 * refrain from using it.  It was introduced to facilitate JXPath
	 * queries via the <code>NodeBeanInfo</code> class.
	 *
	 * @return a <code>List</code> value with the node's children in
	 * an unmodifiable list
	 */
	public List<Node<TValue> > getChildren() {
		return Collections.unmodifiableList(this.children);
	}

	/**
	 * <code>children</code> method returns an <code>Iterator</code>
	 * over the node's <code>List</code> of children.
	 *
	 * @return an <code>Iterator</code> value over the node's children
	 */
	public Iterator<Node<TValue> > children() {
		return this.children.listIterator();
	}

	/**
	 * <code>addChild</code> method adds a child node to the node by
	 * appending it to the list of children.
	 *
	 * @param child a <code>Node</code> value to be added as child
	 * @return a <code>boolean</code> value, <code>true</code> if the
	 * operation was succesful, <code>false</code> otherwise
	 */
	public boolean addChild(Node<TValue> child) {
		child.setParent(this);
		child.setTree(this.getTree());
		return this.children.add(child);
	}

	/**
	 * <code>addChild</code> method inserts a child node to the node on
	 * the specified position in the list of children. If the index is
	 * larger or equal than the number of children, an exception will be
	 * thrown.
	 *
	 * @param index an <code>int</code> value that is the position to
	 * insert the child node
	 * @param child a <code>Node</code> value to add as child
	 * @exception IndexOutOfBoundsException if an invalid index was
	 * specified
	 */
	public void addChild(int index, Node<TValue> child) {
		child.setParent(this);
		child.setTree(this.getTree());
		this.children.set(index, child);
	}

	/**
	 * <code>addChildren</code> method adds a <code>Collection</code>
	 * of children to the current node.
	 *
	 * @param newChildren a <code>Collection</code> value containing
	 * the children to add
	 * @return a <code>boolean</code> value, <code>true</code> if the
	 * operation was succesful, <code>false</code> otherwise
	 */
	public boolean addChildren(Collection<Node<TValue> > newChildren) {
		boolean success = true;
		for (Iterator<Node<TValue> > it = newChildren.iterator(); it.hasNext(); ) {
			success = success && this.addChild(it.next());
		}
		return success;
	}

	/**
	 * <code>setChild</code> method replaces a child node at the
	 * specified position; the old child node is returned. If
	 * necessary, the list of children is padded will
	 * <code>null</code> values to adapt the size to the index
	 * specified.
	 *
	 * @param index an <code>int</code> value that is the position of
	 * the child to replace
	 * @param child a <code>Node</code> value that is the new child
	 * node
	 * @return a <code>Node</code> value that is the old child node
	 * @exception IndexOutOfBoundsException if an invalid index was
	 * specified
	 */
	public Node<TValue> setChild(int index, Node<TValue> child)
		throws IndexOutOfBoundsException {
		child.setParent(this);
		child.setTree(this.getTree());
		for (int i = children.size(); i <= index; i++) {
			children.add(null);
		}
		Node<TValue> oldChild = this.children.set(index, child);
		if (oldChild != null)
		    oldChild.setParent(null);
		return oldChild;
	}

	/**
	 * <code>removeChild</code> method removes a child at the
	 * specified position and returns it; its siblings will be shifted
	 * to the left.
	 *
	 * @param index an <code>int</code> value that is the position of
	 * the child to remove
	 * @return a <code>Node</code> value is the node that was removed
	 * @exception IndexOutOfBoundsException if an invalid index was
	 * specified
	 */
	public Node<TValue> removeChild(int index)
		throws IndexOutOfBoundsException {
		Node<TValue> exChild = this.children.remove(index);
		if (exChild != null) {
			exChild.setParent(null);
			exChild.setTree(null);
		}
		return exChild;
	}

	/**
	 * <code>removeChild</code> method removes the specified child
	 * from the node; its siblings will be shifted to the left.
	 *
	 * @param child a <code>Node</code> value to be removed
	 * @return a <code>boolean</code> value, <code>true</code> if the
	 * operation succeeds, <code>false</code> otherwise
	 */
	public boolean removeChild(Node<TValue> child) {
		child.setParent(null);
		return this.children.remove(child);
	}

	/**
	 * <code>childIndex</code> method returns the index a node has in
	 * the list of its parent's children.
	 *
	 * @return an <code>int</code> value that is the node's child index
	 */
	public int getChildIndex() {
		if (this.getParent() != null) {
			return this.getParent().children.indexOf(this);
		} else {
			return -1;
		}
	}

	/**
	 * <code>hasNextSibling</code> method returns <code>true</code> if
	 * the node has right siblings, <code>false</code> otherwise.
	 *
	 * @return a <code>boolean</code> value, <code>true</code> if the
	 * node has right siblings, <code>false</code> if it is the
	 * rightmost child
	 */
	public boolean hasNextSibling() {
		if (this.getParent() != null) {
			int indexNextSibling = this.getChildIndex() + 1;
			return indexNextSibling < this.getParent().getNumberOfChildren();
		} else {
			return false;
		}
	}

	/**
	 * <code>nextSibling</code> method returns the next sibling,
	 * <code>null</code> if the node has no right siblings.
	 *
	 * @return a <code>Node</code> value that is the next sibling, or
	 * <code>null</code> if there's none
	 */
	public Node<TValue> getNextSibling() {
		if (this.hasNextSibling()) {
			return this.getParent().child(this.getChildIndex() + 1);
		} else {
			return null;
		}
	}

	/**
	 * <code>hasPreviousSibling</code> method returns <code>true</code> if
	 * the node has left siblings, <code>false</code> otherwise.
	 *
	 * @return a <code>boolean</code> value, <code>true</code> if the
	 * node has left siblings, <code>false</code> if it is the
	 * leftmost child
	 */
	public boolean hasPreviousSibling() {
		if (this.getParent() != null) {
			int indexPreviousSibling = this.getChildIndex() - 1;
			return indexPreviousSibling >= 0;
		} else {
			return false;
		}
	}

	/**
	 * <code>previousSibling</code> method returns the previous sibling,
	 * <code>null</code> if the node has no left siblings.
	 *
	 * @return a <code>Node</code> value that is the previous sibling,
	 * or <code>null</code> if there's none
	 */
	public Node<TValue> getPreviousSibling() {
		if (this.hasPreviousSibling()) {
			return this.getParent().child(this.getChildIndex() - 1);
		} else {
			return null;
		}
	}

	/**
	 * <code>toString</code> method makes a string representation out
	 * of the node showing its key and value.
	 *
	 * @return a <code>String</code> value representing the node
	 */
	public String toString() {
		String keyString = this.key() != null ? this.key() : "";
		String valueString = this.value() != null ? this.value().toString() : "";
		return "node " + keyString + ": " + valueString;
	}

	/**
	 * <code>toSExpression</code> method returns the S-Expression
	 * representing the node; note that the value of the node is not
	 * represented.
	 *
	 * @return a <code>String</code> value, the node's S-Expression
	 */
	public String toSExpression() {
		StringBuffer str = new StringBuffer();
		str.append("(");
		str.append(this.key() != null ? this.key() : "node");
		if (this.getNumberOfChildren() > 0) {
			for (Iterator<Node<TValue> > it = children(); it.hasNext(); ) {
				Node<TValue> child = it.next();
				str.append(" ").append(child.toSExpression());
			}
		}
		str.append(")");
		return str.toString();
	}

}
