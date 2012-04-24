package uh.df.learning.evaluation;

import eu.fox7.flt.automata.factories.sparse.ProductNFAFactory;
import eu.fox7.flt.automata.factories.sparse.ThompsonBuilder;
import eu.fox7.flt.automata.factories.sparse.ThompsonFactory;
import eu.fox7.flt.automata.impl.sparse.SparseNFA;
import eu.fox7.flt.automata.measures.EmptynessTest;
import eu.fox7.flt.regex.UnknownOperatorException;
import eu.fox7.flt.treegrammar.XMLElementDefinition;
import eu.fox7.flt.treegrammar.XMLElementNotDefinedException;
import eu.fox7.flt.treegrammar.XMLGrammar;
import eu.fox7.util.tree.SExpressionParseException;
import eu.fox7.util.xml.acstring.AncestorChildrenExampleParser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * 
 * @author Dominique Fonteyn
 *
 */
public class FalsePositivesAndNegativesMeasure {

	protected int fp;
	protected int fn;
	
	/**
	 * Evaluates grammars on false positives and negatives
	 * @param grammar1
	 * @param grammar2
	 * @param sample
	 * @throws IOException
	 * @throws UnknownOperatorException
	 * @throws SExpressionParseException
	 * @throws XMLElementNotDefinedException
	 */
	public void evaluatePrecision(XMLGrammar grammar1, XMLGrammar grammar2, File sample) throws IOException, UnknownOperatorException, SExpressionParseException, XMLElementNotDefinedException {
		Map<String, Set<String>> correspondingTypesGrammar1 = new HashMap<String, Set<String>>();
		Map<String, Set<String>> correspondingTypesGrammar2 = new HashMap<String, Set<String>>();
		
		// run through paths
		BufferedReader br = new BufferedReader(new FileReader(sample));
		Set<String> paths = new HashSet<String>();
		String line = null;
		while ((line = br.readLine()) != null)
			paths.add(line);
		
		for (String path : paths) {
			String[] parts = path.split(AncestorChildrenExampleParser.DEFAULT_ANCESTOR_CHILDREN_SEP);
			String type1 = getType(grammar1, parts[0]);
			String type2 = getType(grammar2, parts[0]);

			if (!correspondingTypesGrammar1.containsKey(type1))
				correspondingTypesGrammar1.put(type1, new HashSet<String>());
			correspondingTypesGrammar1.get(type1).add(type2);

			if (!correspondingTypesGrammar2.containsKey(type2))
				correspondingTypesGrammar2.put(type2, new HashSet<String>());
			correspondingTypesGrammar2.get(type2).add(type1);
		}

		// evaluate
		fp = 0;
		for (String type : correspondingTypesGrammar1.keySet()) {
			if (correspondingTypesGrammar1.get(type).size() > 1) {
				if (!evaluate(correspondingTypesGrammar1.get(type), grammar2))
					fp++;
			}
		}
		

		fn = 0;
		for (String type : correspondingTypesGrammar2.keySet()) {
			if (correspondingTypesGrammar2.get(type).size() > 1) {
				if (!evaluate(correspondingTypesGrammar2.get(type), grammar1))
				fn++;
			}
		}
	}
	
	public int getFalsePositives() {
		return fp;
	}
	
	public int getFalseNegatives() {
		return fn;
	}
	
	protected static boolean evaluate(Set<String> types, XMLGrammar grammar) throws UnknownOperatorException, SExpressionParseException, XMLElementNotDefinedException {
		boolean ok = true;
		for (Iterator<String> it = types.iterator(); it.hasNext();) {
			String a = it.next();
			for (Iterator<String> sit = types.iterator(); sit.hasNext();) {
				String b = sit.next();
				if (!a.equals(b)) {
					ok &= isEqual(grammar.getElement(a).getContentModel().toString(), grammar.getElement(b).getContentModel().toString());
				}
			}
		}
		return ok;
	}

	/**
	 * 
	 * @param path
	 * @return
	 */
	protected static String followPath(String path) {
		return null;
	}

	/**
	 * 
	 * @param typeString
	 * @return
	 */
	protected static String getType(XMLGrammar grammar, String typeString) {
		String[] parts = typeString.substring(1).split(AncestorChildrenExampleParser.DEFAULT_ANCESTOR_SEP);
		String type = null;
		try {
			String contentmodel = null;
			XMLElementDefinition element = null;
			for (String str : parts) {
				if (type == null)
					type = grammar.getRootElement().getName();
				else {
					str = str.trim();
					element = grammar.getElement(type);
					contentmodel = element.getContentModelString();

					int from = contentmodel.indexOf(str + XMLGrammar.QNAME_TYPE_SEPARATOR);
					int to = contentmodel.indexOf(")", contentmodel.indexOf(str + XMLGrammar.QNAME_TYPE_SEPARATOR));
					type = contentmodel.substring(from, to);
				}
			}
		} catch (XMLElementNotDefinedException e) {
			e.printStackTrace();
		}
		
		return type;
	}

	/**
	 * 
	 * @param regex1
	 * @param regex2
	 * @return
	 * @throws UnknownOperatorException
	 * @throws SExpressionParseException
	 */
	protected static boolean isEqual(String regex1, String regex2) throws UnknownOperatorException,
			SExpressionParseException {
		// creata nfas
		ThompsonFactory factory = new ThompsonFactory();
		SparseNFA nfa1 = factory.create(regex1);
		SparseNFA nfa2 = factory.create(regex2);
		SparseNFA[] nfas = { nfa1, nfa2 };

		// alphabet
		Set<String> alphabet = new HashSet<String>();
		alphabet.addAll(nfa1.getSymbolValues());
		alphabet.addAll(nfa2.getSymbolValues());

		// intersection
		SparseNFA intersectionCompl = ThompsonBuilder.complement(ProductNFAFactory.intersection(nfas), alphabet);

		// union
		SparseNFA union = ThompsonBuilder.union(nfas);
		SparseNFA[] intersectionAndUnionNfas = { intersectionCompl, union };
		EmptynessTest emptyTest = new EmptynessTest();
		return emptyTest.test(ProductNFAFactory.intersection(intersectionAndUnionNfas));
	}
	
}
