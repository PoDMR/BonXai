package uh.df.learning;

import eu.fox7.flt.automata.measures.MutualExclusionDistance;
import eu.fox7.flt.regex.infer.rwr.NoOpportunityFoundException;
import eu.fox7.flt.regex.infer.rwr.Rewriter;
import eu.fox7.flt.schema.infer.ixsd.ContentEquivalenceRelation;
import eu.fox7.flt.schema.infer.ixsd.ContextMap;
import eu.fox7.flt.schema.infer.ixsd.ContextMapConverter;
import eu.fox7.flt.schema.infer.ixsd.DirectEquivalenceRelation;
import eu.fox7.flt.schema.infer.ixsd.Merger;
import eu.fox7.flt.schema.infer.ixsd.XsdLearner;
import eu.fox7.flt.treeautomata.factories.SupportContextAutomatonFactory;
import eu.fox7.flt.treeautomata.impl.ContextAutomaton;
import eu.fox7.flt.treegrammar.XMLGrammar;
import eu.fox7.flt.treegrammar.io.GrammarWriter;
import eu.fox7.util.sampling.SampleException;
import eu.fox7.util.xml.acstring.AncestorChildrenDocumentIterator;
import eu.fox7.util.xml.acstring.AncestorChildrenExampleParser;
import eu.fox7.util.xml.acstring.ExampleParsingException;
import eu.fox7.util.xml.acstring.ParseResult;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Iterator;

/**
 * 
 * @author Dominique Fonteyn
 *
 */
public class KoXsdLearner extends XsdLearner {

	@Override
	/**
	 * 
	 */
	public ContextAutomaton learn(File file, int contextSize) throws IOException, ExampleParsingException,
			SampleException {
		ContextMap contextMap = computeContextMap(file, contextSize);
		ContextAutomaton contextFA = ContextMapConverter.convertToContextFA(contextMap);
		SupportContextAutomatonFactory factory = new SupportContextAutomatonFactory(
				new KoreSupportContentAutomatonFactory());
		Merger friendlyMerger = new Merger(factory, contextFA);
		ContentEquivalenceRelation relation = new DirectEquivalenceRelation(0.5, new MutualExclusionDistance());
		friendlyMerger.setContentEquivalenceRelation(relation);
		friendlyMerger.merge();
		return contextFA;
	}

	@Override
	/**
	 * 
	 */
	public ContextMap computeContextMap(File file, int contextSize) throws IOException, ExampleParsingException,
			SampleException {
		ContextMap contextMap = new ContextMap(new KoreSupportContentAutomatonFactory(), contextSize);
		Iterator<String> docIt = new AncestorChildrenDocumentIterator(file);
		while (docIt.hasNext()) {
			String doc = docIt.next();
			BufferedReader reader = new BufferedReader(new StringReader(doc));
			AncestorChildrenExampleParser parser = new AncestorChildrenExampleParser();
			parser.setStrippedFirst(true);
			String line = null;
			while ((line = reader.readLine()) != null) {
				if (line.trim().length() == 0)
					break;
				ParseResult result = parser.parse(line);
				contextMap.add(result.getContext(), result.getContent());
			}
			reader.close();
		}
		return contextMap;
	}

	/**
	 * 
	 * @param map
	 * @return
	 * @throws NoOpportunityFoundException
	 */
	public XMLGrammar grammar(ContextMap map) throws NoOpportunityFoundException {
		XMLGrammar grammar = null;
		Rewriter rewriter = new Rewriter();
		grammar = ContextMapConverter.convertToGrammar(map, rewriter);
		return grammar;
	}

	/**
	 * 
	 * @param grammar
	 * @return
	 */
	public String printGrammar(XMLGrammar grammar) {
		String output;
		try {
			GrammarWriter grammarWriter = new AlternativeRegularTreeGrammarWriter();
			StringWriter writer = new StringWriter();
			grammarWriter.write(grammar, writer);
			output = writer.toString();
		} catch (IOException e) {
			output = null;
		}
		return output;
	}

}
