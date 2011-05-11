/*
 * Created on Feb 13, 2006
 * Modified on $Date: 2009-10-28 12:17:59 $
 */
package gjb.flt.automata.factories.sparse;

import gjb.flt.automata.NoSuchTransitionException;
import gjb.flt.automata.impl.sparse.AnnotatedSparseNFA;
import gjb.flt.automata.impl.sparse.AnnotatedStateNFA;
import gjb.flt.automata.impl.sparse.State;
import gjb.flt.automata.impl.sparse.StateNFA;
import gjb.flt.automata.impl.sparse.Transition;
import gjb.flt.automata.io.DefaultDotFormatter;
import gjb.flt.automata.io.DotFormatter;
import gjb.flt.regex.Glushkov;

/**
 * @author gjb
 * @version $Revision: 1.2 $
 * 
 */
public class KLAFactory extends AbstractIncrementalNFAFactory<AnnotatedSparseNFA<Integer,Integer>> {

    public static final String DEFAULT_PADDING_CHAR = "#";
    public static final String DEFAULT_SEPARATION_CHAR = "-";
    protected String paddingChar = DEFAULT_PADDING_CHAR;
    protected String separatorChar = DEFAULT_SEPARATION_CHAR;
    protected int k;

    public KLAFactory(int k) {
        this.k = k;
        nfa = new AnnotatedSparseNFA<Integer,Integer>();
        nfa.setInitialState(Glushkov.INITIAL_STATE);
    }

    @Override
    public KLAFactory newInstance() {
    	KLAFactory newFactory = new KLAFactory(k);
    	newFactory.setSeparatorChar(getSeparatorChar());
    	newFactory.setPaddingChar(getPaddingChar());
		return newFactory;
    }

    @Override
    public void add(String[] example) {
        String fromValue = nfa.getStateValue(nfa.getInitialState());
        for (int i = 0; i < example.length; i++) {
            String symbolValue = example[i];
            String toValue = getStateName(example, i);
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

    protected String getStateName(String[] tokens, int index) {
        StringBuilder name = new StringBuilder();
        name.append(tokens[index--]);
        int charCount = 1;
        for ( ; index >= 0 && charCount < getK(); index--, charCount++) {
            name.insert(0, getSeparatorChar());
            name.insert(0, tokens[index]);
        }
        for ( ; charCount < getK(); charCount++) {
            name.insert(0, getSeparatorChar());
            name.insert(0, getPaddingChar());
        }
        return name.toString();
    }

    protected String stripStateName(String name) {
        int index = name.lastIndexOf(getSeparatorChar());
        if (index >= 0)
            return name.substring(index + 1);
        else
            return name;
    }

    public static String strip(String name, String sep) {
        int index = name.lastIndexOf(sep);
        if (index >= 0)
            return name.substring(index + 1);
        else
            return name;
    }

    public static String strip(String name) {
        return strip(name, DEFAULT_SEPARATION_CHAR);
    }

    public DotFormatter getDotFormatter() {
        return new AnnotatedKLADotFormatter();
    }

    public String getPaddingChar() {
        return paddingChar;
    }

    public void setPaddingChar(String paddingChar) {
        this.paddingChar = paddingChar;
    }

    public String getSeparatorChar() {
        return separatorChar;
    }

    public void setSeparatorChar(String separatorChar) {
        this.separatorChar = separatorChar;
    }

    public int getK() {
        return k;
    }

    protected class AnnotatedKLADotFormatter extends DefaultDotFormatter {

    	@Override
        public String stateToDot(StateNFA nfa, State state) {
            StringBuffer str = new StringBuffer(INDENT);
            str.append(state.toString());
            str.append(ATTR_OPEN);
            str.append(createLabel(stripStateName(nfa.getStateValue(state))));
            str.append(ATTR_SEP);
            str.append(createAttr("shape", STATE_SHAPE));
            if (nfa.getFinalStates().contains(state)) {
                str.append(ATTR_SEP);
                str.append(createAttr("peripheries", "2"));
            }
            str.append(ATTR_CLOSE);
            str.append(LINE_END);
            return str.toString();
        }

        @Override
        public String transitionToDot(StateNFA nfa, Transition transition) {
            StringBuffer str = new StringBuffer(INDENT);
            str.append(createEdge(transition));
            str.append(ATTR_OPEN);
            str.append(createLabel(((AnnotatedStateNFA<?, ?>) nfa).getAnnotation(transition).toString()));
            str.append(ATTR_CLOSE);
            str.append(LINE_END);
            return str.toString();
        }

    }

}
