/**
 * 
 */
package gjb.util.automata.disambiguate;

import gjb.flt.automata.FeatureNotSupportedException;
import gjb.flt.regex.Glushkov;
import gjb.flt.automata.converters.NFAMinimizer;
import gjb.flt.automata.factories.sparse.Determinizer;
import gjb.flt.automata.impl.sparse.SparseNFA;
import gjb.flt.regex.UnknownOperatorException;
import gjb.flt.regex.generators.LanguageGenerator;
import gjb.flt.regex.infer.rwr.NoOpportunityFoundException;
import gjb.util.tree.SExpressionParseException;
import gjb.util.tree.Tree;

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
