package eu.fox7.upafixer.impl;

import eu.fox7.flt.automata.FeatureNotSupportedException;
import eu.fox7.flt.automata.impl.sparse.SparseNFA;
import eu.fox7.flt.automata.impl.sparse.StateNFA;
import eu.fox7.flt.regex.Regex;
import eu.fox7.flt.regex.UnknownOperatorException;
import eu.fox7.flt.regex.infer.rwr.NoOpportunityFoundException;
import eu.fox7.flt.treeautomata.impl.ContentAutomaton;
import eu.fox7.schematoolkit.SchemaToolkitException;
import eu.fox7.util.automata.disambiguate.DeterministicExpression;
import eu.fox7.util.automata.disambiguate.DeterministicExpressionBKW;
import eu.fox7.util.tree.SExpressionParseException;
import eu.fox7.util.tree.Tree;

public class BKWUPAFixer extends AbstractUPAFixer {

	@SuppressWarnings("rawtypes")
	@Override
	public Regex fixUPA(SparseNFA contentAutomaton) throws SchemaToolkitException {
		DeterministicExpression deFactory = new DeterministicExpressionBKW();
		Tree tree;
		try {
			tree = deFactory.deterministicExpression(contentAutomaton);
			return new Regex(tree);
		} catch (NoOpportunityFoundException e) {
			throw new SchemaToolkitException(e);
		} catch (UnknownOperatorException e) {
			throw new SchemaToolkitException(e);
		} catch (FeatureNotSupportedException e) {
			throw new SchemaToolkitException(e);
		} catch (SExpressionParseException e) {
			throw new SchemaToolkitException(e);
		}
	}

}
