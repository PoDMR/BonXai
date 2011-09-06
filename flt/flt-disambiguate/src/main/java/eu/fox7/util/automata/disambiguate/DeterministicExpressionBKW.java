/**
 * 
 */
package eu.fox7.util.automata.disambiguate;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections15.BidiMap;
import org.apache.commons.collections15.bidimap.DualHashBidiMap;

import eu.fox7.flt.automata.FeatureNotSupportedException;
import eu.fox7.flt.automata.converters.NFAMinimizer;
import eu.fox7.flt.automata.converters.Simplifier;
import eu.fox7.flt.automata.factories.sparse.Determinizer;
import eu.fox7.flt.automata.impl.sparse.SparseNFA;
import eu.fox7.flt.automata.impl.sparse.State;
import eu.fox7.flt.automata.impl.sparse.Transition;
import eu.fox7.flt.regex.Glushkov;
import eu.fox7.flt.regex.Regex;
import eu.fox7.flt.regex.UnknownOperatorException;
import eu.fox7.flt.regex.generators.LanguageGenerator;
import eu.fox7.flt.regex.infer.rwr.NoOpportunityFoundException;
import eu.fox7.flt.regex.infer.rwr.impl.GraphAutomatonFactory;
import eu.fox7.flt.regex.infer.rwr.measures.LanguageSizeMeasure;
import eu.fox7.util.Pair;
import eu.fox7.util.RandomSelector;
import eu.fox7.util.tree.Node;
import eu.fox7.util.tree.SExpressionParseException;
import eu.fox7.util.tree.Tree;


/**
 * @author woutergelade
 *
 */
public class DeterministicExpressionBKW implements DeterministicExpression, DeterministicApproximator {
	
	//int dotCounter = 0;

	public static int NR_NO_EXPRESSION_FOUND = 0;
	public static int NR_EXPRESSION_FOUND = 0;

	public static final int NO_REPAIR = 0;
	public static final int BRANCH_RANDOM = 1;
	public static final int BRANCH_LANG_SIZE = 2;

	public static final int SELECT_RANDOM = 3;
	public static final int SELECT_SIZE = 4;
	public static final int SELECT_LANG_SIZE = 5;

	public static final int INFINITY = 100000;

	protected LanguageSizeMeasure languageSize;

	protected int repairBranchCriterion;
	protected int repairSelectCriterion;
	protected int repairSelectFactor;
	protected int repairBranchingFactor;
	protected boolean optimizeSingleOrbit;
	protected boolean optimizeMultipleOrbit;
	protected boolean removeNonDeterministic;
	public String defaultFile = "/Users/woutergelade/Documents/Data/workspace/Disambiguate/automaton.dot";
	
	private SparseNFA globalNFA;

	public DeterministicExpressionBKW(){
		repairBranchCriterion = NO_REPAIR;
		repairSelectFactor = 1;
		optimizeSingleOrbit = false;
		repairSelectCriterion = SELECT_RANDOM;
		optimizeMultipleOrbit = false;
		removeNonDeterministic = false;
	}

	public DeterministicExpressionBKW(boolean optimizeSingleOrbit,boolean optimizeMultipleOrbit){
		this.optimizeSingleOrbit = optimizeSingleOrbit;
		this.optimizeMultipleOrbit = optimizeMultipleOrbit;
		repairBranchCriterion = NO_REPAIR;
		repairSelectFactor = 1;
		repairSelectCriterion = SELECT_RANDOM;
		removeNonDeterministic = false;
	}

	public void setRepairStatus(int branchingCriterion, int branchingFactor, int selectCriterion, int selectionFactor){
		repairBranchCriterion = branchingCriterion;
		repairSelectFactor = selectionFactor;
		repairBranchingFactor = branchingFactor;
		repairSelectCriterion = selectCriterion;
		removeNonDeterministic = true;
	}
	
	public SparseNFA ahonen(SparseNFA nfa){
		SparseNFA dfa = new SparseNFA(nfa);
		nfa = Determinizer.dfa(nfa);
		new NFAMinimizer().minimize(dfa);
		return ahonenRecursive(dfa);
	}

	/**
	 * @param dfa
	 * @return
	 */
	private SparseNFA ahonenRecursive(SparseNFA dfa) {
		globalNFA = new SparseNFA(dfa);
		disambiguate(dfa);
		//DeterministicExpressionExploreTest.writeDot("beforeDeterminizeByMerge.dot", globalNFA);
		globalNFA = determinizeByMerge(globalNFA);
		return globalNFA;
	}
	

	/**
	 * @param dfa
	 */
	protected void disambiguate(SparseNFA dfa) {
		Simplifier.simplify(dfa);
		
		//DeterministicExpressionExploreTest.writeDot("GlobalDFA" + dotCounter + ".dot",globalNFA);
		//DeterministicExpressionExploreTest.writeDot("CurrentDFA" + dotCounter++ + ".dot",dfa);
		
		if(!BKWTools.isTrivial(dfa)){
			StronglyConnectedComponents sccCompute = new LinearStronglyConnectedComponents();
			Set<HashSet<String>> sccs = sccCompute.computeStronglyConnectedComponents(dfa,dfa.getStateValue(dfa.getInitialState()));
			
			Set<String> sConsistent = BKWTools.sConsistentSymbols(dfa);
			if(sccs.size() == 1 && sConsistent.isEmpty()){
				Set<String> candidates = BKWTools.computeCandidateSConsistentSymbols(dfa);
				if(candidates.isEmpty())
					System.out.println("Something wrong: didn't find any repair candidates");
				RandomSelector<String> selector = new RandomSelector<String>();
				String symbol = selector.selectOneFrom(candidates);
				makeSymbolConsistent(dfa,symbol);
				disambiguate(dfa);
			}
			else {
				BKWTools.removeFinalTransitions(dfa, sConsistent);
				sccs = sccCompute.computeAllStronglyConnectedComponents(dfa);
				if(sccs.size() == 1)
					disambiguate(dfa);
				else {
					if(BKWTools.hasOrbitProperty(dfa, sccs)){
						for(HashSet<String> scc : sccs){
							SparseNFA orbitAutomaton = BKWTools.createOrbitAutomaton(dfa,scc, null);
							disambiguate(orbitAutomaton);
						}
					}
					else {
						forceOrbitProperty(dfa,sccs);
						sccs = sccCompute.computeAllStronglyConnectedComponents(dfa);
						for(HashSet<String> scc : sccs){
							SparseNFA orbitAutomaton = BKWTools.createOrbitAutomaton(dfa,scc, null);
							disambiguate(orbitAutomaton);
						}
					}
				}
				
			}
		}	
	}


