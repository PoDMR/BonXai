package uh.df.tools;

import eu.fox7.flt.regex.infer.rwr.NoOpportunityFoundException;
import eu.fox7.flt.regex.infer.rwr.RewriteEngine;
import eu.fox7.flt.schema.infer.ixsd.ContextMap;
import eu.fox7.flt.schema.infer.ixsd.XsdLearner;
import eu.fox7.flt.treegrammar.XMLGrammar;
import eu.fox7.util.sampling.SampleException;
import eu.fox7.util.xml.acstring.ExampleParsingException;

import java.io.File;
import java.io.IOException;

import uh.df.learning.AlternativeContextMapConverter;
import uh.df.learning.AlternativeRewriter;

/**
 * 
 * @author Dominique Fonteyn
 *
 */
public class SOLearner {

	/**
	 * Learns a single occurrence XMLGrammar from sample with the specified contextsize.
	 * @param sample the sample to learn the grammar from
	 * @param contextSize the contextsize for the grammar
	 * @return single occurrence XMLGrammar
	 * @throws NoOpportunityFoundException
	 * @throws ExampleParsingException
	 * @throws IOException
	 * @throws SampleException
	 */
	public XMLGrammar learnGrammar(File sample, int contextSize) throws ExampleParsingException, IOException,
			SampleException, NoOpportunityFoundException {
		XsdLearner learner = new XsdLearner();
		ContextMap contextMap = learn(sample, learner, contextSize);
		AlternativeRewriter rewriter = new AlternativeRewriter();
		XMLGrammar grammar = rewrite(contextMap, rewriter);
		return grammar;
	}

	protected ContextMap learn(File sample, XsdLearner learner, int contextSize) throws ExampleParsingException,
			IOException, SampleException {
		ContextMap contextMap = learner.computeContextMap(sample, contextSize);
		return contextMap;
	}

	protected XMLGrammar rewrite(ContextMap contextMap, RewriteEngine rewriter) throws NoOpportunityFoundException {
		XMLGrammar xmlGrammar = AlternativeContextMapConverter.convertToGrammar(contextMap, rewriter);
		return xmlGrammar;
	}

	protected static String grammarToString(XMLGrammar grammar) {
		StringBuilder str = new StringBuilder();

		// root
		str.append(XMLGrammar.ROOT_DEF);
		str.append(grammar.getRootElement().getQName());
		str.append(XMLGrammar.QNAME_TYPE_SEPARATOR);
		str.append(grammar.getRootElement().getType());
		str.append("\n");

		// rest of grammar
		str.append(grammar.toString());

		return str.toString();
	}

}
