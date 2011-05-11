package gjb.util.tree;

import java.beans.SimpleBeanInfo;
import java.beans.PropertyDescriptor;
import java.beans.IntrospectionException;

/**
 * Class <code>TreeBeanInfo</code> facilitates mainly JXPath query
 * access to a <code>Tree</code>.  Note that this class is not
 * dependent on JXPath.  Together with <code>NodeBeanInfo</code> this
 * class provides introspection information for bean access.
 *
 * @author <a href="mailto:gjb@luc.ac.be">Geert Jan Bex</a>
 * @version 1.0
 */
public class TreeBeanInfo extends SimpleBeanInfo {

	/**
	 * <code>getPropertyDescriptors</code> method makes the
	 * information on the properties of objects of the
	 * <code>Tree</code> class available.
	 *
	 * @return a <code>PropertyDescriptor[]</code> value with the
	 * description of the tree's properties
	 */
	public PropertyDescriptor[] getPropertyDescriptors() {
		/* root is read-only so only a getter is specified. */
		try {
			PropertyDescriptor root = new PropertyDescriptor("root",
															 Tree.class,
															 "root",
															 null);
			PropertyDescriptor[] pd = {root};
			return pd;
		} catch (IntrospectionException e) {
			e.printStackTrace();
		}
		return new PropertyDescriptor[0];
	}

}
