/**
 * 
 */
package eu.fox7.util.automata.disambiguate;

import eu.fox7.flt.automata.FeatureNotSupportedException;
import eu.fox7.flt.regex.UnknownOperatorException;
import eu.fox7.flt.regex.infer.rwr.impl.Automaton;
import eu.fox7.flt.regex.infer.rwr.impl.GraphAutomaton;
import eu.fox7.flt.regex.infer.rwr.impl.GraphAutomatonFactory;
import eu.fox7.flt.regex.infer.rwr.measures.LanguageSizeMeasure;
import eu.fox7.util.tree.SExpressionParseException;

import java.util.HashMap;
import java.util.Map;

/**
 * @author woutergelade
 *
 */
public class GraphAutomatonMeasures {
	
	public double languageSize(String regexStr,int maxLength) throws SExpressionParseException, UnknownOperatorException, FeatureNotSupportedException{
		eu.fox7.flt.regex.infer.rwr.impl.GraphAutomatonFactory factory = new eu.fox7.flt.regex.infer.rwr.impl.GraphAutomatonFactory();
		Automaton automaton = factory.create(regexStr);
		LanguageSizeMeasure measure = new LanguageSizeMeasure();
		measure.setMaxLength(maxLength);
		
		return 1 - Math.abs(measure.compute(automaton));
	}
	
	public double languageSize(Automaton automaton,int maxLength) throws SExpressionParseException, UnknownOperatorException, FeatureNotSupportedException{
		LanguageSizeMeasure measure = new LanguageSizeMeasure();
		measure.setMaxLength(maxLength);
		
		return 1 - Math.abs(measure.compute(automaton));
	}
	
	public double languageSize(String regexStr) throws SExpressionParseException, UnknownOperatorException, FeatureNotSupportedException{
		eu.fox7.flt.regex.infer.rwr.impl.GraphAutomatonFactory factory = new eu.fox7.flt.regex.infer.rwr.impl.GraphAutomatonFactory();
		Automaton automaton = factory.create(regexStr);
		LanguageSizeMeasure measure = new LanguageSizeMeasure();
		
		return 1 - Math.abs(measure.compute(automaton));
	}
	
	public double languageSize(Automaton automaton) throws SExpressionParseException, UnknownOperatorException, FeatureNotSupportedException{
		LanguageSizeMeasure measure = new LanguageSizeMeasure();
		
		return 1 - Math.abs(measure.compute(automaton));
	}
	
	public double kappaValue(String regexStr) throws SExpressionParseException, UnknownOperatorException, FeatureNotSupportedException{
		eu.fox7.flt.regex.infer.rwr.impl.GraphAutomatonFactory factory = new eu.fox7.flt.regex.infer.rwr.impl.GraphAutomatonFactory();
		Automaton automaton = factory.create(regexStr);
		return kappaValue(automaton);
	}
	
	public double kappaValue(Automaton automaton){
		Map<String,Integer> getAlphabet = getAlphabetCount(automaton);
		
		return (double) (automaton.getNumberOfStates()-1) / getAlphabet.size();
	}
	
	public int kValue(String regexStr) throws SExpressionParseException, UnknownOperatorException, FeatureNotSupportedException{
		eu.fox7.flt.regex.infer.rwr.impl.GraphAutomatonFactory factory = new eu.fox7.flt.regex.infer.rwr.impl.GraphAutomatonFactory();
		Automaton automaton = factory.create(regexStr);
		return kValue(automaton);
	}
	
	public int kValue(Automaton automaton){
		Map<String,Integer> getAlphabet = getAlphabetCount(automaton);
		
		int k = 0;
		for(String symbol : getAlphabet.keySet())
			k = Math.max(k, getAlphabet.get(symbol).intValue());
		
		return k;
	}
	
	public Map<String,Integer> getAlphabetCount(Automaton automaton){
		Map<String,Integer> getAlphabet = new HashMap<String, Integer>();
		
		for(int state = 0; state < automaton.getNumberOfStates(); state++){
			String symbol = automaton.getLabel(state);
			if(symbol != null && symbol != GraphAutomaton.SOURCE_LABEL && symbol != GraphAutomaton.SINK_LABEL){
				symbol = extractSymbol(symbol);
				if(!getAlphabet.keySet().contains(symbol))
					getAlphabet.put(symbol, new Integer(1));
				else
					getAlphabet.put(symbol, new Integer(getAlphabet.get(symbol).intValue() + 1));
			}
		}
		return getAlphabet;
	}
	
	public String extractSymbol(String label){
		return label.substring(1, label.indexOf('_'));
	}
}