	protected SparseNFA determinizeByMerge(SparseNFA nfa) {
		while(!nfa.getTransitionMap().checkDeterminism()){
			boolean merged = false;
			Set<String> states = new HashSet<String>(nfa.getStateValues());
			for(String state : states){
				for(String symbol : nfa.getAlphabet().keySet()){
					HashSet<String> nextStates = (HashSet<String>) nfa.getNextStateValues(symbol, state);
					if(nextStates.size() > 1){
						DFAShrinker shrinker = new DFAShrinker();
						shrinker.mergeStates(nfa, nextStates, false);
						merged = true;
						break;
					}
				}
				if(merged)
					break;
			}
		}
		return nfa;
		
	}

	public void setRemoveNonDeterministic(boolean removeNonDeterministic){
		this.removeNonDeterministic = removeNonDeterministic;
	}

	public void setOptimizeSingleOrbit(boolean optimizeSingleOrbit){
		this.optimizeSingleOrbit = optimizeSingleOrbit;
	}

	public void setOptimizeMultipleOrbit(boolean optimizeMultipleOrbit){
		this.optimizeMultipleOrbit = optimizeMultipleOrbit;
	}

	public void optimizeSingleOrbit(){
		optimizeSingleOrbit = true;
	}

	/* (non-Javadoc)
	 * @see eu.fox7.util.automata.disambiguate.DeterministicApproximator#deterministicApproximation(eu.fox7.flt.automata.impl.sparse.SparseNFA)
	 */
	public Set<Tree> deterministicApproximation(String regexStr) throws NoOpportunityFoundException, SExpressionParseException, UnknownOperatorException, FeatureNotSupportedException {
//		Glushkov g = new Glushkov();
		SparseNFA dfa = new SparseNFA(new LanguageGenerator(regexStr).getNFA());
		dfa = Determinizer.dfa(dfa);
		new NFAMinimizer().minimize(dfa);

		int maxLength = ((new Regex(regexStr)).getTotalOccurrencesOfSymbol() * 2) + 1;
		languageSize = new LanguageSizeMeasure();
		languageSize.setMaxLength(maxLength);
		
		Set<Tree> result = deterministicExpressionMinimal(dfa);
		
		result = applySelectionCriteria(result);

		return result;
	}
	
	public Tree deterministicExpression(String regexStr) throws NoOpportunityFoundException, SExpressionParseException, UnknownOperatorException, FeatureNotSupportedException{
//		Glushkov g = new Glushkov();
		SparseNFA dfa = new SparseNFA(new LanguageGenerator(regexStr).getNFA());
		
		int maxLength = ((new Regex(regexStr)).getTotalOccurrencesOfSymbol() * 2) + 1;
		languageSize = new LanguageSizeMeasure();
		languageSize.setMaxLength(maxLength);
		
		return deterministicExpression(dfa);
	}

	public Tree deterministicExpression(SparseNFA dfa) throws NoOpportunityFoundException {
		dfa = Determinizer.dfa(dfa);
		new NFAMinimizer().minimize(dfa);

		/*if(!isUnambiguous(dfa))
			BKWTools.throwNoOpportunityFoundException("automaton is not convertable to expression", dfa);
		else*/
		return eu.fox7.util.Collections.getOne(deterministicExpressionMinimal(dfa));
	}

	protected Set<Tree> deterministicExpressionMinimal(SparseNFA nfa) throws NoOpportunityFoundException{
		if(BKWTools.isTrivial(nfa)){
			Set<Tree> result = new HashSet<Tree>();
			if(nfa.isFinalState(nfa.getInitialState()))
				result.add(new Tree(Regex.EPSILON_SYMBOL));
			else
				result.add(new Tree(Regex.EMPTY_SYMBOL));
			return result;
		}
		else{
			//Modification
			//Set<HashSet<String>> sccs = stronglyConnectedComponents(nfa);
			StronglyConnectedComponents sccCompute = new LinearStronglyConnectedComponents();
			Set<HashSet<String>> sccs = sccCompute.computeStronglyConnectedComponents(nfa,nfa.getStateValue(nfa.getInitialState()));
			if(sccs.size() == 1){
				Set<String> sConsistent = BKWTools.sConsistentSymbols(nfa);
				if(!sConsistent.isEmpty()){
					return constructSingleOrbitExpression(nfa, sConsistent);
				}
				else if(repairBranchCriterion == NO_REPAIR){
					throw new NoOpportunityFoundException("Didn't have an S-Consistent Symbol and couldn't repair",null);
				}
				else {
					Set<String> candidates = BKWTools.computeCandidateSConsistentSymbols(nfa);
					if(candidates.isEmpty())
						throw new NoOpportunityFoundException("Didn't find a repair candidate",null);
					else{
						candidates = pruneAccordingToBranchingFactor(nfa,candidates);

						Set<Tree> result = new HashSet<Tree>();
						for(String symbol : candidates){
							SparseNFA newNFA = new SparseNFA(nfa);
							makeSymbolConsistent(newNFA,symbol);
							sConsistent.add(symbol);
							if(!BKWTools.sConsistentSymbols(newNFA).contains(symbol))
								System.out.print("oeps");
							result.addAll(constructSingleOrbitExpression(newNFA, sConsistent));
							sConsistent.remove(symbol);
						}

						return applySelectionCriteria(result);
					}
				}
			}
			else{
				//System.out.println("has multiple orbit");
				if(BKWTools.hasOrbitProperty(nfa,sccs)){
					//System.out.println("and orbit property");
					Set<Tree> result = constructMultipleOrbitExpression(nfa, sccs);
					return applySelectionCriteria(result);
				}
				else{
					if(repairBranchCriterion == NO_REPAIR)
						throw new NoOpportunityFoundException("Automaton does not have Orbit Property", null);
					else{
						//System.out.println("but no orbit property");
						/*count++;
						System.out.println(count);
						if(count == 10){
							System.out.println("Infinite Loop");
						}*/
						//DeterministicExpressionExploreTest.writeDot("beforeForce1.dot", nfa);
						forceOrbitProperty(nfa,sccs);
						//DeterministicExpressionExploreTest.writeDot("afterForce1.dot", nfa);
						return deterministicExpressionMinimal(nfa);
					}
				}
			}
		}
	}

