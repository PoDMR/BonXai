package gjb.util.automata.disambiguate;

import java.util.Set;

import gjb.flt.automata.FeatureNotSupportedException;
import gjb.flt.regex.UnknownOperatorException;
import gjb.flt.regex.infer.rwr.NoOpportunityFoundException;
import gjb.util.tree.SExpressionParseException;
import gjb.util.tree.Tree;

public interface DeterministicApproximator {
	/**
	 * It returns a set of deterministic expressions, each 
	 * overapproximating the given nfa.
	 * @param nfa
	 * @return
	 * @throws NoOpportunityFoundException 
	 * @throws FeatureNotSupportedException 
	 * @throws UnknownOperatorException 
	 * @throws SExpressionParseException 
	 */
	public Set<Tree> deterministicApproximation(String regexStr) throws NoOpportunityFoundException, SExpressionParseException, UnknownOperatorException, FeatureNotSupportedException;
}
