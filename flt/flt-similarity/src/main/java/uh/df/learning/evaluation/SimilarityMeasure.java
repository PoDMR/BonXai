package uh.df.learning.evaluation;

import gjb.flt.grammar.SyntaxErrorException;
import gjb.flt.treegrammar.XMLElementNotDefinedException;
import gjb.flt.treegrammar.XMLGrammar;
import gjb.util.Collections;

import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

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
public class SimilarityMeasure {
	
	protected String dirToMaple;
	protected double similarity;
	protected double intersection;
	protected double union;
	
	public SimilarityMeasure(String dirToMaple) {
		this.dirToMaple = dirToMaple;
	}
	
	public void computeSimilarity(XMLGrammar grammar1, XMLGrammar grammar2, int minimumsize, int maximumsize) throws XMLElementNotDefinedException, IOException, SyntaxErrorException, StreamNotOpenException {
		XMLGrammarConverter xgc = new XMLGrammarConverter();
		CombinatorialSpecification spec1 = xgc.getCombinatorialSpecification(grammar1);
		CombinatorialSpecification spec2 = xgc.getCombinatorialSpecification(grammar2);
		MapleStream stream = new MapleStream(dirToMaple);
		MapleParser parser = new MapleParser();
		
		// reset
		intersection = 0;
		union = 0;
		similarity = 0;
		
		// open stream
		stream.open();
		
		double[] counts1 = new double[maximumsize];
		double[] counts2 = new double[maximumsize];
		for (int i = minimumsize; i < maximumsize; i++) {
			System.out.printf("%d) ", i);
			stream.send(MapleCommandBuilder.getAllstructsCommand(spec1,i));
			Set<String> samples1 = parser.parseToXmlFiles(stream.receive(), spec1);
			
			stream.send(MapleCommandBuilder.getAllstructsCommand(spec2,i));
			Set<String> samples2 = parser.parseToXmlFiles(stream.receive(), spec2);

			counts1[i - minimumsize] = samples1.size();
			counts2[i - minimumsize] = samples2.size();

			Set<String> unionset = new HashSet<String>();
			for (Iterator<String> it = samples1.iterator(); it.hasNext();)
				unionset.add(it.next());
			for (Iterator<String> it = samples2.iterator(); it.hasNext();)
				unionset.add(it.next());
			Set<String> intersect = Collections.intersect(samples1, samples2);

			intersection += intersect.size();
			union += unionset.size();

			if (intersection == 0 && union == 0)
				similarity = 1;
			else
				similarity = intersection / union;


			double sum1 = 0;
			for(double d : counts1)
				sum1+=d;
			
			double sum2 = 0;
			for(double d : counts2)
				sum2+=d;
			
			
		}
		
		// close stream
		stream.close();
		
	}
	
	public double getIntersectionAmount() {
		return intersection;
	}
	
	public double getUnionAmount() {
		return union;
	}
	
	public double getSimilarity() {
		return similarity;
	}

}
