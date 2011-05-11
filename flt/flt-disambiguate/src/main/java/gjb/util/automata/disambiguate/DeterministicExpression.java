/**
 * 
 */
package gjb.util.automata.disambiguate;

import gjb.flt.automata.FeatureNotSupportedException;
import gjb.flt.automata.impl.sparse.SparseNFA;
import gjb.flt.regex.UnknownOperatorException;
import gjb.flt.regex.infer.rwr.NoOpportunityFoundException;
import gjb.util.tree.SExpressionParseException;
import gjb.util.tree.Tree;

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
