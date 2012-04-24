package uh.df.grammar;

import eu.fox7.flt.automata.FeatureNotSupportedException;
import eu.fox7.flt.automata.NotDFAException;
import eu.fox7.flt.automata.factories.sparse.Determinizer;
import eu.fox7.flt.automata.factories.sparse.GlushkovFactory;
import eu.fox7.flt.automata.impl.sparse.State;
import eu.fox7.flt.automata.impl.sparse.StateDFA;
import eu.fox7.flt.automata.impl.sparse.StateNFA;
import eu.fox7.flt.automata.impl.sparse.Symbol;
import eu.fox7.flt.automata.impl.sparse.Transition;
import eu.fox7.flt.regex.Regex;
import eu.fox7.flt.regex.UnknownOperatorException;
import eu.fox7.flt.treegrammar.XMLElementDefinition;
import eu.fox7.flt.treegrammar.XMLElementNotDefinedException;
import eu.fox7.flt.treegrammar.XMLGrammar;
import gjb.util.tree.SExpressionParseException;
import gjb.util.xml.ConfigurationException;
import gjb.util.xml.acstring.AncestorChildrenExampleParser;
import gjb.util.xml.acstring.XMLtoAncestorChildrenConverter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections15.map.HashedMap;

/**
 * 
 * @author Dominique Fonteyn
 * 
 */
public class CoverageMeasurer {
	protected XMLGrammar grammar;
	protected Map<XMLElementDefinition, Double> coverage;
	protected Map<XMLElementDefinition, Map<Transition, Integer>> transitions;
	protected Map<XMLElementDefinition, List<String[]>> samples;
	protected Map<XMLElementDefinition, StateNFA> automata;
	protected Set<String> uniqueLinesSet;
	protected List<String> uniqueLinesList;
	protected double coverageAmount;

	/**
	 * Constructor. Initializes hashedmaps, analyses XMLGrammar. *
	 * 
	 * @param grammar
	 */
	public CoverageMeasurer(XMLGrammar grammar) {
		this.grammar = grammar;
		coverage = new HashedMap<XMLElementDefinition, Double>();
		transitions = new HashedMap<XMLElementDefinition, Map<Transition, Integer>>();
		samples = new HashedMap<XMLElementDefinition, List<String[]>>();
		automata = new HashedMap<XMLElementDefinition, StateNFA>();
		uniqueLinesSet = new HashSet<String>();
		uniqueLinesList = new ArrayList<String>();

		coverageAmount = 0.0;
		analyse();
	}

	/**
	 * Computes coverage by computing the average coverage of all
	 * coverage-values.
	 * 
	 * @return coverage of grammar as decimal value between 0 and 1.0
	 */
	public double getCoverage() {
		double sum = 0.0;
		for (double value : coverage.values())
			sum += value;
		coverageAmount = (sum / coverage.size());
		return coverageAmount;
	}

	/**
	 * Returns if grammar is fully covered. The grammar is fully covered if each
	 * separate type is fully covered by its specific sample.
	 * 
	 * @return true if grammar is covered, false otherwise
	 */
	public boolean isCovered() {
		return (coverageAmount == 1.0);
	}

