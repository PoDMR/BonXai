/**
 * 
 */
package eu.fox7.util.automata.disambiguate;

import eu.fox7.flt.automata.FeatureNotSupportedException;
import eu.fox7.flt.automata.NotDFAException;
import eu.fox7.flt.automata.converters.NFAMinimizer;
import eu.fox7.flt.automata.factories.sparse.Determinizer;
import eu.fox7.flt.automata.impl.sparse.SparseNFA;
import eu.fox7.flt.automata.measures.LanguageSize;
import eu.fox7.flt.regex.Glushkov;
import eu.fox7.flt.regex.Regex;
import eu.fox7.flt.regex.UnknownOperatorException;
import eu.fox7.flt.regex.generators.LanguageGenerator;
import eu.fox7.flt.regex.infer.rwr.BacktrackingRepairer;
import eu.fox7.flt.regex.infer.rwr.FixedOrderRepairer;
import eu.fox7.flt.regex.infer.rwr.NoOpportunityFoundException;
import eu.fox7.flt.regex.infer.rwr.RewriteEngine;
import eu.fox7.flt.regex.infer.rwr.impl.GraphAutomatonFactory;
import eu.fox7.flt.regex.infer.rwr.measures.LanguageSizeMeasure;
import eu.fox7.util.tree.Node;
import eu.fox7.util.tree.SExpressionParseException;
import eu.fox7.util.tree.Tree;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

/**
 * @author woutergelade
 *
 */
