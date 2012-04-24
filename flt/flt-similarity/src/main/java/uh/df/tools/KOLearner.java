package uh.df.tools;

import eu.fox7.flt.regex.infer.rwr.NoOpportunityFoundException;
import eu.fox7.flt.regex.infer.rwr.Rewriter;
import eu.fox7.flt.schema.infer.ixsd.ContextMap;
import eu.fox7.flt.schema.infer.ixsd.ContextMapConverter;
import eu.fox7.flt.treegrammar.XMLGrammar;
import gjb.util.sampling.SampleException;
import gjb.util.xml.acstring.ExampleParsingException;

import java.io.File;
import java.io.IOException;
import java.io.Writer;

import uh.df.learning.KoXsdLearner;

/**
 * 
 * @author Dominique Fonteyn
 *
 */
public class KOLearner {

	/**
	 * Learns a k occurrence XMLGrammar from sample with the specified contextsize.
	 * @param sample the sample to learn the grammar from
	 * @param contextSize the contextsize for the grammar
	 * @return k occurrence XMLGrammar
	 * @throws NoOpportunityFoundException
	 * @throws ExampleParsingException
	 * @throws IOException
	 * @throws SampleException
	 */
	public XMLGrammar learnGrammar(File sample, int contextSize) throws NoOpportunityFoundException, ExampleParsingException, IOException, SampleException {
		KoXsdLearner learner = new KoXsdLearner();
		Rewriter rewriter = new Rewriter();
		ContextMap contextMap = learn(sample, learner, contextSize);
		XMLGrammar grammar = rewrite(contextMap, rewriter);
		return grammar;
	}

	protected ContextMap learn(File sample, KoXsdLearner learner, int contextSize) throws ExampleParsingException, IOException, SampleException {
		ContextMap contextMap = learner.computeContextMap(sample, contextSize);
		return contextMap;
	}

	protected XMLGrammar rewrite(ContextMap contextMap, Rewriter rewriter) throws NoOpportunityFoundException {
		XMLGrammar xmlGrammar = ContextMapConverter.convertToGrammar(contextMap, rewriter);
		return xmlGrammar;
	}
	
	/**
	 * Outputs grammar to writer 
	 * @param grammar
	 * @param writer
	 * @throws IOException
	 */
	public void writeGrammar(XMLGrammar grammar, Writer writer) throws IOException {
		writer.write(XMLGrammar.ROOT_DEF);
		writer.write(grammar.getRootElement().getQName());
		writer.write(XMLGrammar.QNAME_TYPE_SEPARATOR);
		writer.write(grammar.getRootElement().getType());
		writer.write("\n");

		// write rest of grammar
		writer.write(grammar.toString());
		writer.flush();
	}	

}
