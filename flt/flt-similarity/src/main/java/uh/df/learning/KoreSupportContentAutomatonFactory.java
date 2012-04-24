package uh.df.learning;

import eu.fox7.flt.automata.NoSuchTransitionException;
import eu.fox7.flt.automata.factories.sparse.AbstractIncrementalNFAFactory;
import eu.fox7.flt.automata.factories.sparse.Determinizer;
import eu.fox7.flt.automata.impl.sparse.SparseNFA;
import eu.fox7.flt.automata.impl.sparse.State;
import eu.fox7.flt.automata.impl.sparse.Symbol;
import eu.fox7.flt.automata.impl.sparse.Transition;
import eu.fox7.flt.treeautomata.factories.ContentAutomatonFactory;
import eu.fox7.flt.treeautomata.impl.ContentAutomaton;
import gjb.util.hmm.Learner;
import gjb.util.hmm.ObservationSequencesFactory;
import gjb.util.hmm.Pomm;
import gjb.util.hmm.PommConverter;
import gjb.util.hmm.PommFactory;
import gjb.util.hmm.RandomPommFactory;
import gjb.util.hmm.Learner.NullResult;
import gjb.util.hmm.Learner.Result;
import gjb.util.hmm.ObservationSequencesFactory.SequencesInformation;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * 
 * @author Dominique Fonteyn
 *
 */
public class KoreSupportContentAutomatonFactory extends AbstractIncrementalNFAFactory<ContentAutomaton> implements
		ContentAutomatonFactory {

	List<String[]> samples;

	final int minStatesPerSymbol = 1;

	final int maxStatesPerSymbol = 3;

	final int maxEnsemble = 10;

	final int maxIterations = 50;

	Set<String[]> sampleCheckSet;

	/**
	 * 
	 */
	public KoreSupportContentAutomatonFactory() {
		super();
		samples = new ArrayList<String[]>();
		sampleCheckSet = new HashSet<String[]>();
	}

	@Override
	/**
	 * 
	 */
	public ContentAutomaton getAutomaton() {
		if (super.nfa == null)
			inferAutomaton();
		return super.getAutomaton();
	}

	@Override
	/**
	 * 
	 */
	public AbstractIncrementalNFAFactory<ContentAutomaton> newInstance() {
		return new KoreSupportContentAutomatonFactory();
	}

	@Override
	/**
	 * 
	 */
	public void add(String[] sample) {
		samples.add(sample);
		sampleCheckSet.add(sample);
	}

	/**
	 * 
	 */
	protected void inferAutomaton() {
		// optimization 
		if (sampleCheckSet.size() == 1 && sampleCheckSet.iterator().next().length == 0) {
			samples.clear();
			samples.addAll(sampleCheckSet);
		}

		String[][] corpus = new String[samples.size()][];
		int i = 0;
		for (Iterator<String[]> it = samples.iterator(); it.hasNext();) {
			corpus[i++] = it.next();
		}

		SequencesInformation info = new ObservationSequencesFactory().createSequences(corpus); 
		Learner learner = new Learner(); // FastLearner
		Result result = learner.learnRegex(info, minStatesPerSymbol, maxStatesPerSymbol, maxEnsemble, maxIterations);

		SparseNFA nfa = null;
		if (result instanceof NullResult) {
			PommFactory factory = new RandomPommFactory();
			Pomm pomm = factory.learnScaled(info, 1);
			nfa = PommConverter.convertToNFA(pomm, info);
		} else
			nfa = Determinizer.dfa(result.getNFA());
		
		ContentAutomaton ca = new ContentAutomaton();
		ca.setInitialState(nfa.getInitialStateValue());
		for (String value : nfa.getStateValues())
			ca.addState(value);
		for (String value : nfa.getFinalStateValues())
			ca.addFinalState(value);
		for (String symbol : nfa.getSymbolValues())
			ca.addSymbol(symbol);
		for (Transition transition : nfa.getTransitionMap().getTransitions()) {
			ca.addTransition(transition.getSymbol().toString(), nfa.getStateValue(transition.getFromState()), nfa
					.getStateValue(transition.getToState()));
		}

		// add supports
		for (String[] sample : corpus) {
			State fromState = ca.getInitialState();
			for (i = 0; i < sample.length; i++) {
				Symbol symbol = Symbol.create(sample[i]);
				State toState = null;
				for (Transition s : ca.getOutgoingTransitions(fromState)) {
					if (s.getSymbol().toString().equals(symbol.toString())) {
						toState = s.getToState();
						break;
					}
				}

				if (toState == null)
					throw new RuntimeException("this shouldn't happen");

				try {
					if (ca.hasAnnotation(symbol, fromState, toState))
						ca.annotate(symbol, fromState, toState, ca.getAnnotation(symbol, fromState, toState) + 1);
					else
						ca.annotate(symbol, fromState, toState, 0);
					fromState = toState;
				} catch (NoSuchTransitionException e) {
					e.printStackTrace();
					throw new RuntimeException(e);
				}
			}
			if (ca.getFinalStates().contains(fromState)) {
				if (ca.hasAnnotation(fromState))
					ca.annotate(fromState, ca.getAnnotation(fromState) + 1);
				else
					ca.annotate(fromState, 1);
			}
		}

		super.nfa = ca;
	}

}
