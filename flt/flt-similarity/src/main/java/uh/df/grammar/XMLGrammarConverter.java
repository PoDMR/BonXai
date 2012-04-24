package uh.df.grammar;

import eu.fox7.flt.grammar.SyntaxErrorException;
import eu.fox7.flt.regex.Regex;
import eu.fox7.flt.treegrammar.XMLElementDefinition;
import eu.fox7.flt.treegrammar.XMLElementNotDefinedException;
import eu.fox7.flt.treegrammar.XMLGrammar;
import gjb.util.tree.Node;
import gjb.util.tree.Tree;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections15.map.HashedMap;

import uh.df.combspec.CombinatorialSpecification;
import uh.df.maple.MapleCommandBuilder;

/**
 * 
 * @author Dominique Fonteyn
 *
 */
public class XMLGrammarConverter {

	private static int index = 1;

	private Map<String, String> nonterminals;

	private Set<String> epsilonSet;

	private Set<String> atoms = new HashSet<String>();

	/**
	 * 
	 * 
	 */
	public XMLGrammarConverter() {
		nonterminals = new HashedMap<String, String>();
		atoms = new HashSet<String>();
		epsilonSet = new HashSet<String>();
	}

	/**
	 * 
	 * @param grammar
	 * @return
	 * @throws XMLElementNotDefinedException
	 * @throws SyntaxErrorException
	 * @throws IOException
	 */
	public CombinatorialSpecification getCombinatorialSpecification(XMLGrammar grammar)
			throws XMLElementNotDefinedException, IOException, SyntaxErrorException {
		CombinatorialSpecification spec = null;
		nonterminals = new HashedMap<String, String>();
		epsilonSet = new HashSet<String>();
		atoms = new HashSet<String>();

		// preprocessing step: add all symbols
		String roottype = null;
		for (Iterator<String> it = grammar.getElementNameIterator(); it.hasNext();) {
			String element = it.next();
			String[] parts = XMLGrammar.decomposeElementName(element.toUpperCase());
			String type = parts[0] + parts[1];
			nonterminals.put(type, "T" + type);

			if (grammar.getRootElement().getName().equals(element))
				roottype = type;
		}

		StringBuilder builder = new StringBuilder();
		builder.append("S -> " + nonterminals.get(roottype) + "\n");

		for (Iterator<String> it = grammar.getElementNameIterator(); it.hasNext();) {
			String element = it.next();
			String[] parts = XMLGrammar.decomposeElementName(element.toUpperCase());
			String type = parts[0] + parts[1];
			// voeg T -> RT regel toe
			builder.append(nonterminals.get(type));
			builder.append(" -> ");
			builder.append(MapleCommandBuilder.PROD + "(" + parts[0].toLowerCase() + ", Prod("
					+ MapleCommandBuilder.LEFTBRACE + ", " + MapleCommandBuilder.PROD + "(R");
			builder.append(nonterminals.get(type));
			builder.append(", " + MapleCommandBuilder.RIGHTBRACE + ")))\n");

			atoms.add(parts[0].toLowerCase());

			// get expression
			Regex regex = grammar.getElement(element).getContentModel();
			Tree tree = regex.getTree();

			// parse
			parse("R" + nonterminals.get(type), tree.getRoot(), builder);
		}

		spec = new CombinatorialSpecification(new StringReader(builder.toString()));
		spec.setAtoms(atoms);
		spec.setEpsilonSet(epsilonSet);

		return spec;
	}

	/**
	 * 
	 * @param lh
	 * @param node
	 * @param builder
	 */
	protected void parse(String lh, Node node, StringBuilder builder) {
		String key = node.getKey();
		if (key.equals(".")) {
			handleConcatenation(lh, node, builder);
		} else if (key.equals("*")) {
			handleZeroOrMore(lh, node, builder);
		} else if (key.equals("+")) {
			handleOneOrMore(lh, node, builder);
		} else if (key.equals("?")) {
			handleZeroOrOne(lh, node, builder);
		} else if (key.equals("|")) {
			handleUnion(lh, node, builder);
		} else if (key.equals("EPSILON")) {
			handleEpsilon(lh, node, builder);
		} else {
			handleAtom(lh, node, builder);
		}
	}

