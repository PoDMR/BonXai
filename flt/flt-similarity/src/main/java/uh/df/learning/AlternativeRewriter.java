package uh.df.learning;

import gjb.flt.regex.Regex;
import gjb.flt.regex.infer.rwr.AutomatonRewriter;
import gjb.flt.regex.infer.rwr.NoOpportunityFoundException;
import gjb.flt.regex.infer.rwr.RewriteEngine;
import gjb.flt.regex.infer.rwr.impl.Automaton;
import gjb.flt.regex.infer.rwr.rewriters.ConcatFinder;
import gjb.flt.regex.infer.rwr.rewriters.ConcatOptionalFinder;
import gjb.flt.regex.infer.rwr.rewriters.ConcatOptionalRewriter;
import gjb.flt.regex.infer.rwr.rewriters.ConcatRewriter;
import gjb.flt.regex.infer.rwr.rewriters.DisjunctionFinder;
import gjb.flt.regex.infer.rwr.rewriters.DisjunctionRewriter;
import gjb.flt.regex.infer.rwr.rewriters.OpportunityFinder;
import gjb.flt.regex.infer.rwr.rewriters.OptionalConcatFinder;
import gjb.flt.regex.infer.rwr.rewriters.OptionalConcatOptionalFinder;
import gjb.flt.regex.infer.rwr.rewriters.OptionalConcatOptionalRewriter;
import gjb.flt.regex.infer.rwr.rewriters.OptionalConcatRewriter;
import gjb.flt.regex.infer.rwr.rewriters.OptionalFinder;
import gjb.flt.regex.infer.rwr.rewriters.OptionalRewriter;
import gjb.flt.regex.infer.rwr.rewriters.RepetitionFinder;
import gjb.flt.regex.infer.rwr.rewriters.RepetitionRewriter;
import gjb.util.tree.SExpressionParseException;
import gjb.util.tree.Tree;

/**
 * 
 * @author Dominique Fonteyn
 *
 */
public class AlternativeRewriter implements RewriteEngine {

	protected OpportunityFinder repetitionFinder = new RepetitionFinder();
	protected OpportunityFinder[] finders = new OpportunityFinder[] { new DisjunctionFinder(), repetitionFinder,
			new ConcatFinder(), new ConcatOptionalFinder(), new OptionalConcatFinder(),
			new OptionalConcatOptionalFinder() };
	protected OpportunityFinder optionalFinder = new OptionalFinder();
	protected AutomatonRewriter repetitionRewriter = new RepetitionRewriter();
	protected AutomatonRewriter[] rewriters = new AutomatonRewriter[] { new DisjunctionRewriter(), repetitionRewriter,
			new ConcatRewriter(), new ConcatOptionalRewriter(), new OptionalConcatRewriter(),
			new OptionalConcatOptionalRewriter() };
	protected AutomatonRewriter optionalRewriter = new OptionalRewriter();

	public String rewriteToRegex(Automaton automaton) throws NoOpportunityFoundException {
		if (automaton.getNumberOfStates() == 1)
			if (automaton.acceptsEpsilon())
				return Regex.LEFT_BRACKET + Regex.EPSILON_SYMBOL + Regex.RIGHT_BRACKET;
			else
				return Regex.LEFT_BRACKET + Regex.EMPTY_SYMBOL + Regex.RIGHT_BRACKET;
		Automaton newAutomaton = rewrite(automaton);
		if (newAutomaton.isReduced())
			return unmark(newAutomaton.getLabel(0));
		else
			throw new NoOpportunityFoundException("could not be reduced", newAutomaton);
	}

	public Automaton rewrite(Automaton automaton) {
		while (automaton.getNumberOfStates() > 2) {
			int[] indices = null;
			for (int i = 0; i < finders.length; i++) {
				indices = finders[i].getFirst(automaton);
				if (indices != null) {
					automaton = rewriters[i].rewrite(automaton, indices);
					break;
				}
			}
			if (indices == null)
				return automaton;
		}
		int[] indices = repetitionFinder.getFirst(automaton);
		if (indices != null)
			automaton = repetitionRewriter.rewrite(automaton, indices);
		indices = optionalFinder.getFirst(automaton);
		if (indices != null)
			automaton = optionalRewriter.rewrite(automaton, indices);
		return automaton;
	}

	protected String unmark(String regexStr) {
		try {
			if (regexStr.contains("image_capture"))
				System.out.println();
			Regex regex = new Regex();
			Tree tree = regex.getTree(regexStr);
			return tree.toSExpression();
		} catch (SExpressionParseException e) {
			e.printStackTrace();
			throw new RuntimeException("unexpected exception", e);
		}
	}

}
