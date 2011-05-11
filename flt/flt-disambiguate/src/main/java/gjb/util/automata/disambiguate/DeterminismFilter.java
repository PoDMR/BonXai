/**
 * 
 */
package gjb.util.automata.disambiguate;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import gjb.flt.regex.infer.rwr.impl.Automaton;
import gjb.flt.regex.infer.rwr.impl.GraphAutomatonFactory;
import gjb.flt.regex.infer.rwr.impl.InvalidLabelException;
import gjb.flt.regex.infer.rwr.impl.Opportunity;
import gjb.flt.regex.infer.rwr.impl.OpportunityFilter;



/**
 * @author woutergelade
 *
 */
public class DeterminismFilter implements OpportunityFilter{
	public boolean isPassed(Opportunity opportunity, Automaton automaton) {
		gjb.flt.regex.infer.rwr.impl.GraphAutomatonFactory factory = new gjb.flt.regex.infer.rwr.impl.GraphAutomatonFactory();
		Automaton newAutomaton = factory.expand(automaton);
		return isDeterministic(newAutomaton);
	}
	
	public boolean isDeterministic(Automaton automaton){
		Map<String,String> symbols = new HashMap<String, String>();
		for(int i = 0; i < automaton.getNumberOfStates() - 1; i++){
			symbols.put(automaton.getLabel(i), getSymbol(automaton.getLabel(i)));
		}
		
		for(int i = 0; i < automaton.getNumberOfStates() - 1; i++){
			Set<String> labels = new HashSet<String>();
			int total = 0;
			for(int j = 0; j < automaton.getNumberOfStates() - 1; j++){
				try {
					if(automaton.hasTransition(automaton.getLabel(i), automaton.getLabel(j))){
						labels.add(symbols.get(automaton.getLabel(j)));
						total++;
					}
				} catch (InvalidLabelException e) {
					System.out.println("I'm messing up labels");
					e.printStackTrace();
				}
			}
			if(total > labels.size())
				return false;
		}
		return true;
	}

	/**
	 * @param label
	 * @return
	 */
	private String getSymbol(String label) {
		if(!label.contains("_"))
			return label;
		else
			return label.substring(1,label.lastIndexOf("_"));
	}
	
}
