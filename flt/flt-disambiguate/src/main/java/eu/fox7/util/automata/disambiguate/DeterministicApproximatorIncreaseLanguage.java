/**
 * 
 */
package eu.fox7.util.automata.disambiguate;

import eu.fox7.flt.automata.FeatureNotSupportedException;
import eu.fox7.flt.automata.converters.NFAMinimizer;
import eu.fox7.flt.automata.factories.sparse.Determinizer;
import eu.fox7.flt.automata.impl.sparse.SparseNFA;
import eu.fox7.flt.automata.measures.LanguageSize;
import eu.fox7.flt.regex.Glushkov;
import eu.fox7.flt.regex.Regex;
import eu.fox7.flt.regex.UnknownOperatorException;
import eu.fox7.flt.regex.generators.LanguageGenerator;
import eu.fox7.flt.regex.infer.rwr.BacktrackingRepairer;
import eu.fox7.flt.regex.infer.rwr.NoOpportunityFoundException;
import eu.fox7.flt.regex.infer.rwr.RewriteEngine;
import eu.fox7.flt.regex.infer.rwr.Rewriter;
import eu.fox7.flt.regex.infer.rwr.impl.Automaton;
import eu.fox7.flt.regex.infer.rwr.impl.GraphAutomatonFactory;
import eu.fox7.flt.regex.infer.rwr.measures.LanguageSizeMeasure;
import eu.fox7.util.RandomSelector;
import eu.fox7.util.tree.SExpressionParseException;
import eu.fox7.util.tree.Tree;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * @author woutergelade
 *
 */
