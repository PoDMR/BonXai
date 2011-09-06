package eu.fox7.util.tree;

import eu.fox7.util.tree.io.PrefixStringTreeWriter;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.List;

/**
 * Class <code>DefaultTreeComparator</code> implements a default tree
 * compare that is based on the prefix string representation of the
 * two trees.  The main purpose is to sort lists of trees.
 *
 * @author <a href="mailto:eu.fox7@luc.ac.be">Geert Jan Bex</a>
 * @version 1.0
 */
public class DefaultTreeComparator implements Comparator<Tree> {

	static Map<Node,List<String>> cache = new HashMap<Node,List<String>>();

	public int compare(Tree tree1, Tree tree2) {
		return compare(tree1.getRoot(), tree2.getRoot());
	}

    protected int compare(Node node1, Node node2) {
        if (!cache.containsKey(node1)) {
			cache.put(node1,
					  PrefixStringTreeWriter.prefixListRepresentation(node1));
		}
		if (!cache.containsKey(node2)) {
			cache.put(node2,
					  PrefixStringTreeWriter.prefixListRepresentation(node2));
		}
		Iterator<String> it1 = cache.get(node1).iterator();
		Iterator<String> it2 = cache.get(node2).iterator();
		while (it1.hasNext() && it2.hasNext()) {
			String item1 = it1.next();
			String item2 = it2.next();
			if (item1 == null && item2 != null) {
				return -1;
			} else if (item1 != null && item2 == null) {
				return 1;
			} else if (item1 != null && item2 != null) {
				int cmp = compare(item1, item2);
				if (cmp != 0) {
					return cmp;
				}
			}
		}
		if (it1.hasNext()) {
			return 1;
		}
		if (it2.hasNext()) {
			return -1;
		}
		return 0;
    }

	protected int compare(String str1, String str2) {
		return str1.compareTo(str2);
	}

}