	protected void handleConcatenation(String lh, Node node, StringBuilder builder) {
		if (node.getNumberOfChildren() == 1) {
			parse(lh, node.getChild(0), builder);
		} else if (node.getNumberOfChildren() == 2) {
			String symbol1 = newLeftHandSymbol();
			String symbol2 = newLeftHandSymbol();
			builder.append(lh + " -> " + "Prod(" + symbol1 + ", " + symbol2 + ")" + "\n");
			parse(symbol1, node.getChild(0), builder);
			parse(symbol2, node.getChild(1), builder);
		} else if (node.getNumberOfChildren() > 2) {
			String symbol1 = newLeftHandSymbol();
			String symbol2 = newLeftHandSymbol();
			builder.append(lh + " -> " + "Prod(" + symbol1 + ", " + symbol2 + ")" + "\n");
			parse(symbol1, node.getChild(0), builder);
			Node tnode = new Node();
			tnode.setKey(".");
			List<Node> nodes = new ArrayList<Node>();
			for (int i = 1; i < node.getChildren().size(); i++)
				nodes.add(node.getChild(i));
			tnode.addChildren(nodes);
			parse(symbol2, tnode, builder);
		} else {
			throw new RuntimeException("concatenation operated heeft geen kinderen: " + node.getNumberOfChildren());
		}
	}

	protected void handleUnion(String lh, Node node, StringBuilder builder) {
		if (node.getNumberOfChildren() == 1) {
			parse(lh, node.getChild(0), builder);
		} else if (node.getNumberOfChildren() == 2) {
			String symbol1 = newLeftHandSymbol();
			String symbol2 = newLeftHandSymbol();
			builder.append(lh + " -> " + "Union(" + symbol1 + ", " + symbol2 + ")" + "\n");
			parse(symbol1, node.getChild(0), builder);
			parse(symbol2, node.getChild(1), builder);
		} else if (node.getNumberOfChildren() > 2) {
			String symbol1 = newLeftHandSymbol();
			String symbol2 = newLeftHandSymbol();
			builder.append(lh + " -> " + "Union(" + symbol1 + ", " + symbol2 + ")" + "\n");
			parse(symbol1, node.getChild(0), builder);
			Node tnode = new Node();
			tnode.setKey("|");
			List<Node> nodes = new ArrayList<Node>();
			for (int i = 1; i < node.getChildren().size(); i++)
				nodes.add(node.getChild(i));
			tnode.addChildren(nodes);
			parse(symbol2, tnode, builder);
		} else {
			throw new RuntimeException("union operator heeft geen kinderen: " + node.getNumberOfChildren());
		}
	}

	protected void handleOneOrMore(String lh, Node node, StringBuilder builder) {
		String symbol = newLeftHandSymbol();
		builder.append(lh + " -> " + "Prod(" + lh + ", " + symbol + ")" + "\n");
		builder.append(lh + " -> " + symbol + "\n");
		parse(symbol, node.getChild(0), builder);
	}

	protected void handleZeroOrMore(String lh, Node node, StringBuilder builder) {
		String symbol = newLeftHandSymbol();
		builder.append(lh + " -> " + "Prod(" + lh + ", " + symbol + ")" + "\n");
		builder.append(lh + " -> " + "Epsilon" + "\n");
		epsilonSet.add(lh);
		parse(symbol, node.getChild(0), builder);
	}

	protected void handleZeroOrOne(String lh, Node node, StringBuilder builder) {
		String symbol = newLeftHandSymbol();
		builder.append(lh + " -> " + symbol + "\n");
		builder.append(lh + " -> " + "Epsilon" + "\n");
		epsilonSet.add(lh);
		parse(symbol, node.getChild(0), builder);
	}

	protected void handleEpsilon(String lh, Node node, StringBuilder builder) {
		builder.append(lh);
		builder.append(" -> ");
		builder.append("Epsilon");
		builder.append("\n");
		epsilonSet.add(lh);
	}

	protected void handleAtom(String lh, Node node, StringBuilder builder) {
		String[] parts = XMLGrammar.decomposeElementName(node.getKey().toUpperCase());
		builder.append(lh);
		builder.append(" -> ");
		builder.append(nonterminals.get(parts[0] + parts[1]));
		builder.append("\n");
	}

	
	protected String newLeftHandSymbol() {
		return "R" + index++;
	}

	protected String contentStateName(XMLElementDefinition element) {
		String name = element.getQName();
		String type = element.getType();
		return name + XMLGrammar.QNAME_TYPE_SEPARATOR + type;
	}

}
