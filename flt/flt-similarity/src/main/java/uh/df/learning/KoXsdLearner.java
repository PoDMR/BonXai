package uh.df.learning;

import gjb.flt.automata.measures.MutualExclusionDistance;
import gjb.flt.regex.infer.rwr.NoOpportunityFoundException;
import gjb.flt.regex.infer.rwr.Rewriter;
import gjb.flt.schema.infer.ixsd.ContentEquivalenceRelation;
import gjb.flt.schema.infer.ixsd.ContextMap;
import gjb.flt.schema.infer.ixsd.ContextMapConverter;
import gjb.flt.schema.infer.ixsd.DirectEquivalenceRelation;
import gjb.flt.schema.infer.ixsd.Merger;
import gjb.flt.schema.infer.ixsd.XsdLearner;
import gjb.flt.treeautomata.factories.SupportContextAutomatonFactory;
import gjb.flt.treeautomata.impl.ContextAutomaton;
import gjb.flt.treegrammar.XMLGrammar;
import gjb.flt.treegrammar.io.GrammarWriter;
import gjb.util.sampling.SampleException;
import gjb.util.xml.acstring.AncestorChildrenDocumentIterator;
import gjb.util.xml.acstring.AncestorChildrenExampleParser;
import gjb.util.xml.acstring.ExampleParsingException;
import gjb.util.xml.acstring.ParseResult;

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
