/**
 * 
 */
package gjb.util.automata.disambiguate;

import gjb.flt.automata.impl.sparse.SparseNFA;
import gjb.flt.automata.NotDFAException;
import gjb.util.tree.Node;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

/**
 * @author woutergelade
 * Class which maintains a tree of encodings of glushkovized DFAs, 
 * which allows to efficiently decide whether a given DFA
 * is isomorphic to one already present in the tree
 */
public class IsomorphismTree {
	private Node root;
	private String[] getAlphabet;
	private boolean bound;
	private int sizeBound;
	private int count;
	
	public IsomorphismTree(int sizeBound){
		root = new Node();
		root.setValue(new HashMap<String,Node>());
		this.sizeBound = sizeBound;
		bound = true;
		count = 0;
	}
	
	public IsomorphismTree(){
		root = new Node();
		root.setValue(new HashMap<String,Node>());
		count = 0;
		bound = false;
	}
	
	/**
	 * Adds given NFA to the prefix tree. Returns true if it was not present yet, false otherwise.
	 * @throws NotDFAException 
	 */
	@SuppressWarnings("unchecked")
	public boolean doesNotContain(SparseNFA dfa) throws NotDFAException{
		if(bound && count > sizeBound)
			return false;
		if(((HashMap<String,String>) root.value()).keySet().isEmpty())
			getAlphabet = (String[]) dfa.getAlphabet().keySet().toArray(new String[dfa.getAlphabet().keySet().size()]);
		
		String encoding = encode(dfa);
		
		if(encoding.length() == 0)
			return false;
		
		Node currentNode = root;
		for(int position = 0; position < encoding.length(); position++){
			HashMap<String, Node> currentNodeMap = (HashMap<String,Node>) currentNode.getValue();
			if(!currentNodeMap.keySet().contains("" + encoding.charAt(position))){
				return true;
			}
			currentNode = currentNodeMap.get("" + encoding.charAt(position));
		}
		return false;
		
	}
	
	/**
	 * Adds given NFA to the prefix tree. Returns true if it was not present yet, false otherwise.
	 * @throws NotDFAException 
	 */
	public boolean addNFA(SparseNFA dfa) throws NotDFAException{
		if(bound && count > sizeBound)
			return false;
		if(((HashMap<String,String>) root.value()).keySet().isEmpty())
			getAlphabet = (String[]) dfa.getAlphabet().keySet().toArray(new String[dfa.getAlphabet().keySet().size()]);
		
		String encoding = encode(dfa);
		
		if(encoding.length() == 0)
			return false;
		
		Node currentNode = root;
		boolean addedLast = false;
		for(int position = 0; position < encoding.length(); position++){
			HashMap<String, Node> currentNodeMap = (HashMap<String,Node>) currentNode.getValue();
			if(!currentNodeMap.keySet().contains("" + encoding.charAt(position))){
				Node newNode = new Node();
				newNode.setValue(new HashMap<String,Node>());
				currentNode.addChild(newNode);
				currentNodeMap.put("" + encoding.charAt(position), newNode);
				if(position == encoding.length() - 1)
					addedLast = true;
			}
			currentNode = currentNodeMap.get("" + encoding.charAt(position));
		}
		if(addedLast)
			count++;
		return addedLast;
		
	}

	/**
	 * @param dfa
	 * @param getAlphabet2
	 * @return
	 * @throws NotDFAException 
	 */
	private String encode(SparseNFA dfa) throws NotDFAException {
		String result = "";
		
		Map<String,Integer> symbolCount = new HashMap<String,Integer>();
		for(int i = 0; i < getAlphabet.length; i++)
			symbolCount.put(getAlphabet[i], new Integer(0));
		
		Map<String,String> stateMap = new HashMap<String,String>();
		stateMap.put(dfa.getStateValue(dfa.getInitialState()), "q_I");
		
		Stack<String> toDo = new Stack<String>();
		toDo.push(dfa.getStateValue(dfa.getInitialState()));
		
		Set<String> added = new HashSet<String>();
		added.add(dfa.getStateValue(dfa.getInitialState()));
		
		while(!toDo.isEmpty()){
			String state = toDo.pop();
			for(int symbol = 0; symbol < getAlphabet.length; symbol++){
				String toState = dfa.getStateValue(dfa.getNextState(dfa.getAlphabet().get(getAlphabet[symbol]), dfa.getState(state)));
				if(toState != null){
					if(!stateMap.containsKey(toState)){
						int count = symbolCount.get(getAlphabet[symbol]).intValue();
						stateMap.put(toState, getAlphabet[symbol] + "_" + count);
						symbolCount.put(getAlphabet[symbol], new Integer(count + 1));
					}
					result += stateMap.get(state) + stateMap.get(toState);
					if(!added.contains(toState)){
						added.add(toState);
						toDo.add(toState);
					}
				}
			}
		}
		return result;
	}

	/**
	 * @return
	 */
	public boolean satisfied() {
		return bound && count >= sizeBound;
	}
}
