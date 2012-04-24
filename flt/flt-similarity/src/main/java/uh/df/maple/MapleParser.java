package uh.df.maple;

import eu.fox7.util.tree.Node;
import eu.fox7.util.tree.Tree;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import java.util.StringTokenizer;

import uh.df.combspec.CombinatorialSpecification;



/**
 * 
 * @author Dominique Fonteyn
 *
 */
public class MapleParser {

	private final static String DELIMETER = ",";
	final static String EPSILON = "Epsilon";
	final static String MAPLE_LINEBREAK = "\\    ";

	/**
	 * 
	 * @param mapleString
	 * @return
	 */
	public long parseToNumber(String mapleString) {
		long result = Long.parseLong(mapleString);
		return result;
	}
	
	/**
	 * 
	 * @param sample
	 * @return
	 */
	public String parseToXml(String sample, CombinatorialSpecification spec) {
		try {
			StringBuilder builder = new StringBuilder();			
			sample = sample.replaceAll(MapleCommandBuilder.PROD, "").replace('(', ' ').replace(')', ' ');
			sample = sample.replaceAll(MapleCommandBuilder.LEFTBRACE, "[").replaceAll(MapleCommandBuilder.RIGHTBRACE, "]");
			sample = sample.replace(MAPLE_LINEBREAK, "");
			
			// deal with abbreviations like %1, %2, ...
			if (hasAbbreviations(sample)) {
				List<String> abbreviatons = getAbbreviations(sample);
				for (String rule : abbreviatons) {
					String [] parts = rule.split(":");			
					sample = sample.substring(0, sample.indexOf(parts[0] + " :=") - 1);
					sample = sample.replace(parts[0], parts[1]);
				}				
			}

			// deal with Epsilon rules
			for (String nt : spec.getEpsilonSet())
				sample = sample.replace(nt, "Epsilon");
			sample = sample.replaceAll("Epsilon", "");	
			
			// transform to tree
			Tree tree = new Tree();
			Stack<Node> stack = new Stack<Node>();
			StringTokenizer tokenizer = new StringTokenizer(sample, DELIMETER);		
			String token = null;
			while (tokenizer.hasMoreTokens()) {
				token = tokenizer.nextToken().trim();
				if (isAtom(token)) {
					Node node = new Node(token);
					if (stack.size() > 0) 
						stack.peek().addChild(node);
					stack.push(node);
					if (tree.getRoot() == null) {
						tree.setRoot(node);
					}
				}
				else if (isOpeningBrace(token)) {
					// do nothing
				}
				else if (isClosingBrace(token)) {
					stack.pop();
				}			
			}		

			if (tree.getNumberOfNodes() > 0)
				treeToXml(tree.getRoot(), builder);

			return builder.toString();
		} catch (Exception e) {
			System.err.println("Error with parsing ");
			System.err.println(sample);
			System.err.println();
			e.printStackTrace();
			System.exit(0);

		}
		return null;
	}
	
	/**
	 * 
	 * @param sample
	 * @return
	 */
	public Set<String> parseToXmlFiles(String sample, CombinatorialSpecification spec) {
		try {
			StringBuilder builder = new StringBuilder();
			sample = sample.replace('[', ' ').replace(']', ' ');
			sample = sample.replaceAll(MapleCommandBuilder.PROD, "").replace('(', ' ').replace(')', ' ');
			sample = sample.replaceAll(MapleCommandBuilder.LEFTBRACE, "[").replaceAll(MapleCommandBuilder.RIGHTBRACE, "]");

			// deal with abbreviations like %1, %2, ...
			if (hasAbbreviations(sample)) {
				List<String> abbreviatons = getAbbreviations(sample);
				for (String rule : abbreviatons) {
					String [] parts = rule.split(":");		
					sample = sample.substring(0, sample.indexOf(parts[0] + " :=") - 1);
					sample = sample.replace(parts[0], parts[1]);
				}				
			}

			// deal with Epsilon rules
			for (String nt : spec.getEpsilonSet()) {
				sample = sample.replaceAll(nt, "Epsilon");
			}
			sample = sample.replaceAll("Epsilon", "");	
			
			// transform to tree
			Set<String> xmlfiles = new HashSet<String>();
			Tree tree = new Tree();
			Stack<Node> stack = new Stack<Node>();
			StringTokenizer tokenizer = new StringTokenizer(sample, DELIMETER);		
			String token = null;
			while (tokenizer.hasMoreTokens()) {
				token = tokenizer.nextToken().trim();
				if (isAtom(token)) {
					Node node = new Node(token);
					if (stack.size() > 0) 
						stack.peek().addChild(node);
					stack.push(node);
					if (tree.getRoot() == null)
						tree.setRoot(node);
				}
				else if (isOpeningBrace(token)) {
					// do nothing
				}
				else if (isClosingBrace(token)) {
					stack.pop();
					
					if (stack.size() == 0 && tree.getNumberOfNodes() > 0) {
						treeToXml(tree.getRoot(), builder);
						String xml = builder.toString();
						xmlfiles.add(xml);
						if (tokenizer.hasMoreTokens()) {
							tree = new Tree();
							builder = new StringBuilder();
						}
					}
				}				
			}					
			
			
			return xmlfiles;
		} catch (Exception e) {
			System.err.println("Error with parsing ");
			System.err.println(sample);
			System.err.println();
			e.printStackTrace();
			System.exit(0);

		}
		return null;
	}
	
	
	/**
	 * 
	 * @param sample
	 * @return
	 */
	protected boolean hasAbbreviations(String sample) {
		return sample.contains("%");
	}
	
	/**
	 * 
	 * @param sample
	 * @return
	 */
	protected List<String> getAbbreviations(String sample) {
		List<String> abbrevs = new ArrayList<String>();
		
		int i = 1;
		String abbreviation = "%" + i;
		List<String> marks = new ArrayList<String>();
		while (sample.contains(abbreviation)) {
			marks.add(abbreviation);
			i++;
			abbreviation = "%" + i;
		}
		
		int end = sample.length() - 1;
		for (i = marks.size() - 1; i >= 0; i--) {
			int start = sample.indexOf(marks.get(i) + " :=");
			String rule = sample.substring(start, end);
			end = start -1;			
			rule = rule.substring(rule.indexOf(":=") + 3);
			abbrevs.add(marks.get(i)  + ":" + rule);
		}
		
		return abbrevs;
	}
	
	/**
	 * 
	 * @param node
	 * @param builder
	 */
	protected void treeToXml(Node node, StringBuilder builder) {
		if (node.hasChildren()) {
			builder.append("<");
			builder.append(node.getKey());
			builder.append(">");
			
			for (Iterator<Node> it = node.children(); it.hasNext();)
				treeToXml(it.next(), builder);
			
			builder.append("</");
			builder.append(node.getKey());
			builder.append(">");
		}
		else {
			builder.append("<");
			builder.append(node.getKey());
			builder.append("/>");
		}
	}
	
	/**
	 * 
	 * @param token
	 * @return
	 */
	private boolean isAtom(String token) {
		if (isOpeningBrace(token) || isClosingBrace(token) || token.trim().length() == 0)
			return false;
		return true;
	}
	
	/**
	 * 
	 * @param token
	 * @return
	 */
	private boolean isOpeningBrace(String token) {
		return (token.equals("["));
	}
	
	/**
	 * 
	 * @param token
	 * @return
	 */
	private boolean isClosingBrace(String token) {
		return (token.equals("]"));
	}

}