public class DeterministicApproximatorShrink implements
DeterministicApproximator {

	private RewriteEngine repairer;
	private DeterministicExpression deterministic;
	private int poolSize;
	private LanguageSize measure;
	private boolean testIsomorphism;
	private int shrinkBound;

	public DeterministicApproximatorShrink(){
		//repairer = new FixedOrderRepairer();
		deterministic = new DeterministicExpressionExplore(5,20);
		repairer = new BacktrackingRepairer(new LanguageSizeMeasure(),1);
		((BacktrackingRepairer)repairer).setFilter(new DeterminismFilter());
		testIsomorphism = true;
	}


	public DeterministicApproximatorShrink(int poolSize, int exploreDepth,
			                               int exporeThreshold, int backtrackLevel) {
		this.poolSize = poolSize;
		this.shrinkBound = 5;
		//this.nrExpansions = nrExpansions;
		//this.nrTries = nrTries;
		deterministic = new DeterministicExpressionExplore(exploreDepth,exporeThreshold);
		testIsomorphism = true;
		repairer = new FixedOrderRepairer();
		//repairer = new BacktrackingRepairer(new LanguageSizeMeasure(),backtrackLevel);
		//((BacktrackingRepairer)repairer).setFilter(new DeterminismFilter());
		//((BacktrackingRepairer) repairer).setVerbose(false);
	}

	public DeterministicApproximatorShrink(int poolSize, int exploreDepth, int exploreThreshold) {
		this(poolSize, exploreDepth, exploreThreshold, 1);
	}
	public void setTestIsomorphism(boolean testIsomorphism){
		this.testIsomorphism = testIsomorphism;
	}

	/*public DeterministicApproximatorShrink(RewriteEngine rewriter){
		this.repairer = rewriter;
	}*/

	/*public void setRewriter(RewriteEngine rewriter){
		this.repairer = rewriter;
	}*/

	/* (non-Javadoc)
	 * @see eu.fox7.util.automata.disambiguate.DeterministicApproximator#deterministicApproximation(eu.fox7.flt.automata.impl.sparse.SparseNFA)
	 */
	public Set<Tree> deterministicApproximation(String regexStr) throws SExpressionParseException, UnknownOperatorException, FeatureNotSupportedException {
//		Glushkov g = new Glushkov();
		SparseNFA nfa = new SparseNFA(new LanguageGenerator(regexStr).getNFA());
		Set<Tree> nfas = deterministicApproximation(nfa);
		if(nfas.isEmpty())
			nfas.add(sigmaStarExpression(regexStr));

		return nfas;
	}

	

	protected Set<Tree> deterministicApproximation(SparseNFA nfa) throws SExpressionParseException, UnknownOperatorException, FeatureNotSupportedException {
		measure = new LanguageSize();
		
		nfa = Determinizer.dfa(nfa);
		new NFAMinimizer().minimize(nfa);
		DFAShrinker shrinker = new DFAShrinker(nfa.getNumberOfStates());
		shrinker.setUpperbound(shrinkBound);
		nfa = DFAExpander.constructGlushkovAutomaton(nfa);
		Set<Tree> result = new HashSet<Tree>();

		IsomorphismTree testedNFAs = new IsomorphismTree();
		IsomorphismTree generatingNFAs = new IsomorphismTree();
		Set<SparseNFA> currentNFAs = new HashSet<SparseNFA>();
		currentNFAs.add(nfa);

		if(BKWTools.isUnambiguous(nfa))
			result.addAll(convertToExpressions(currentNFAs, currentNFAs, new HashSet<SparseNFA>(), testedNFAs));
		else
			result.addAll(convertToExpressions(currentNFAs, new HashSet<SparseNFA>(), currentNFAs, testedNFAs));
		

		int iterations = 0;
		while(!currentNFAs.isEmpty()){
			//System.err.println("After " + iterations++ + " iterations, we have " + result.size() + " results");
			//System.err.println("Shrinking ...");
			Set<SparseNFA> newNFAs = shrinker.shrink(currentNFAs);
			//System.err.println("Created" + newNFAs.size() + "NFAs");
			Set<SparseNFA> deterministicLanguages = new HashSet<SparseNFA>();
			Set<SparseNFA> nonDeterministicLanguages = new HashSet<SparseNFA>();
			
			//System.err.println("Splitting ...");
			
			splitNFAs(newNFAs,deterministicLanguages,nonDeterministicLanguages);
						
			//System.err.println("Ranking ...");

			currentNFAs = keepTopDFAs(nfa, deterministicLanguages,nonDeterministicLanguages, generatingNFAs);
			
			//System.err.println("Converting ...");
			
			result.addAll(convertToExpressions(currentNFAs, deterministicLanguages, nonDeterministicLanguages, testedNFAs));
			
			Runtime r = Runtime.getRuntime(); 
			r.gc();
		}
		return result;
	}

	/**
	 * @param deterministicLanguages
	 * @param nonDeterministicLanguages
	 * @param generatingNFAs 
	 * @return
	 */
	private Set<SparseNFA> keepTopDFAs(SparseNFA nfa, Set<SparseNFA> deterministicLanguages,
			Set<SparseNFA> nonDeterministicLanguages, IsomorphismTree generatingNFAs) {
		Set<SparseNFA> result = new HashSet<SparseNFA>();

		DeterministicApproximatorIncreaseLanguage approx = new DeterministicApproximatorIncreaseLanguage();
		LinkedList<SparseNFA> orderedDeterministic = approx.orderDFAs(nfa, deterministicLanguages, measure);
		while(result.size() < poolSize && !orderedDeterministic.isEmpty()){
			SparseNFA dfa = orderedDeterministic.getFirst();
			orderedDeterministic.removeFirst();
			try {
				if(!testIsomorphism || generatingNFAs.addNFA(dfa))
					result.add(dfa);
			} catch (NotDFAException e) {
				System.err.println("Shrink generated an NFA");
				e.printStackTrace();
			}
		}

		if(result.size() >= poolSize)
			return result;

		LinkedList<SparseNFA> orderedNonDeterministic = approx.orderDFAs(nfa, nonDeterministicLanguages, measure);
		while(result.size() < poolSize && !orderedNonDeterministic.isEmpty()){
			SparseNFA dfa = orderedNonDeterministic.getFirst();
			orderedNonDeterministic.removeFirst();
			try {
				if(!testIsomorphism || generatingNFAs.addNFA(dfa))
					result.add(dfa);
			} catch (NotDFAException e) {
				System.err.println("Shrink generated an NFA");
				e.printStackTrace();
			}
		}

		return result;
	}

	/**
	 * @param testedNFAs 
	 * @param deterministicLanguages
	 * @param nonDeterministicLanguages 
	 * @return
	 */
	protected Set<Tree> convertToExpressions(Set<SparseNFA> NFAs, Set<SparseNFA> deterministicLanguages, Set<SparseNFA> nonDeterministicLanguages, IsomorphismTree testedNFAs) {
		Set<Tree> result = new HashSet<Tree>();
		eu.fox7.flt.regex.infer.rwr.impl.GraphAutomatonFactory factory = new eu.fox7.flt.regex.infer.rwr.impl.GraphAutomatonFactory();

		for(SparseNFA nfa : deterministicLanguages){
			if(NFAs.contains(nfa)){
				try {
					if(!testIsomorphism || testedNFAs.addNFA(nfa)){
						Tree tree = deterministic.deterministicExpression(nfa);
						if(tree != null)
							result.add(tree);
						String approximation = repairer.rewriteToRegex(factory.create(DFAExpander.constructGlushkovAutomaton(nfa)));
						Glushkov g = new Glushkov();
						if(approximation != null && !g.isAmbiguous(approximation)){
							Regex re = new Regex(approximation);
							result.add(re.getTree());
						}

					}
				} catch (NoOpportunityFoundException e) {
				} catch (SExpressionParseException e) {
				} catch (UnknownOperatorException e) {
				} catch (FeatureNotSupportedException e) {
				} catch (RuntimeException e) {
				} catch (NotDFAException e) {
					System.err.println("Shrink created an NFA");
				}
			}
		}

		for(SparseNFA nfa : nonDeterministicLanguages){
			if(NFAs.contains(nfa)){
				try {
					if(!testIsomorphism || testedNFAs.addNFA(nfa)){
						String approximation = repairer.rewriteToRegex(factory.create(DFAExpander.constructGlushkovAutomaton(nfa)));
						Glushkov g = new Glushkov();
						if(approximation != null && !g.isAmbiguous(approximation)){
							Regex re = new Regex(approximation);
							result.add(re.getTree());
						}
					}
				} catch (NoOpportunityFoundException e) {
				} catch (SExpressionParseException e) {
				} catch (UnknownOperatorException e) {
				} catch (FeatureNotSupportedException e) {
				} catch (RuntimeException e) {
				} catch (NotDFAException e) {
					System.err.println("Shrink created an NFA");
				}
			}
		}

		return result;
	}

	/**
	 * @param newNFAs
	 * @param deterministicLanguages
	 * @param nonDeterministicLanguages
	 */
	protected void splitNFAs(Set<SparseNFA> NFAs, Set<SparseNFA> deterministicLanguages, Set<SparseNFA> nonDeterministicLanguages) {		
		for(SparseNFA nfa : NFAs){
			if(BKWTools.isUnambiguous(nfa))
				deterministicLanguages.add(nfa);
			else
				nonDeterministicLanguages.add(nfa);
		}	
	}
	
	/**
	 * @param regexStr
	 * @return
	 */
	protected static Tree sigmaStarExpression(String regexStr) {
		try {
			Regex re = new Regex(regexStr);
			Node root = new Node(Regex.ZERO_OR_MORE_OPERATOR);
			Tree tree = new Tree();
			tree.setRoot(root);
			Node disjunction = new Node(Regex.UNION_OPERATOR);
			root.addChild(disjunction);
			for(String symbol : re.getAlphabet()){
				Node symbolNode = new Node(symbol);
				disjunction.addChild(symbolNode);
			}
			return tree;
			
			
		} catch (SExpressionParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnknownOperatorException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}

	
}
