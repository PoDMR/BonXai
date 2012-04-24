package uh.df.grammar;

import eu.fox7.flt.treegrammar.XMLGrammar;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * 
 * @author Dominique Fonteyn
 *
 */
public class XMLGrammarAnalysis {
	
	private XMLGrammar grammar;
	private Set<String> types;
	private Set<String> symbols;
	
	public XMLGrammarAnalysis(XMLGrammar grammar) {
		initialize(grammar);
	}	
	
	/**
	 * Analyses grammar
	 */
	protected void analyse() {
		Iterator<String> it = grammar.getElementNameIterator();
		String elementName = null;
		String type = null;
		String symbol = null;
		while (it.hasNext()) {
			elementName = it.next();
			String [] parts = XMLGrammar.decomposeElementName(elementName);
			symbol = parts[0];
			type = parts.length == 2 ? parts[1] : parts[0];
			symbols.add(symbol);
			types.add(type);
		}
	}
	
	protected void initialize(XMLGrammar grammar) {
		this.grammar = grammar;
		types = new HashSet<String>();
		symbols = new HashSet<String>();
		analyse();
	}
	
	public void setGrammar(XMLGrammar grammar) {
		initialize(grammar);
	}
	
	public Set<String> getTypes() {
		return this.types;
	}
	
	public Set<String> getSymbols() {
		return this.symbols;
	}
	
	public XMLGrammar getGrammar() {
		return this.grammar;
	}
	
	public int numberOfTypes() {
		return this.types.size();
	}
	
	public int numberOfSymbols() {
		return this.symbols.size();
	}	
	
	public int minSize() {
		return numberOfTypes();
	}
	
	public int maxSize() {
		return maxSize(5);
	}
	
	public int maxSize(int factor) {
		return (factor * numberOfSymbols() * numberOfTypes()); 
	}

}
