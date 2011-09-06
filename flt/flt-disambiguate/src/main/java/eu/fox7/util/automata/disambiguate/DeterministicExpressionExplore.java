/**
 * 
 */
package eu.fox7.util.automata.disambiguate;

import java.util.HashSet;
import java.util.Set;

import eu.fox7.flt.automata.FeatureNotSupportedException;
import eu.fox7.flt.automata.NotDFAException;
import eu.fox7.flt.automata.converters.NFAMinimizer;
import eu.fox7.flt.automata.factories.sparse.Determinizer;
import eu.fox7.flt.automata.factories.sparse.EquivalenceCondition;
import eu.fox7.flt.automata.impl.sparse.SparseNFA;
import eu.fox7.flt.automata.measures.Simulator;
import eu.fox7.flt.regex.Glushkov;
import eu.fox7.flt.regex.Regex;
import eu.fox7.flt.regex.UnknownOperatorException;
import eu.fox7.flt.regex.generators.LanguageGenerator;
import eu.fox7.flt.regex.infer.rwr.NoOpportunityFoundException;
import eu.fox7.flt.regex.infer.rwr.RewriteEngine;
import eu.fox7.flt.regex.infer.rwr.Rewriter;
import eu.fox7.flt.regex.infer.rwr.impl.Automaton;
import eu.fox7.flt.regex.infer.rwr.impl.GraphAutomatonFactory;
import eu.fox7.util.tree.SExpressionParseException;
import eu.fox7.util.tree.Tree;


/**
 * @author woutergelade
 *
 */
public class DeterministicExpressionExplore implements DeterministicExpression {

	private boolean limitedSearch;
	private int searchDepth;
	private boolean randomSearch;
	private int randomThreshold;
	private DFAGeneratorEfficient generator;

	public DeterministicExpressionExplore(){
		limitedSearch = false;
		searchDepth = -1;
		randomSearch = false;
		randomThreshold = -1;
		generator = new DFAGeneratorEfficient();
		//generator.setAbsoluteUpperbound(absoluteUpperBound);
	}

	public DeterministicExpressionExplore(int searchDepth){
		limitedSearch = true;
		this.searchDepth = searchDepth;
		randomSearch = false;
		randomThreshold = -1;
		generator = new DFAGeneratorEfficient();
		//generator.setAbsoluteUpperbound(absoluteUpperBound);
	}

	public DeterministicExpressionExplore(int searchDepth, int randomThreshold){
		limitedSearch = true;
		this.searchDepth = searchDepth;
		randomSearch = true;
		this.randomThreshold = randomThreshold;
		generator = new DFAGeneratorEfficient(randomThreshold);
		generator.setAbsoluteUpperbound(randomThreshold);
	}
	
	public void setAbsoluteUpperbound(int bound){
		generator.setAbsoluteUpperbound(bound);
	}

	public Tree deterministicExpression(String regexStr) throws SExpressionParseException, UnknownOperatorException, FeatureNotSupportedException {
//		Glushkov g = new Glushkov();
		SparseNFA nfa = new SparseNFA(new LanguageGenerator(regexStr).getNFA());
		return deterministicExpression(nfa);
	}
	public Tree deterministicExpression(SparseNFA nfa) {
		nfa = Determinizer.dfa(nfa);
		new NFAMinimizer().minimize(nfa);
		if(!BKWTools.isUnambiguous(nfa))
			return null;
		else{
			eu.fox7.flt.regex.infer.rwr.impl.GraphAutomatonFactory factory = new eu.fox7.flt.regex.infer.rwr.impl.GraphAutomatonFactory();
			RewriteEngine rewriter = new Rewriter();
			nfa = DFAExpander.constructGlushkovAutomaton(nfa);
			
			Set<SparseNFA> NFAs = new HashSet<SparseNFA>();
			NFAs.add(nfa);

			int depth = 0;
			while(!NFAs.isEmpty() && (!limitedSearch || depth <= searchDepth)){
				for(SparseNFA dfa : NFAs){
					Automaton automaton = factory.create(dfa);
					try{
						String regexStr1 = rewriter.rewriteToRegex(automaton);						
						Regex re = new Regex(regexStr1);
						return re.getTree();
					}
					catch(NoOpportunityFoundException e){				   
					} 
					catch (SExpressionParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (UnknownOperatorException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				depth++;
				if(!limitedSearch || depth <= searchDepth){
					NFAs = generator.generateDisjointDFAs(nfa, nfa.getNumberOfStates() + depth);
					//System.out.println("Depth: " + depth + " size: " + NFAs.size());
				}				
			}
			return null;
		}
	}

	/**
	 * @return the limitedSearch
	 */
	public boolean isLimitedSearch() {
		return limitedSearch;
	}

	/**
	 * @param limitedSearch the limitedSearch to set
	 */
	public void setLimitedSearch(boolean limitedSearch) {
		this.limitedSearch = limitedSearch;
	}

	/**
	 * @return the searchDepth
	 */
	public int getSearchDepth() {
		return searchDepth;
	}

	public boolean isRandomSearch() {
		return randomSearch;
	}

	public void setRandomSearch(boolean randomSearch) {
		this.randomSearch = randomSearch;
	}

	public int getApproximateSetSize() {
		return randomThreshold;
	}

	public void setApproximateSetSize(int approximateSetSize) {
		this.randomThreshold = approximateSetSize;
	}

	public void setSearchDepth(int searchDepth) {
		this.searchDepth = searchDepth;
	}

}
