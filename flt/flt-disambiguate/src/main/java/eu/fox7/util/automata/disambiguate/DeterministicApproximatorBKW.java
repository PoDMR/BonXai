/**
 * 
 */
package eu.fox7.util.automata.disambiguate;

import eu.fox7.flt.automata.FeatureNotSupportedException;
import eu.fox7.flt.automata.converters.NFAMinimizer;
import eu.fox7.flt.automata.factories.sparse.Determinizer;
import eu.fox7.flt.automata.impl.sparse.SparseNFA;
import eu.fox7.flt.regex.Glushkov;
import eu.fox7.flt.regex.UnknownOperatorException;
import eu.fox7.flt.regex.generators.LanguageGenerator;
import eu.fox7.flt.regex.infer.rwr.NoOpportunityFoundException;
import eu.fox7.util.tree.SExpressionParseException;
import eu.fox7.util.tree.Tree;

import java.util.HashSet;
import java.util.Set;

/**
 * @author woutergelade
 *
 */
public class DeterministicApproximatorBKW implements DeterministicApproximator {
	

	DeterministicExpressionBKW bkw;
	
	private boolean iterate;
	
	public DeterministicApproximatorBKW() {
		bkw = new DeterministicExpressionBKW();
		iterate = false;
	}
	
	public DeterministicApproximatorBKW(boolean iterate) {
		bkw = new DeterministicExpressionBKW();
		this.iterate = iterate;
	}
	
	
	public DeterministicApproximatorBKW(boolean optimizeSingleOrbit,boolean optimizeMultipleOrbit) {
		bkw = new DeterministicExpressionBKW(optimizeSingleOrbit, optimizeMultipleOrbit);
	}
	
	public DeterministicApproximatorBKW(boolean optimizeSingleOrbit,boolean optimizeMultipleOrbit, boolean iterate) {
		bkw = new DeterministicExpressionBKW(optimizeSingleOrbit, optimizeMultipleOrbit);
		this.iterate = iterate;
	}
	
	
	public Set<Tree> deterministicApproximation(String regexStr)
			throws NoOpportunityFoundException, SExpressionParseException,
			UnknownOperatorException, FeatureNotSupportedException {
//		Glushkov g = new Glushkov();
		SparseNFA nfa = new SparseNFA(new LanguageGenerator(regexStr).getNFA());
//		nfa = SparseNFA.dfa(nfa).minimize();
		new NFAMinimizer().minimize(nfa);
		
		Set<Tree> result = new HashSet<Tree>();
		
		//DeterministicExpressionExploreTest.writeDot("beforeAhonen.dot", nfa);
		
		
		if(!BKWTools.isUnambiguous(nfa))
			nfa = bkw.ahonen(nfa);
		
		if(iterate){
			nfa = iterateAhonen(nfa);
		}
		
		//DeterministicExpressionExploreTest.writeDot("afterAhonen.dot", nfa);
		
		if(!BKWTools.isUnambiguous(nfa))
			throw new NoOpportunityFoundException("Even after ahonen, the dfa was still not deterministic",null);
		else
			result.add(bkw.deterministicExpression(nfa));
		
		return result;
	}

	public SparseNFA iterateAhonen(SparseNFA nfa) {
		nfa = Determinizer.dfa(nfa);
		new NFAMinimizer().minimize(nfa);
		while(!BKWTools.isUnambiguous(nfa))
			nfa = bkw.ahonen(nfa);
		return nfa;
	}

}