public class DeterministicApproximatorIncreaseLanguage implements
DeterministicApproximator {

	private DeterministicExpression deterministic;
	private RewriteEngine repairer;
	private LanguageSize measure;
	private int maxTransitionsAdded;
	private int numTries;
	private boolean singleSolution;

	public void setMeasure(LanguageSize measure) {
		this.measure = measure;
	}

	public DeterministicApproximatorIncreaseLanguage() {
		this.maxTransitionsAdded = 1;
		this.numTries = 1;
		this.singleSolution = false;
		deterministic = new DeterministicExpressionExplore();
		repairer = new BacktrackingRepairer(new LanguageSizeMeasure(),1);
		((BacktrackingRepairer)repairer).setFilter(new DeterminismFilter());
	}

	public DeterministicApproximatorIncreaseLanguage(int maxTransitions, int numTries, DeterministicExpression deterministic) {
		this.deterministic = deterministic;
		this.maxTransitionsAdded = maxTransitions;
		this.numTries = numTries;
		this.singleSolution = false;
		repairer = new BacktrackingRepairer(new LanguageSizeMeasure(),1);
		((BacktrackingRepairer)repairer).setFilter(new DeterminismFilter());
	}

	public DeterministicApproximatorIncreaseLanguage(int maxTransitions, int numTries, int exploreDepth, int exploreRandomThreshold, int backtrackingDepth) {
		this.maxTransitionsAdded = maxTransitions;
		this.numTries = numTries;
		deterministic = new DeterministicExpressionExplore(exploreDepth, exploreRandomThreshold);
		this.singleSolution = false;
		repairer = new BacktrackingRepairer(new LanguageSizeMeasure(),backtrackingDepth);
		((BacktrackingRepairer)repairer).setFilter(new DeterminismFilter());
	}
	
	public DeterministicApproximatorIncreaseLanguage(int maxTransitions, int numTries, int exploreDepth, int exploreRandomThreshold) {
	 	this(maxTransitions,numTries,exploreDepth,exploreRandomThreshold,1);
	}

	public void setSingleSolution(boolean singleSolution){
		this.singleSolution = singleSolution;
	}

	/* (non-Javadoc)
	 * @see eu.fox7.util.automata.disambiguate.DeterministicApproximator#deterministicApproximation(java.lang.String)
	 */
	public Set<Tree> deterministicApproximation(String regexStr)
	throws NoOpportunityFoundException, SExpressionParseException,
	UnknownOperatorException, FeatureNotSupportedException {
		Set<Tree> result = new HashSet<Tree>();
		Regex re = new Regex(regexStr);
		measure = new LanguageSize();

		Glushkov g = new Glushkov();
		
//		SparseNFA minimalDFA = SparseNFA.dfa(g.nfa(regexStr)).minimize();
		SparseNFA minimalDFA = new SparseNFA(new LanguageGenerator(regexStr).getNFA());
		new NFAMinimizer().minimize(minimalDFA);

		Set<ExtendOpportunity> opportunities = computeAllOpportunities(minimalDFA);
		DeterministicApproximatorShrink shrink = new DeterministicApproximatorShrink();
		shrink.setTestIsomorphism(false);

		for(int i = 1; i <= maxTransitionsAdded; i++){
			Set<SparseNFA> newNFAs = generateExtendedDFAs(i, minimalDFA, opportunities, true);
			newNFAs = determinize(newNFAs);
			
			Set<SparseNFA> deterministicLanguages = new HashSet<SparseNFA>();
			Set<SparseNFA> nonDeterministicLanguages = new HashSet<SparseNFA>();
			shrink.splitNFAs(newNFAs,deterministicLanguages,nonDeterministicLanguages);

			result.addAll(shrink.convertToExpressions(newNFAs, deterministicLanguages, nonDeterministicLanguages, null));
		}

		if(result.isEmpty())
			throw new NoOpportunityFoundException("Didn't find a superapproximation", null);
		else
			return result;
	}

	/**
	 * @param possibleDFAs
	 * @return
	 */
	private Set<SparseNFA> determinize(Set<SparseNFA> NFAs) {
		Set<SparseNFA> DFAs = new HashSet<SparseNFA>();
		for(SparseNFA nfa : NFAs)
			DFAs.add(Determinizer.dfa(nfa));
		return DFAs;
	}

	/**
	 * @param possibleDFAs
	 * @return
	 */
	protected LinkedList<SparseNFA> orderDFAs(SparseNFA originalDFA, Set<SparseNFA> DFAs, LanguageSize measure) {
		eu.fox7.flt.regex.infer.rwr.impl.GraphAutomatonFactory factory = new eu.fox7.flt.regex.infer.rwr.impl.GraphAutomatonFactory();
		SortedSet<TreePair> sortedSet = new TreeSet<TreePair>();

		SparseNFA minOriginalDFA = new SparseNFA(originalDFA);
		new NFAMinimizer().minimize(minOriginalDFA);
		originalDFA = DFAExpander.constructGlushkovAutomaton(minOriginalDFA);

		for(SparseNFA dfa : DFAs) {
			SparseNFA minDFA = new SparseNFA(dfa);
			new NFAMinimizer().minimize(minDFA);
			SparseNFA glushkovDFA = DFAExpander.constructGlushkovAutomaton(minDFA);
			Double langSize = new Double(1 + measure.compute(originalDFA,glushkovDFA));
			sortedSet.add(new TreePair(dfa, null, langSize));
		}

		LinkedList<SparseNFA> result = new LinkedList<SparseNFA>();
		while(!sortedSet.isEmpty()){
			result.addLast((SparseNFA) sortedSet.first().getNfa());
			sortedSet.remove(sortedSet.first());
		}

		return result;
	}

	protected Set<SparseNFA> generateExtendedDFAs(int setSize, SparseNFA minimalDFA, Set<ExtendOpportunity> opportunities,boolean onlyDeterministic) {
		Set<SparseNFA> result = new HashSet<SparseNFA>();
		RandomSelector<ExtendOpportunity> selector = new RandomSelector<ExtendOpportunity>();
		int counter = 0;
		if(setSize == 1 && !opportunities.isEmpty()){
			Set<ExtendOpportunity> opportunitiesCopy = new HashSet<ExtendOpportunity>(opportunities);
			while(!opportunitiesCopy.isEmpty() && counter < numTries){
				Set<ExtendOpportunity> extension = new HashSet<ExtendOpportunity>();
				ExtendOpportunity opportunity = selector.selectOneFrom(opportunitiesCopy);
				opportunitiesCopy.remove(opportunity);
				extension.add(opportunity);
				tryExtension(minimalDFA, onlyDeterministic, result, extension);
				counter++;
			}
		}
		else if (setSize <= opportunities.size()){
			while(counter < numTries){
				Set<ExtendOpportunity> extension = selector.selectSubsetFrom(opportunities, setSize);
				tryExtension(minimalDFA, onlyDeterministic, result, extension);
				counter++;
			}
		}

		return result;
	}

	private void tryExtension(SparseNFA minimalDFA, boolean onlyDeterministic,
			Set<SparseNFA> result, Set<ExtendOpportunity> extension) {
		SparseNFA newNFA = new SparseNFA(minimalDFA);
		applyExtensions(newNFA,extension);
		newNFA = Determinizer.dfa(newNFA);
		new NFAMinimizer().minimize(newNFA);
		if(!onlyDeterministic || BKWTools.isUnambiguous(newNFA))
			result.add(newNFA);
	}

	/**
	 * @param newNFA
	 * @param extension
	 */
	private void applyExtensions(SparseNFA newNFA, Set<ExtendOpportunity> extensions) {
		for(ExtendOpportunity opportunity : extensions){
			if(opportunity.concernsFinal()){
				newNFA.addFinalState(opportunity.getState());
			}
			else {
				newNFA.addTransition(opportunity.getSymbol(), opportunity.getFromState(), opportunity.getToState());
			}
		}

	}

	/**
	 * @param nfa
	 * @return
	 */
	protected Set<ExtendOpportunity> computeAllOpportunities(SparseNFA minimalDFA) {
		Set<ExtendOpportunity> opportunities = new HashSet<ExtendOpportunity>();

		computeFinalStateOpportunities(minimalDFA, opportunities);

		for(String fromState : minimalDFA.getStateValues()){
			for(String symbol : minimalDFA.getAlphabet().keySet()){
				for(String toState : minimalDFA.getStateValues()){
					if(!minimalDFA.hasTransition(symbol, fromState, toState))
						opportunities.add(new ExtendOpportunity(fromState,toState,symbol));
				}
			}
		}

		return opportunities;
	}

	private void computeFinalStateOpportunities(SparseNFA minimalDFA,
			Set<ExtendOpportunity> opportunities) {
		for(String state : minimalDFA.getStateValues()){
			if(!minimalDFA.isFinalState(state))
				opportunities.add(new ExtendOpportunity(state));
		}
	}

	protected Set<ExtendOpportunity> computeDeterminismPreservingOpportunities(SparseNFA minimalDFA) {
		Set<ExtendOpportunity> opportunities = new HashSet<ExtendOpportunity>();

		computeFinalStateOpportunities(minimalDFA, opportunities);
		for(String fromState : minimalDFA.getStateValues()){
			for(String symbol : minimalDFA.getAlphabet().keySet()){
				if(minimalDFA.getNextStates(minimalDFA.getAlphabet().get(symbol), minimalDFA.getState(fromState)).isEmpty()){
					for(String toState : minimalDFA.getStateValues()){
						opportunities.add(new ExtendOpportunity(fromState,toState,symbol));
					}
				}
			}
		}


		return opportunities;
	}




}
