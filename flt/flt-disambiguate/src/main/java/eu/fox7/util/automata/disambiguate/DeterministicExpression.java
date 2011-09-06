/**
 * 
 */
package eu.fox7.util.automata.disambiguate;

import eu.fox7.flt.automata.FeatureNotSupportedException;
import eu.fox7.flt.automata.impl.sparse.SparseNFA;
import eu.fox7.flt.regex.UnknownOperatorException;
import eu.fox7.flt.regex.infer.rwr.NoOpportunityFoundException;
import eu.fox7.util.tree.SExpressionParseException;
import eu.fox7.util.tree.Tree;

/**
 * @author woutergelade
 *
 */
public interface DeterministicExpression {
	/**
	 * Depending on the implementation, it returns a deterministic expression
	 * which is either equivalent to the given nfa, or an overapproximation.
	 * If it fails to find an expression up to its standards, it throws a
	 * NoOpportunityFoundException.
	 * @param nfa
	 * @return
	 * @throws FeatureNotSupportedException 
	 * @throws UnknownOperatorException 
	 * @throws SExpressionParseException 
	 */
	public Tree deterministicExpression(String regexStr) throws NoOpportunityFoundException, SExpressionParseException, UnknownOperatorException, FeatureNotSupportedException;
	public Tree deterministicExpression(SparseNFA nfa) throws NoOpportunityFoundException, SExpressionParseException, UnknownOperatorException, FeatureNotSupportedException;

}
