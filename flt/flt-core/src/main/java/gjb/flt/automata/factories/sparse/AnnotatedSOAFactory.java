/*
 * Created on Feb 7, 2006
 * Modified on $Date: 2009-10-28 12:17:59 $
 */
package gjb.flt.automata.factories.sparse;

import gjb.flt.automata.NoSuchTransitionException;
import gjb.flt.automata.impl.sparse.AnnotatedSparseNFA;
import gjb.flt.regex.Glushkov;

/**
 * @author gjb
 * @version $Revision: 1.2 $
 * 
 */
public class AnnotatedSOAFactory
        extends AbstractIncrementalNFAFactory<AnnotatedSparseNFA<Integer,Integer>> {

    public AnnotatedSOAFactory() {
        super();
        nfa = new AnnotatedSparseNFA<Integer,Integer>();
        nfa.setInitialState(Glushkov.INITIAL_STATE);
    }

    public AnnotatedSOAFactory newInstance() {
    	return new AnnotatedSOAFactory();
    }

    @Override
    public void add(String[] example) {
        String fromValue = nfa.getStateValue(nfa.getInitialState());
        for (int i = 0; i < example.length; i++) {
            String symbolValue = example[i];
            String toValue = example[i];
            try {
                if (!nfa.hasTransition(symbolValue, fromValue, toValue)) {
                    nfa.addTransition(symbolValue, fromValue, toValue);
                    nfa.annotate(symbolValue, fromValue, toValue, 0);
                }
                nfa.annotate(symbolValue, fromValue, toValue,
                             nfa.getAnnotation(symbolValue, fromValue, toValue) + 1);
                fromValue = toValue;
            } catch (NoSuchTransitionException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
        nfa.addFinalState(fromValue);
    }

}
