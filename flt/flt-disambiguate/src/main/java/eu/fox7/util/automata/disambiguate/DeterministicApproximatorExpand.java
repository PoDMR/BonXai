/**
 * 
 */
package eu.fox7.util.automata.disambiguate;

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
import eu.fox7.flt.regex.converters.Sorter;
import eu.fox7.flt.regex.generators.LanguageGenerator;
import eu.fox7.flt.regex.infer.rwr.BacktrackingRepairer;
import eu.fox7.flt.regex.infer.rwr.FixedOrderRepairer;
import eu.fox7.flt.regex.infer.rwr.NoOpportunityFoundException;
import eu.fox7.flt.regex.infer.rwr.RewriteEngine;
import eu.fox7.flt.regex.infer.rwr.impl.GraphAutomatonFactory;
import eu.fox7.flt.regex.infer.rwr.measures.LanguageSizeMeasure;
import eu.fox7.util.tree.SExpressionParseException;
import eu.fox7.util.tree.Tree;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * @author woutergelade
 *
 */
public class DeterministicApproximatorExpand implements
DeterministicApproximator {

	private RewriteEngine rewriter;
	private DFAGeneratorEfficient generator;
	private LanguageSizeMeasure measure;

	private int searchDepth;

	private int absoluteUpperBound;
	private int setSize;
	private int nrExpansions;

	private boolean doExpansions;

	private Set<SparseNFA> previousSet;

	private boolean randomSearch;
	private int randomThreshold;

	public DeterministicApproximatorExpand(int searchDepth, int randomThreshold, int backtrackingDepth){
		rewriter = new BacktrackingRepairer(new LanguageSizeMeasure(),backtrackingDepth);
		((BacktrackingRepairer)rewriter).setFilter(new DeterminismFilter());
		randomSearch = true;
		this.searchDepth = searchDepth;
		this.randomThreshold = randomThreshold;
		generator = new DFAGeneratorEfficient(randomThreshold);
	}

	public DeterministicApproximatorExpand(int searchDepth, int setSize, int nrExpansions, int backtrackingDepth){
		rewriter = new BacktrackingRepairer(new LanguageSizeMeasure(),backtrackingDepth);
		((BacktrackingRepairer)rewriter).setFilter(new DeterminismFilter());
		randomSearch = false;
		this.searchDepth = searchDepth;
		this.setSize = setSize;
		this.nrExpansions = nrExpansions;
		this.absoluteUpperBound = setSize * nrExpansions;
		generator = new DFAGeneratorEfficient(absoluteUpperBound);
		generator.setAbsoluteUpperbound(absoluteUpperBound);
	}

	/*public void setRewriter(RewriteEngine rewriter){
		this.rewriter = rewriter;
	}*/



	/* (non-Javadoc)
	 * @see eu.fox7.util.automata.disambiguate.DeterministicApproximator#deterministicApproximation(eu.fox7.flt.automata.impl.sparse.SparseNFA)
	 */
	public Set<Tree> deterministicApproximation(String regexStr) throws SExpressionParseException, UnknownOperatorException, FeatureNotSupportedException, NoOpportunityFoundException {
//		Glushkov g = new Glushkov();
		SparseNFA nfa = new SparseNFA(new LanguageGenerator(regexStr).getNFA());
		nfa = Determinizer.dfa(nfa);
		new NFAMinimizer().minimize(nfa);
		nfa = DFAExpander.constructGlushkovAutomaton(nfa);

		Regex re = new Regex(regexStr);
		measure = new LanguageSizeMeasure();
		measure.setMaxLength((re.getTotalOccurrencesOfSymbol() * 2) + 1);

		Set<Tree> result = new HashSet<Tree>();

		for(int depth = 0; depth < searchDepth; depth++){
			SortedSet<TreePair> levelSet = new TreeSet<TreePair>();
			if(randomSearch){
				//System.out.println("Generating ... " + depth);
				//DeterministicExpressionExploreTest.writeDot("nfa.dot", nfa);
				Set<SparseNFA> NFAs = generator.generateDFAs(nfa, nfa.getNumberOfStates() + depth);
				
				/*for(NFA currentNFA : NFAs){
					//System.out.println("Testing nfa ...");
					if(!currentNFA.transitions().isDeterministic())
						System.out.println("I generated a non-deterministic NFA");
					Simulator simulator = new Simulator(new EquivalenceCondition());
					try {
						if(!simulator.simulate(nfa, currentNFA)){
							DeterministicExpressionExploreTest.writeDot("nfa.dot", nfa);
							DeterministicExpressionExploreTest.writeDot("currentNfa.dot", currentNFA);
							System.out.println("They are not equivalent!!");
						}
					} catch (NotDFAException e) {
						System.out.println("Used a non-deterministic NFA");
						e.printStackTrace();
					}
				}*/
				
				repairNFAs(levelSet, NFAs);
			}
			else {
				Set<SparseNFA> NFAs;
				if(!doExpansions){
					NFAs = generator.generateDFAs(nfa, nfa.getNumberOfStates() + depth);
					if(NFAs.size() >= absoluteUpperBound)
						doExpansions = true;
					else {
						repairNFAs(levelSet, NFAs);
						previousSet = getBestNFAs(levelSet);
					}
				}
				if(doExpansions){
					NFAs = expandNFAs();
					repairNFAs(levelSet,NFAs);
					previousSet = getBestNFAs(levelSet);
				}
			}

			if(!levelSet.isEmpty())
				result.add(levelSet.first().getTree());
		}

		if(result.isEmpty())
			throw new NoOpportunityFoundException("Sorry: didn't find a repair",null);
		return result;

	}

	/**
	 * @param previousSet2
	 * @return
	 */
	private Set<SparseNFA> expandNFAs() {
		Set<SparseNFA> result = new HashSet<SparseNFA>();
		DFAExpander expander = new DFAExpander();

		for(SparseNFA nfa : previousSet){
			int counter = 0;
			int tries = 0;
			while(tries < 5 * nrExpansions && counter < nrExpansions){
				SparseNFA newNFA = expander.expandDFA(nfa);
				if(newNFA != null){
					result.add(newNFA);
					counter++;
				}
				tries++;
			}
		}

		return result;
	}

	/**
	 * @param levelSet
	 * @return
	 */
	private Set<SparseNFA> getBestNFAs(SortedSet<TreePair> levelSet) {
		SortedSet<TreePair> copy = new TreeSet<TreePair>(levelSet);
		Set<SparseNFA> result = new HashSet<SparseNFA>();

		int counter = 0;
		while(!copy.isEmpty() && counter < setSize){
			TreePair first = copy.first();
			copy.remove(first);
			result.add((SparseNFA) first.getNfa());
		}

		return result;
	}

	private void repairNFAs(SortedSet<TreePair> levelSet, Set<SparseNFA> NFAs)
	throws SExpressionParseException, UnknownOperatorException,
	FeatureNotSupportedException {
		eu.fox7.flt.regex.infer.rwr.impl.GraphAutomatonFactory factory = new eu.fox7.flt.regex.infer.rwr.impl.GraphAutomatonFactory();
		for(SparseNFA expandedNFA : NFAs){
			try {
				String repairedRE;
				repairedRE = rewriter.rewriteToRegex(factory.create(expandedNFA));
				Regex repairedRegex = new Regex(repairedRE);
				Glushkov g = new Glushkov();
				if(!g.isAmbiguous(repairedRegex.getTree())){
					//System.out.println("Deterministic one!");
					Double langSize = new Double(1 - Math.abs(measure.compute(factory.create(repairedRE))));
					levelSet.add(new TreePair(expandedNFA,repairedRegex.getTree(),langSize));
				}
			} catch (Exception e) {}
		}
	}

}