	/**
	 * Computes coverage with sample.
	 * 
	 * @param sample
	 *            a string representation of an XML file
	 */
	public void computeCoverage(String sample) {
		// create temporary file
		File file = new File("tempsample.xml");
		FileWriter writer;
		try {
			writer = new FileWriter(file);
			writer.write(sample);
			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// parse temporary file
		File pfile = new File("tempparsed.txt");
		try {
			XMLtoAncestorChildrenConverter xml2ac = new XMLtoAncestorChildrenConverter(new FileWriter(pfile));
			xml2ac.parse("tempsample.xml");
		} catch (ConfigurationException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// read parsed
		parse(pfile);

		// delete temporary file
		file.delete();
		pfile.delete();
	}

	/**
	 * Computes coverage with this sample.
	 * 
	 * @param sample
	 *            a file containing AncestorChildren paths.
	 */
	public void computeCoverageOnSampleFile(File sample) {
		parse(sample);
	}

	/**
	 * Parses each line in the file.
	 * 
	 * @param file
	 */
	protected void parse(File file) {
		try {
			BufferedReader reader = new BufferedReader(new FileReader(file));
			String line = null;
			while ((line = reader.readLine()) != null)
				parse(line);
			reader.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Retrieves coverages for each element.
	 * 
	 * @return Map with coverage per XMLElementDefinition
	 */
	public Map<XMLElementDefinition, Double> getCoverages() {
		return coverage;
	}

	/**
	 * Retrieves coverage for a specific element.
	 * 
	 * @param element
	 *            the element for which to retrieve the coverage.
	 * @return double coverage for the specific element.
	 */
	public double getCoverage(XMLElementDefinition element) {
		return coverage.get(element);
	}

	public List<String> getMinimalCoverageRules() {
		return uniqueLinesList;
	}

	/**
	 * Parses the path.
	 * 
	 * @param path
	 *            an ancestorchildren path
	 */
	protected void parse(String path) {
		if (!uniqueLinesSet.contains(path)) {
			uniqueLinesList.add(path);
			uniqueLinesSet.add(path);
		}

		String[] parts = path.split(AncestorChildrenExampleParser.DEFAULT_ANCESTOR_CHILDREN_SEP);
		String type = getType(parts[0]);
		String sample = parts[1];

		// find element
		XMLElementDefinition element = null;
		try {
			element = grammar.getElement(type);
		} catch (XMLElementNotDefinedException e) {
			e.printStackTrace();
		}

		// no need to compute coverage if already covered
		if (coverage.get(element) == 1.0)
			return;

		// prepare sample
		String[] sampleArray = sample.trim().split(AncestorChildrenExampleParser.DEFAULT_CHILDREN_SEP);

		// process sample
		StateNFA automaton = automata.get(element);
		State fromState = automaton.getState(automaton.getInitialStateValue());
		if (sampleArray.length == 1 && sampleArray[0].trim().length() == 0) {
			if (automaton.isFinalState(fromState)) {
				Transition transition = new Transition(Symbol.getEpsilon(), fromState, fromState);
				transitions.get(element).put(transition, transitions.get(element).get(transition) + 1);
			}
		} else {
			for (int i = 0; i < sampleArray.length; i++) {
				// proceed as normal
				Symbol symbol = Symbol.create(sampleArray[i].trim());

				State toState = null;
				try {
					toState = ((StateDFA) automaton).getNextState(symbol, fromState);
				} catch (NotDFAException e) {
					e.printStackTrace();
				}
				Transition transition = new Transition(symbol, fromState, toState);
				transitions.get(element).put(transition, transitions.get(element).get(transition) + 1);
				fromState = toState;
			}
		}

		// compute coverage amount for this element
		double d_coverage = 0.0;
		Map<Transition, Integer> coverages = transitions.get(element);
		for (Transition transition : coverages.keySet()) {
			if (coverages.get(transition) > 0)
				d_coverage += 1.0;
		}
		coverage.put(element, (d_coverage / coverages.size()));

	}

	/**
	 * Retrieves the typename for an AncestorChildrenBases type name
	 * 
	 * @param typeString
	 *            ancestorchildren based type
	 * @return name of the type
	 */
	protected String getType(String typeString) {
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
	 * Analyses each type *
	 */
	protected void analyse() {
		for (Iterator<String> it = grammar.getElementNameIterator(); it.hasNext();) {
			try {
				// get element
				String ename = it.next();
				XMLElementDefinition element = grammar.getElement(ename);

				// put initial coverage
				coverage.put(element, 0.0);

				// get contentmodel
				Regex regex = element.getContentModel();

				// put automaton
				String regexString = regex.toString();
				for (String alphabetSymbol : regex.getAlphabet()) {
					String[] _parts = XMLGrammar.decomposeElementName(alphabetSymbol);
					regexString = regexString.replace(alphabetSymbol, _parts[0].toLowerCase());
				}

				StateNFA automaton = Determinizer.dfa(new GlushkovFactory().create(regexString));
				automata.put(element, automaton);

				transitions.put(element, new HashedMap<Transition, Integer>());
				for (Transition transition : automaton.getTransitionMap().getTransitions())
					transitions.get(element).put(transition, 0);
				if (automaton.isFinalState(automaton.getInitialState()))
					transitions.get(element).put(
							new Transition(Symbol.getEpsilon(), automaton.getInitialState(), automaton
									.getInitialState()), 0);

			} catch (XMLElementNotDefinedException e) {
				e.printStackTrace();
			} catch (UnknownOperatorException e) {
				e.printStackTrace();
			} catch (FeatureNotSupportedException e) {
				e.printStackTrace();
			} catch (SExpressionParseException e) {
				e.printStackTrace();
			}

		}
	}

	@Override
	/**
	 * String representation of the coverages.
	 */
	public String toString() {
		StringBuilder str = new StringBuilder();
		for (XMLElementDefinition element : coverage.keySet()) {
			str.append(element.getName() + ": " + coverage.get(element) + "\n");
		}
		return str.toString();
	}

}
