package gjb.util.tree;

import java.beans.SimpleBeanInfo;
import java.beans.PropertyDescriptor;
import java.beans.IntrospectionException;

/**
 * Class <code>NodeBeanInfo</code> facilitates mainly JXPath query
 * access to a <code>Tree</code>.  Note that this class is not
 * dependent on JXPath.  Together with <code>TreeBeanInfo</code> this
 * class provides introspection information for bean access.
 *
 * @author <a href="mailto:gjb@luc.ac.be">Geert Jan Bex</a>
 * @version 1.0
 */
public class NodeBeanInfo extends SimpleBeanInfo {

	/**
	 * <code>getPropertyDescriptors</code> method makes the
	 * information on the properties of objects of the
	 * <code>Node</code> class.
	 *
	 * @return a <code>PropertyDescriptor[]</code> value with the
	 * description of the node's properties
	 */
	public PropertyDescriptor[] getPropertyDescriptors() {
		/* key and value have both setters and getters; parent,
		 * children, nrOfChildren and tree are read-only so only a
		 * getter is specified;  node and children are synonymous
         */
		try {
			PropertyDescriptor key = new PropertyDescriptor("key",
			                                                Node.class,
			                                                "key",
			                                                "setKey");
			PropertyDescriptor value = new PropertyDescriptor("value",
															  Node.class,
															  "value",
															  "setValue");
			PropertyDescriptor parent = new PropertyDescriptor("parent",
															   Node.class,
															   "getParent",
															   null);
			PropertyDescriptor children = new PropertyDescriptor("children",
																 Node.class,
																 "getChildren",
																 null);
			PropertyDescriptor node = new PropertyDescriptor("node",
															 Node.class,
															 "getChildren",
															 null);
			PropertyDescriptor nrOfChildren = new PropertyDescriptor("nrOfChildren",
																	 Node.class,
																	 "getNumberOfChildren",
																	 null);
			PropertyDescriptor tree = new PropertyDescriptor("tree",
															 Node.class,
															 "getTree",
															 null);
			PropertyDescriptor[] pd = {key, value, parent, children, node,
									   nrOfChildren, tree};
			return pd;
		} catch (IntrospectionException e) {
			e.printStackTrace();
		}
		return new PropertyDescriptor[0];
	}

}
