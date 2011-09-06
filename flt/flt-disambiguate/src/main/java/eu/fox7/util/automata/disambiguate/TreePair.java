/**
 * 
 */
package eu.fox7.util.automata.disambiguate;

import eu.fox7.flt.automata.NFA;
import eu.fox7.util.tree.Tree;

/**
 * @author woutergelade
 *
 */
public class TreePair implements Comparable<TreePair> {

	private NFA nfa;
	private Tree tree;
	private Double languageSize;
	
	public TreePair(NFA nfa, Tree tree, double languageSize){
		this.setNfa(nfa);
		this.setTree(tree);
		if(languageSize < 0)
			System.out.println("Please give me a positive language size");
		this.languageSize = languageSize;
	}
	
	public int compareTo(TreePair pair) {
		return -languageSize.compareTo(pair.languageSize);
	}
	
	public String toString() {
		return "NFA:\n" + nfa + "Language Size: " + languageSize.doubleValue();
	}

	/**
	 * @param nfa the nfa to set
	 */
	public void setNfa(NFA nfa) {
		this.nfa = nfa;
	}

	/**
	 * @return the nfa
	 */
	public NFA getNfa() {
		return nfa;
	}

	/**
	 * @param tree the tree to set
	 */
	public void setTree(Tree tree) {
		this.tree = tree;
	}

	/**
	 * @return the tree
	 */
	public Tree getTree() {
		return tree;
	}

}