	private Set<Tree> applySelectionCriteria(Set<Tree> result)
	throws NoOpportunityFoundException {
		if(removeNonDeterministic)
			result = removeNonDeterministic(result);

		if(result.isEmpty())
			throw new NoOpportunityFoundException("All intermediately constructed expressions were already deterministic",null);

		result = giveTopK(result);

		return result;
	}

	/**
	 * @param result
	 * @return
	 */
	private Set<Tree> giveTopK(Set<Tree> input) {
		if(repairSelectFactor == INFINITY || input.size() <= repairSelectFactor)
			return input;
		else if(repairSelectCriterion == SELECT_RANDOM){
			RandomSelector<Tree> selector = new RandomSelector<Tree>();
			return selector.selectSubsetFrom(input, repairSelectFactor);
		}
		else if(repairSelectCriterion == SELECT_SIZE){
			Set<Pair<Tree,Double>> result = new HashSet<Pair<Tree,Double>>();

			for(Tree tree : input){
				try {
					addIfNeeded(result,tree,(new Regex(tree)).getTotalOccurrencesOfSymbol());
				} catch (UnknownOperatorException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			Set<Tree> resultSet = new HashSet<Tree>();
			for(Pair<Tree,Double> pair : result)
				resultSet.add(pair.getFirst());
			return resultSet;
		}
		else if(repairSelectCriterion == SELECT_LANG_SIZE){
			Set<Pair<Tree,Double>> result = new HashSet<Pair<Tree,Double>>();
			eu.fox7.flt.regex.infer.rwr.impl.GraphAutomatonFactory factory = new eu.fox7.flt.regex.infer.rwr.impl.GraphAutomatonFactory();

			for(Tree tree : input){
				try {				
					addIfNeeded(result,tree,1 - Math.abs(languageSize.compute(factory.create(tree.toSExpression()))));
				} catch (SExpressionParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (UnknownOperatorException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (FeatureNotSupportedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			Set<Tree> resultSet = new HashSet<Tree>();
			for(Pair<Tree,Double> pair : result)
				resultSet.add(pair.getFirst());
			return resultSet;
		}
		else{
			System.out.println("The repair selection criterion is not properly set");
			return null;
		}
	}

	/**
	 * @param result
	 * @param tree
	 * @param abs
	 */
	private void addIfNeededBranching(Set<Pair<String, Double>> result, String tree, double value) {
		Pair<String,Double> newPair = new Pair<String, Double>(tree, new Double(value));

		if(result.size() < repairBranchingFactor)
			result.add(newPair);
		else{
			Pair<String,Double> biggest = eu.fox7.util.Collections.getOne(result);
			for(Pair<String,Double> pair : result){
				if(pair.getSecond().doubleValue() > biggest.getSecond().doubleValue())
					biggest = pair;
			}
			if(value < biggest.getSecond().doubleValue()){
				result.remove(biggest);
				result.add(newPair);
			}
		}
	}

	/**
	 * @param result
	 * @param tree
	 * @param abs
	 */
	private void addIfNeeded(Set<Pair<Tree, Double>> result, Tree tree, double value) {
		Pair<Tree,Double> newPair = new Pair<Tree, Double>(tree, new Double(value));

		if(result.size() < repairSelectFactor)
			result.add(newPair);
		else{
			Pair<Tree,Double> biggest = eu.fox7.util.Collections.getOne(result);
			for(Pair<Tree,Double> pair : result){
				if(pair.getSecond().doubleValue() > biggest.getSecond().doubleValue())
					biggest = pair;
			}
			if(value < biggest.getSecond().doubleValue()){
				result.remove(biggest);
				result.add(newPair);
			}
		}
	}

	/**
	 * @param result
	 */
	private Set<Tree> removeNonDeterministic(Set<Tree> set) {
		Set<Tree> result = new HashSet<Tree>();
		Glushkov g = new Glushkov();
		for(Tree tree : set){
			try {
				if(!g.isAmbiguous(tree.toSExpression()))
					result.add(tree);
			} catch (SExpressionParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (UnknownOperatorException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (FeatureNotSupportedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return result;	
	}

	/**
	 * @param nfa 
	 * @param candidates
	 */
	private Set<String> pruneAccordingToBranchingFactor(SparseNFA nfa, Set<String> candidates) {
		if(repairBranchingFactor == INFINITY || candidates.size() <= repairBranchingFactor)
			return candidates;
		else{
			if(repairBranchCriterion == BRANCH_RANDOM){
				RandomSelector<String> selector = new RandomSelector<String>();
				return selector.selectSubsetFrom(candidates, repairSelectFactor);
			}
			else if(repairBranchCriterion == BRANCH_LANG_SIZE){
				Set<Pair<String,Double>> result = new HashSet<Pair<String,Double>>();
				eu.fox7.flt.regex.infer.rwr.impl.GraphAutomatonFactory factory = new eu.fox7.flt.regex.infer.rwr.impl.GraphAutomatonFactory();

				for(String symbol : candidates){
					SparseNFA newNFA = new SparseNFA(nfa);
					makeSymbolConsistent(newNFA, symbol);
					newNFA = DFAExpander.constructGlushkovAutomaton(newNFA);
					addIfNeededBranching(result,symbol,1 - Math.abs(languageSize.compute(factory.create(newNFA))));
				}

				Set<String> resultSet = new HashSet<String>();
				for(Pair<String,Double> pair : result)
					resultSet.add(pair.getFirst());
				return resultSet;

			}
			else{
				System.out.println("Bug: trying to repair using an unknown branching criterion");
				return null;
			}
		}


	}

	private Set<Tree> constructMultipleOrbitExpression(SparseNFA nfa,
			Set<HashSet<String>> sccs) throws NoOpportunityFoundException {
		if(optimizeMultipleOrbit)
			return constructMultipleOrbitExpressionOptimized(nfa, sccs);
		else
			return constructMultipleOrbitExpressionNotOptimized(nfa, sccs);
	}

	private Set<Tree> constructSingleOrbitExpression(SparseNFA nfa,
			Set<String> sConsistent) throws NoOpportunityFoundException {
		if(optimizeSingleOrbit)
			return constructSingleOrbitExpressionOptimized(nfa, sConsistent);
		else
			return constructSingleOrbitExpressionNotOptimized(nfa, sConsistent);
	}



	/**
	 * @param deterministicExpressionBKWOptimized
	 * @param nfa
	 * @param consistent
	 * @param repair
	 * @return
	 * @throws NoOpportunityFoundException 
	 */
	/*public static Tree constructSingleOrbitExpressionOptimized(
			DeterministicExpressionBKWOptimized det,
			NFA nfa, Set<String> sConsistent, boolean repair) {
		String finalState = nfa.getStateValue(eu.fox7.util.Collections.getOne(nfa.getFinalStates()));
		Map<String,HashSet<String>> fromStates = new HashMap<String,HashSet<String>>();
		for(String symbol : sConsistent){
			if(!nfa.getNextStateValues(symbol, finalState).isEmpty()){
				String toState = eu.fox7.util.Collections.extractSingleton(nfa.getNextStateValues(symbol, finalState));
				if(fromStates.containsKey(toState))
					fromStates.get(toState).add(symbol);
				else{
					HashSet<String> set = new HashSet<String>();
					set.add(symbol);
					fromStates.put(toState, set);
				}
			}
		}

		Map<String,String> toStatesNaive = new HashMap<String,String>();
		for(String symbol : sConsistent){
			if(!nfa.getNextStateValues(symbol, finalState).isEmpty())
				toStatesNaive.put(symbol, eu.fox7.util.Collections.extractSingleton(nfa.getNextStateValues(symbol, finalState)));
		}

		Set<String> inToStatesNaive = new HashSet<String>();
		for(String state : toStatesNaive.keySet())
			inToStatesNaive.add(toStatesNaive.get(state));

		if(inToStatesNaive.size() != fromStates.keySet().size())
			System.out.println("problem");
		else if(eu.fox7.util.Collections.intersect(inToStatesNaive, fromStates.keySet()).size() != inToStatesNaive.size())
			System.out.println("problem");

		NFA nfaWithoutS = new NFA(nfa);
		removeFinalTransitions(nfaWithoutS,sConsistent);

		Set<Node> resultingExpressions = new HashSet<Node>();

		for(String toState : fromStates.keySet()){
			NFA nfaForSymbol = new NFA(nfaWithoutS);
			setInitialStateClean(nfaForSymbol,toState);
			Tree result = det.deterministicExpressionMinimal(det, nfaForSymbol, repair);
			if(result == null)
				System.out.println("Bug");
			Set<String> symbols = fromStates.get(toState);
			Tree precedingSymbols = createDisjunction(symbols);
			resultingExpressions.add(concatenate(precedingSymbols,result));
		}
		Tree reWithoutS = det.deterministicExpressionMinimal(det, nfaWithoutS, repair);
		if(reWithoutS == null)
			return null;
		Tree result = composeSingleOrbitExpressionsOptimized(reWithoutS,resultingExpressions);
		return result;
	}*/

	protected Set<Tree> constructSingleOrbitExpressionOptimized(SparseNFA nfa,
			Set<String> sConsistent) throws NoOpportunityFoundException {
		String finalState = nfa.getStateValue(eu.fox7.util.Collections.getOne(nfa.getFinalStates()));
		Map<String,String> toStates = new HashMap<String,String>();
		for(String symbol : sConsistent){
			if(!nfa.getNextStateValues(symbol, finalState).isEmpty())
				toStates.put(symbol, eu.fox7.util.Collections.extractSingleton(nfa.getNextStateValues(symbol, finalState)));
		}


		Map<String,HashSet<String>> fromStates = new HashMap<String,HashSet<String>>();
		for(String symbol : sConsistent){
			if(!nfa.getNextStateValues(symbol, finalState).isEmpty()){
				String toState = eu.fox7.util.Collections.extractSingleton(nfa.getNextStateValues(symbol, finalState));
				if(fromStates.containsKey(toState))
					fromStates.get(toState).add(symbol);
				else{
					HashSet<String> set = new HashSet<String>();
					set.add(symbol);
					fromStates.put(toState, set);
				}
			}
		}


		SparseNFA nfaWithoutS = new SparseNFA(nfa);
		BKWTools.removeFinalTransitions(nfaWithoutS,sConsistent);
		Map<Tree,Set<Tree>> resultingExpressions = new HashMap<Tree,Set<Tree>>();

		for(String toState : fromStates.keySet()){
			SparseNFA nfaForSymbol = new SparseNFA(nfaWithoutS);
			BKWTools.setInitialStateClean(nfaForSymbol,toState);
			Set<Tree> result = deterministicExpressionMinimal(nfaForSymbol);
			Tree initial = BKWTools.createDisjunction(fromStates.get(toState));
			if(result.isEmpty())
				System.out.println("Got a null result, where I expected an exception to be thrown");
			resultingExpressions.put(initial, result);


		}
		Set<Tree> reWithoutS = deterministicExpressionMinimal(nfaWithoutS);
		if(reWithoutS.isEmpty())
			System.out.println("Got a null result, where I expected an exception to be thrown");
		Set<Tree> result = new HashSet<Tree>();
		result.addAll(composeSingleOrbitExpressionsOptimized(reWithoutS,resultingExpressions));
		return result;
	}



	/**
	 * @param reWithoutS
	 * @param resultingExpressions
	 * @return
	 */
	Tree composeSingleOrbitExpressionsOptimized(Tree reWithoutS,
			Set<Node> resultingExpressions) {
		Tree re = new Tree();
		Node root = new Node(Regex.CONCAT_OPERATOR);
		re.setRoot(root);
		root.addChild(reWithoutS.getRoot());
		Node star = new Node(Regex.ZERO_OR_MORE_OPERATOR);
		root.addChild(star);

		star.addChild(BKWTools.createDisjunctionX(resultingExpressions));
		return re;
	}

	protected Set<Tree> constructSingleOrbitExpressionNotOptimized(SparseNFA nfa,
			Set<String> sConsistent) throws NoOpportunityFoundException {
		String finalState = nfa.getStateValue(eu.fox7.util.Collections.getOne(nfa.getFinalStates()));
		Map<String,String> toStates = new HashMap<String,String>();
		for(String symbol : sConsistent){
			if(!nfa.getNextStateValues(symbol, finalState).isEmpty())
				toStates.put(symbol, eu.fox7.util.Collections.extractSingleton(nfa.getNextStateValues(symbol, finalState)));
		}
		SparseNFA nfaWithoutS = new SparseNFA(nfa);
		BKWTools.removeFinalTransitions(nfaWithoutS,sConsistent);
		Map<String,Set<Tree>> resultingExpressions = new HashMap<String,Set<Tree>>();
		for(String symbol : toStates.keySet()){
			SparseNFA nfaForSymbol = new SparseNFA(nfaWithoutS);
			String state = toStates.get(symbol);
			BKWTools.setInitialStateClean(nfaForSymbol,state);
			Set<Tree> result = deterministicExpressionMinimal(nfaForSymbol);
			if(result.isEmpty())
				System.out.println("Received a null expression where I expected an exception to be thrown");
			resultingExpressions.put(symbol, result);
		}
		Set<Tree> reWithoutS = deterministicExpressionMinimal(nfaWithoutS);
		if(reWithoutS.isEmpty())
			System.out.println("Received a null expression where I expected an exception to be thrown");
		Set<Tree> result = new HashSet<Tree>();
		result.addAll(composeSingleOrbitExpressions(reWithoutS,resultingExpressions));
		return result;
	}

	/**
	 * @param nfa
	 * @param inSCC 
	 * @param sccSet
	 * @param sccTransitions
	 * @param sccNames
	 * @return
	 */
	static SparseNFA computeSCCDFA(
			SparseNFA dfa, Set<HashSet<String>> sccs,
			Map<String, HashSet<String>> inSCC, Set<Pair<String, HashSet<String>>> sccSet,
			Map<Pair<HashSet<String>, String>, Pair<String, HashSet<String>>> sccTransitions,
			BidiMap<String,Pair<String, HashSet<String>>> sccNames) {
		SparseNFA result = new SparseNFA();		
		int count = 0;
		for(Pair<String,HashSet<String>> sccPair : sccSet){
			String newState = "scc" + count++;
			result.addState(newState);
			sccNames.put(newState, sccPair);
		}

		for(Set<String> scc : sccs){
			String gate = DeterministicExpressionBKW.getGate(dfa,scc);
			if(gate != null){
				Set<String> getInitialStates = new HashSet<String>();
				for(String state : scc){
					if(sccSet.contains(new Pair(state,scc))){
						getInitialStates.add(state);
						if(dfa.isFinalState(gate)){
							result.addFinalState(sccNames.getKey(new Pair(state,scc)));
						}
					}
				}

				for(String symbol : dfa.getAlphabet().keySet()){
					if(sccTransitions.containsKey(new Pair(scc,symbol))){
						Pair<String,HashSet<String>> toPair = sccTransitions.get(new Pair(scc,symbol));
						for(String initial : getInitialStates){
							String startState = sccNames.getKey(new Pair(initial,scc));
							String symbolState = symbol + "_" + count++;
							String toState = sccNames.getKey(toPair);
							result.addTransition(symbol, startState, symbolState);
							result.addTransition(toState, symbolState, toState);
						}
					} 
				}
			}
		}

		String originalInitial = dfa.getStateValue(dfa.getInitialState());
		Pair<String,HashSet<String>> initialPair = new Pair(originalInitial,inSCC.get(originalInitial));
		String newInitial = "q" + count++;
		result.addState(newInitial);
		result.addTransition(sccNames.getKey(initialPair), newInitial, sccNames.getKey(initialPair));
		result.setInitialState(newInitial);	

		return result;
	}

	protected Set<Tree> constructMultipleOrbitExpressionOptimized(SparseNFA nfa,
			Set<HashSet<String>> sccs) throws NoOpportunityFoundException {
		// set (getInitialState, scc)
		Set<Pair<String,HashSet<String>>> sccSet = new HashSet<Pair<String,HashSet<String>>>();

		//function : (scc, symbol) -> (getInitialState, scc)
		Map<Pair<HashSet<String>,String>,Pair<String,HashSet<String>>> sccTransitions = new HashMap<Pair<HashSet<String>,String>,Pair<String,HashSet<String>>>();

		Map<String,HashSet<String>> inSCC = new HashMap<String,HashSet<String>>();
		for(HashSet<String> scc : sccs){
			for(String state : scc){
				inSCC.put(state, scc);
			}
		}

		for(HashSet<String> scc : sccs){
			if(scc.contains(nfa.getStateValue(nfa.getInitialState()))){
				sccSet.add(new Pair(nfa.getStateValue(nfa.getInitialState()),scc));
			}
			Set<String> gates = BKWTools.computeGates(nfa,scc);
			for(String gate : gates){
				for(Transition transition : nfa.getOutgoingTransitions(nfa.getState(gate))){
					String toState = nfa.getStateValue(transition.getToState());
					if(!scc.contains(nfa.getStateValue(transition.getToState()))){
						Pair toPair = new Pair(toState,inSCC.get(toState));
						sccSet.add(toPair);
						sccTransitions.put(new Pair(scc,transition.getSymbol().toString()), toPair);
					}					
				}
			}
		}

		BidiMap<String, Pair<String, HashSet<String>>> sccNames = new DualHashBidiMap<String, Pair<String, HashSet<String>>>();
		SparseNFA sccDFA = DeterministicExpressionBKW.computeSCCDFA(nfa,sccs,inSCC,sccSet,sccTransitions,sccNames);

		//DeterministicExpressionExploreTest.writeDot("infiniteLoop.dot", sccDFA);

		DeterministicExpressionExplore explore = new DeterministicExpressionExplore(5,100);
		//explore.setAbsoluteUpperbound(100);
		Tree result = explore.deterministicExpression(sccDFA);
		if(result == null){
			DeterministicExpressionBKW.NR_NO_EXPRESSION_FOUND++;
			return constructMultipleOrbitExpressionNotOptimized(nfa, sccs);
		}
		DeterministicExpressionBKW.NR_EXPRESSION_FOUND++;

		Map<String,Set<Tree>> expressions = new HashMap<String,Set<Tree>>();

		for(Pair<String,HashSet<String>> sccPair : sccSet){
			SparseNFA orbit = BKWTools.createOrbitAutomaton(nfa,sccPair.getSecond(),sccPair.getFirst());
			Set<Tree> expression = deterministicExpressionMinimal(orbit);
			expressions.put(sccNames.getKey(sccPair), expression);
		}

		Set<Tree> realResult = composeMultipleOrbitExpressionOptimized(result,expressions);

		return realResult;
	}



	protected Set<Tree> constructMultipleOrbitExpressionNotOptimized(SparseNFA nfa,
			Set<HashSet<String>> sccs) throws NoOpportunityFoundException {
		Set<String> initialScc = BKWTools.getScc(sccs,nfa.getStateValue(nfa.getInitialState()));
		boolean epsilon = !eu.fox7.util.Collections.intersect(initialScc, BKWTools.finalStateValues(nfa)).isEmpty();
		//NFA initialAutomaton = new NFA(nfa);
		//initialAutomaton = createOrbitAutomaton(initialAutomaton,initialScc, initialAutomaton.getStateValue(initialAutomaton.getInitialState()));
		SparseNFA initialAutomaton =  BKWTools.createOrbitAutomaton(nfa,initialScc, nfa.getStateValue(nfa.getInitialState()));
		Set<Tree> initialRe = deterministicExpressionMinimal(initialAutomaton);
		if(initialRe == null)
			System.out.println("Received a null expression where I expected an exception to be thrown");
		Map<String,String> mapOutgoingConnections = BKWTools.outgoingConnections(nfa,initialScc);
		Map<String,Set<Tree>> mapResultingExpressions = new HashMap<String,Set<Tree>>();
		for(String symbol : mapOutgoingConnections.keySet()){
			SparseNFA nfaSymbol = new SparseNFA(nfa);
			BKWTools.setInitialStateClean(nfaSymbol, mapOutgoingConnections.get(symbol));
			Set<Tree> result = deterministicExpressionMinimal(nfaSymbol);
			if(result == null)
				System.out.println("Received a null expression where I expected an exception to be thrown");
			mapResultingExpressions.put(symbol, result);
		}
		Set<Tree> result = new HashSet<Tree>();
		result.addAll(composeMultipleOrbitExpressions(initialRe,mapResultingExpressions,epsilon));

		return result;
	}

	protected static String getGate(SparseNFA dfa, Set<String> scc) {
		for(String state : scc){
			if(dfa.isFinalState(state))
				return state;
			Set<State> outgoingStates = dfa.getNextStates(dfa.getState(state));
			for(State sOut : outgoingStates){
				if(!scc.contains(dfa.getStateValue(sOut)))
					return state;
			}
		}
		return null;
	}

	protected static Tree applyMorphism(Tree expression, Map<String,Tree> morphism){
		Map<Node,Node> nodeMap = new HashMap<Node,Node>();
		Tree result = new Tree(expression,nodeMap);

		for (Iterator<Node> it = expression.leaves(); it.hasNext(); ) {
			Node current = it.next();
			if(morphism.containsKey(current.key())){
				Tree clone = new Tree(morphism.get(current.key()));
				Node currentResult = nodeMap.get(current);
				int index = currentResult.getChildIndex();
				currentResult.getParent().addChild(index, clone.getRoot());
			}
		}

		return result;
	}

	/**
	 * @param result
	 * @param expressions
	 * @return
	 */
	private Set<Tree> composeMultipleOrbitExpressionOptimized(Tree tree,
			Map<String, Set<Tree>> expressionsMap) {
		Set<Tree> result = new HashSet<Tree>();
		if(repairBranchCriterion == NO_REPAIR || repairSelectFactor <= 1){
			Map<String,Tree> expressions = new HashMap<String, Tree>();
			for(String expression : expressionsMap.keySet())
				expressions.put(expression, eu.fox7.util.Collections.getOne(expressionsMap.get(expression)));
			result.add(applyMorphism(tree,expressions));
			return result;
		} 
		else {
			Set<Map<String, Tree>> expressionsMapSet = computeResulingExpressionsMaps(expressionsMap);
			for(Map<String, Tree> expressions : expressionsMapSet)
				result.add(applyMorphism(tree, expressions));
			return result;
		}
	}

	public Set<Tree> composeMultipleOrbitExpressions(Set<Tree> initialReSet,
			Map<String, Set<Tree>> resultingExpressionsSet, boolean epsilon) {
		if(repairBranchCriterion == NO_REPAIR || repairSelectFactor <= 1){
			Map<String, Tree> resultingExpressions = new HashMap<String, Tree>();
			for(String string : resultingExpressionsSet.keySet())
				resultingExpressions.put(string, eu.fox7.util.Collections.getOne(resultingExpressionsSet.get(string)));
			Set<Tree> result = new HashSet<Tree>();
			result.add(composeMultipleOrbitExpressions(eu.fox7.util.Collections.getOne(initialReSet),resultingExpressions,epsilon));
			return result;
		}
		else{
			Set<Tree> result = new HashSet<Tree>();
			Set<Map<String, Tree>> resultingExpressionsPossibilities = computeResulingExpressionsMaps(resultingExpressionsSet);
			for(Tree initialRe : initialReSet){
				for(Map<String, Tree> resultingExpressions : resultingExpressionsPossibilities)
					result.add(composeMultipleOrbitExpressions(initialRe,resultingExpressions,epsilon));
			}
			return result;
		}
	}

	Set<Tree> composeSingleOrbitExpressionsOptimized(Set<Tree> reWithoutSSet,
			Map<Tree,Set<Tree>> resultingExpressionsSet) {
		if(repairBranchCriterion == NO_REPAIR || repairSelectFactor <= 1){
			Set<Node> expressions = new HashSet<Node>();
			Tree reWithoutS = eu.fox7.util.Collections.getOne(reWithoutSSet);

			for(Tree tree : resultingExpressionsSet.keySet()){
				Tree resultingExpression = eu.fox7.util.Collections.getOne(resultingExpressionsSet.get(tree));
				expressions.add(BKWTools.concatenate(tree, resultingExpression));
			}
			Tree result = composeSingleOrbitExpressionsOptimized(reWithoutS, expressions);
			Set<Tree> resultSet = new HashSet<Tree>();
			resultSet.add(result);
			return resultSet;
		}
		else {
			Set<Tree> resultSet = new HashSet<Tree>();
			Set<Set<Node>> expressionsSet = computePossibleExpressions(resultingExpressionsSet);

			for(Tree reWithoutS : reWithoutSSet){
				for(Set<Node> expressions : expressionsSet)
					resultSet.add(composeSingleOrbitExpressionsOptimized(reWithoutS, expressions));
			}

			return resultSet;
		}
	}

	/**
	 * @param resultingExpressionsSet
	 * @return
	 */
	private Set<Set<Node>> computePossibleExpressions(
			Map<Tree, Set<Tree>> resultingExpressionsSet) {
		Set<Tree> toDo = new HashSet<Tree>(resultingExpressionsSet.keySet());
		Set<Node> expressions = new HashSet<Node>();
		return computePossibleExpressionsInductively(resultingExpressionsSet,expressions,toDo);
	}

	/**
	 * @param resultingExpressionsSet
	 * @param expressions
	 * @param toDo
	 * @return
	 */
	private Set<Set<Node>> computePossibleExpressionsInductively(
			Map<Tree, Set<Tree>> resultingExpressionsSet,
			Set<Node> expressions, Set<Tree> toDo) {
		Set<Set<Node>> result = new HashSet<Set<Node>>();
		if(toDo.isEmpty()){
			result.add(expressions);
			return result;
		}
		else{
			Tree current = eu.fox7.util.Collections.takeOne(toDo);
			Set<Tree> currentSet = resultingExpressionsSet.get(current);
			for(Tree resultingExpression : currentSet){
				Set<Node> expressionsNew = new HashSet<Node>(expressions);
				Set<Tree> toDoNew = new HashSet<Tree>(toDo);
				expressionsNew.add(BKWTools.concatenate(current, resultingExpression));
				result.addAll(computePossibleExpressionsInductively(resultingExpressionsSet, expressionsNew, toDoNew));
			}
			return result;
		}
	}

	public Set<Tree> composeSingleOrbitExpressions(Set<Tree> reWithoutSSet,
			Map<String, Set<Tree>> resultingExpressionsSet) {
		if(repairBranchCriterion == NO_REPAIR || repairSelectFactor <= 1){
			Map<String, Tree> resultingExpressions = new HashMap<String, Tree>();
			for(String string : resultingExpressionsSet.keySet())
				resultingExpressions.put(string, eu.fox7.util.Collections.getOne(resultingExpressionsSet.get(string)));
			Set<Tree> result = new HashSet<Tree>();
			result.add(composeSingleOrbitExpressions(eu.fox7.util.Collections.getOne(reWithoutSSet),resultingExpressions));
			return result;
		}
		else {
			Set<Tree> result = new HashSet<Tree>();
			Set<Map<String, Tree>> resultingExpressionsPossibilities = computeResulingExpressionsMaps(resultingExpressionsSet);
			for(Tree initialRe : reWithoutSSet){
				for(Map<String, Tree> resultingExpressions : resultingExpressionsPossibilities)
					result.add(composeSingleOrbitExpressions(initialRe,resultingExpressions));
			}
			return result;		
		}
	}

	public Tree composeMultipleOrbitExpressions(Tree initialRe,
			Map<String, Tree> mapResultingExpressions, boolean epsilon) {
		Tree re = new Tree();
		Node root = new Node(Regex.CONCAT_OPERATOR);
		re.setRoot(root);
		root.addChild(initialRe.getRoot());

		Set<Node> expressions = new HashSet<Node>();
		if(epsilon)
			expressions.add(new Node(Regex.EPSILON_SYMBOL));
		for(String symbol : mapResultingExpressions.keySet()){
			expressions.add(BKWTools.concatenate(symbol, mapResultingExpressions
					.get(symbol)));
		}
		root.addChild(BKWTools.createDisjunctionX(expressions));
		return re;
	}

	public Tree composeSingleOrbitExpressions(Tree reWithoutS,
			Map<String, Tree> resultingExpressions) {
		Tree re = new Tree();
		Node root = new Node(Regex.CONCAT_OPERATOR);
		re.setRoot(root);
		root.addChild(reWithoutS.getRoot());
		Node star = new Node(Regex.ZERO_OR_MORE_OPERATOR);
		root.addChild(star);

		Set<Node> expressions = new HashSet<Node>();
		for(String symbol : resultingExpressions.keySet()){
			expressions.add(BKWTools.concatenate(symbol, resultingExpressions
					.get(symbol)));
		}
		star.addChild(BKWTools.createDisjunctionX(expressions));
		return re;
	}



	/**
	 * @param resultingExpressionsSet
	 * @return
	 */
	private Set<Map<String, Tree>> computeResulingExpressionsMaps(
			Map<String, Set<Tree>> resultingExpressionsSet) {
		Set<String> toDo = new HashSet<String>(resultingExpressionsSet.keySet());

		Map<String,Tree> resultingExpressionsMap = new HashMap<String,Tree>();

		return computeResulingExpressionsMapsInductive(resultingExpressionsSet, resultingExpressionsMap,toDo);
	}

	/**
	 * @param resultingExpressionsSet
	 * @param resultingExpressionsMap
	 * @param toDo
	 * @return
	 */
	private Set<Map<String, Tree>> computeResulingExpressionsMapsInductive(
			Map<String, Set<Tree>> resultingExpressionsSet,
			Map<String, Tree> resultingExpressionsMap, Set<String> toDo) {
		if(toDo.isEmpty()){
			Set<Map<String, Tree>> result = new HashSet<Map<String,Tree>>();
			result.add(resultingExpressionsMap);
			return result;
		}
		else{

			Set<Map<String,Tree>> result = new HashSet<Map<String,Tree>>();
			String current = eu.fox7.util.Collections.takeOne(toDo);
			Set<Tree> currentSet = resultingExpressionsSet.get(current);

			for(Tree tree : currentSet){
				Map<String, Tree> resultingExpressions = new HashMap<String, Tree>(resultingExpressionsMap);
				//Tree newTree = new Tree(tree);
				resultingExpressions.put(current, tree);
				Set<String> newToDo = new HashSet<String>(toDo);
				result.addAll(computeResulingExpressionsMapsInductive(resultingExpressionsSet, resultingExpressions, newToDo));
			}

			return result;
		}
	}

	/**
	 * It adds transitions from the states in states, over symbol to
	 * toState, and returns a (possibly singleton) set of states which must be merged
	 * because of these additions.
	 * @param nfa
	 * @param symbol
	 * @param states
	 * @param toStateNew
	 * @return
	 */
	protected HashSet<String> addTransitions(SparseNFA nfa, String symbol,
			Set<String> states, String toStateNew) {
		HashSet<String> mergeSet = new HashSet<String>();
		for(String state : states){
			if(nfa.getTransitionMap().hasTransitionWith(nfa.getAlphabet().get(symbol), nfa.getState(state)))
				mergeSet.add(eu.fox7.util.Collections.getOne(nfa.getNextStateValues(symbol, state)));
			nfa.addTransition(symbol, state, toStateNew);
			if(globalNFA != null)
				globalNFA.addTransition(symbol, state, toStateNew);
		}
		return mergeSet;
	}

	/**
	 * @param nfa
	 * @param symbol
	 */
	protected void makeSymbolConsistent(SparseNFA nfa, String symbol) {
		Set<String> getFinalStates = BKWTools.finalStateValues(nfa);
		String toStateNew = "";
		boolean first = true;
		
		for(String state : getFinalStates){
			if(nfa.getTransitionMap().hasTransitionWith(nfa.getAlphabet().get(symbol), nfa.getState(state))){
				toStateNew = eu.fox7.util.Collections.getOne(nfa.getNextStateValues(symbol, state));
				break;
			}
		}
		
		HashSet<String> mergeSet = addTransitions(nfa, symbol, getFinalStates,toStateNew);
		
		DFAShrinker shrinker = new DFAShrinker();
		
		if(globalNFA != null)
			shrinker.mergeStates(nfa, mergeSet, false, globalNFA);
		else
			shrinker.mergeStates(nfa, mergeSet, false);
		
		
	}

	protected void makeProperStatesFinal(SparseNFA nfa, Set<HashSet<String>> sccs) {
		for(HashSet<String> scc : sccs){
			Set<String> gates = BKWTools.computeGates(nfa, scc);
			
			boolean containsFinal = false;
			for(String state : gates){
				if(nfa.isFinalState(state)){
					containsFinal = true;
					break;
				}	
			}
			
			if(containsFinal){
				for(String state : gates){
					nfa.addFinalState(state);
					if(globalNFA != null)
						globalNFA.addFinalState(state);
				}
			}
		}
	}

	/**
	 * @param nfa
	 */
	protected void forceOrbitProperty(SparseNFA nfa, Set<HashSet<String>> sccs) {
		
		makeProperStatesFinal(nfa, sccs);
		
		Set<HashSet<String>> mergeSet = new HashSet<HashSet<String>>();
		
		for(HashSet<String> scc : sccs){
			Set<String> gates = BKWTools.computeGates(nfa, scc);
			
			for(String symbol : nfa.getAlphabet().keySet()){
				String toState = null;
				for(String gate : gates){
					if(!nfa.getNextStateValues(symbol, gate).isEmpty()){
						String possibleToState = eu.fox7.util.Collections.getOne(nfa.getNextStateValues(symbol, gate));
						if(!scc.contains(possibleToState)){
							toState = possibleToState;
							break;
						}
					}
				}
				if(toState != null){
					HashSet<String> merge = addTransitions(nfa, symbol, gates, toState);
					if(merge.size() > 1)
						mergeSet.add(merge);
				}
			}
		}
		
		DFAShrinker shrinker = new DFAShrinker();
		if(globalNFA != null)
			shrinker.mergeStates(nfa, mergeSet, false, globalNFA);
		else
			shrinker.mergeStates(nfa, mergeSet, false);
		
		//nfa.minimize();
	}

}
