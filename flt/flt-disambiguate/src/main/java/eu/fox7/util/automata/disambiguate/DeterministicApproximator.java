package eu.fox7.util.automata.disambiguate;

import java.util.Set;

import eu.fox7.flt.automata.FeatureNotSupportedException;
import eu.fox7.flt.regex.UnknownOperatorException;
import eu.fox7.flt.regex.infer.rwr.NoOpportunityFoundException;
import eu.fox7.util.tree.SExpressionParseException;
import eu.fox7.util.tree.Tree;


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
