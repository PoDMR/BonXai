package gjb.util.tree;

import java.util.HashMap;
import java.util.Map;
import java.util.List;

/**
 * Class <code>NodeOrderTreeComparator</code> is an extension of the
 * class <code>DefaultTreeComparator</code> that allows a specified
 * ordering of the nodes instead of the lexicographic order used by
 * the latter.
 *
 * @author <a href="mailto:gjb@luc.ac.be">Geert Jan Bex</a>
 * @version 1.0
 */
public class NodeOrderTreeComparator extends DefaultTreeComparator {

	protected Map<String,Integer> order = new HashMap<String,Integer>();

    private NodeOrderTreeComparator() {
		super();
	}

	/**
	 * Creates a new <code>NodeOrderTreeComparator</code> instance
	 * that will use the ordering specified in the provided
	 * <code>List</code> to compare the nodes' keys.
	 *
	 * @param list a <code>List</code> value that specifies the
	 * ordering of the nodes' keys
	 */
	public NodeOrderTreeComparator(List<String> list) {
	    this();
		int counter = 0;
		for (String key : list) {
			order.put(key, counter++);
		}
	}

	
	/**
	 * <code>compare</code> method overrides the default lexicographic
	 * order in <code>DefaultTreeComparator</code>.
	 *
	 * @param str1 a <code>String</code> value
	 * @param str2 a <code>String</code> value
	 * @return an <code>int</code> value
	 */
	protected int compare(String str1, String str2) {
		if (order.containsKey(str1) && !order.containsKey(str2)) {
			return -1;
		} else if (!order.containsKey(str1) && order.containsKey(str2)) {
			return 1;
		} else if (!order.containsKey(str1) && !order.containsKey(str2)) {
			return 0;
		} else {
			return order.get(str1).compareTo(order.get(str2));
		}
	}

}
