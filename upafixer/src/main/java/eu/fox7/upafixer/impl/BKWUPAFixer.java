package eu.fox7.upafixer.impl;

import gjb.flt.automata.FeatureNotSupportedException;
import gjb.flt.regex.Regex;
import gjb.flt.regex.UnknownOperatorException;
import gjb.flt.regex.infer.rwr.NoOpportunityFoundException;
import gjb.flt.treeautomata.impl.ContentAutomaton;
import gjb.util.automata.disambiguate.DeterministicExpression;
import gjb.util.automata.disambiguate.DeterministicExpressionBKW;
import gjb.util.tree.SExpressionParseException;
import gjb.util.tree.Tree;

public class BKWUPAFixer extends AbstractUPAFixer {

	@Override
	public Regex fixUPA(ContentAutomaton contentAutomaton) {
		DeterministicExpression deFactory = new DeterministicExpressionBKW();
		Tree tree;
		try {
			tree = deFactory.deterministicExpression(contentAutomaton);
			return new Regex(tree);
		} catch (NoOpportunityFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (UnknownOperatorException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (FeatureNotSupportedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (SExpressionParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return null;
	}

}
