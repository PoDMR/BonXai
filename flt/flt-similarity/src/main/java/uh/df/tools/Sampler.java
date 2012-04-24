package uh.df.tools;

import gjb.flt.grammar.SyntaxErrorException;
import gjb.flt.treegrammar.XMLElementNotDefinedException;
import gjb.flt.treegrammar.XMLGrammar;
import gjb.util.xml.ConfigurationException;
import gjb.util.xml.acstring.XMLtoAncestorChildrenConverter;

import java.io.IOException;
import java.io.StringReader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import org.dom4j.DocumentException;

import uh.df.combspec.CombinatorialSpecification;
import uh.df.grammar.XMLGrammarConverter;
import uh.df.maple.MapleCommandBuilder;
import uh.df.maple.MapleParser;
import uh.df.maple.MapleStream;
import uh.df.maple.exceptions.StreamNotOpenException;


/**
 * 
 * @author Dominique Fonteyn
 *
 */
public class Sampler {

	protected String dirToMaple = "/home/domi/maple14/bin/maple";

	public Sampler(String dirToMaple) {
		this.dirToMaple = dirToMaple;
	}

	/**
	 * Samples just one sample for the specified samplesize.
	 * 
	 * @param grammar
	 * @param sampleSize
	 * @return
	 * @throws StreamNotOpenException
	 * @throws IOException
	 * @throws SyntaxErrorException 
	 * @throws XMLElementNotDefinedException 
	 */
	public String getSample(XMLGrammar grammar, int sampleSize) throws StreamNotOpenException, IOException, XMLElementNotDefinedException, SyntaxErrorException {
		CombinatorialSpecification specification = (new XMLGrammarConverter()).getCombinatorialSpecification(grammar);
		MapleStream mstream = new MapleStream(dirToMaple);
		MapleParser mparser = new MapleParser();

		// send count command
		mstream.send(MapleCommandBuilder.getCountCommand(specification, sampleSize));
		long possibleSamples = Long.parseLong(mstream.receive());
		if (possibleSamples == 0)
			return null;

		// send sample command
		mstream.send(MapleCommandBuilder.getDrawCommand(specification, sampleSize));
		String sample = mparser.parseToXml(mstream.receive(), specification);

		return sample;
	}

	/**
	 * Samples multiple samples varying from minimumSize to maximumSize.
	 * 
	 * @param grammar
	 * @param minimumSize
	 *            the minimum size of the samples
	 * @param maximumSize
	 *            the maximum size of the samples
	 * @param samplesPerSize
	 *            amount of samples to be generated per samplesize
	 * @return list of generated samples
	 * @throws StreamNotOpenException
	 * @throws IOException
	 * @throws SyntaxErrorException 
	 * @throws XMLElementNotDefinedException 
	 */
	public List<String> getSamples(XMLGrammar grammar, int minimumSize, int maximumSize, int samplesPerSize)
			throws StreamNotOpenException, IOException, XMLElementNotDefinedException, SyntaxErrorException {
		CombinatorialSpecification specification = (new XMLGrammarConverter()).getCombinatorialSpecification(grammar);
		MapleStream mstream = new MapleStream(dirToMaple);
		MapleParser mparser = new MapleParser();

		List<String> samples = new ArrayList<String>();
		for (int i = minimumSize; i <= maximumSize; i++) {
			// send count command
			mstream.send(MapleCommandBuilder.getCountCommand(specification, i));
			long possibleSamples = Long.parseLong(mstream.receive());
			if (possibleSamples == 0)
				continue;

			// send sample command
			for (int j = 0; j < samplesPerSize; j++) {
				mstream.send(MapleCommandBuilder.getDrawCommand(specification, i));
				String sample = mparser.parseToXml(mstream.receive(), specification);
				samples.add(sample);
			}
		}
		return samples;
	}

	/**
	 * Extracts all paths from the XML samples
	 * @param writer writer to output the paths
	 * @param samples a list of XML samples as strings
	 * @throws ConfigurationException
	 * @throws DocumentException
	 * @throws IOException
	 */
	public void outputSamplesToAncestorChildrenPaths(Writer writer, List<String> samples)
			throws ConfigurationException, DocumentException, IOException {
		XMLtoAncestorChildrenConverter sa = new XMLtoAncestorChildrenConverter(writer);
		for (String s : samples) {
			sa.parse(new StringReader(s));
		}
	}
}
