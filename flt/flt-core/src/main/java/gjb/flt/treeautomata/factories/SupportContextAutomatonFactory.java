/**
 * Created on Oct 28, 2009
 * Modified on $Date: 2009-11-12 09:10:15 $
 */
package gjb.flt.treeautomata.factories;

import gjb.flt.FLTRuntimeException;
import gjb.flt.automata.NoSuchStateException;
import gjb.flt.automata.NoSuchTransitionException;
import gjb.flt.automata.NotDFAException;
import gjb.flt.automata.impl.sparse.State;
import gjb.flt.automata.impl.sparse.Symbol;
import gjb.flt.automata.impl.sparse.Transition;
import gjb.flt.regex.Glushkov;
import gjb.flt.treeautomata.impl.ContextAutomaton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author lucg5005
 * @version $Revision: 1.5 $
 *
 */
public class SupportContextAutomatonFactory {

    public static final String DEFAULT_SEPARATION_CHAR = "_";
    public static final String DEFAULT_ROOT_SYMBOL = "_|_";
    protected String separatorChar = DEFAULT_SEPARATION_CHAR;
    protected String rootSymbol = DEFAULT_ROOT_SYMBOL;
    protected Map<String,Integer> stateCounter = new HashMap<String,Integer>();
	protected ContextAutomaton contextAutomaton;
	protected ContentAutomatonFactory contentFactoryTemplate;
	protected Map<String,ContentAutomatonFactory> contentFactoryMap = new HashMap<String,ContentAutomatonFactory>();

	public SupportContextAutomatonFactory(ContentAutomatonFactory contentFactory) {
		super();
		this.contentFactoryTemplate = contentFactory;
		this.contextAutomaton = new ContextAutomaton();
		contextAutomaton.setInitialState(Glushkov.INITIAL_STATE);
	}

	public String getSeparatorChar() {
        return separatorChar;
    }

	public void setSeparatorChar(String separatorChar) {
        this.separatorChar = separatorChar;
    }

	public String getRootSymbol() {
        return rootSymbol;
    }

	public void setRootSymbol(String rootSymbol) {
        this.rootSymbol = rootSymbol;
    }

	public void add(String[] ancStr, String[] childStr) {
        Set<Transition> counted = new HashSet<Transition>();
        boolean shouldCount = childStr.length == 0;
        String fromValue = contextAutomaton.getStateValue(contextAutomaton.getInitialState());
        List<State> path = new ArrayList<State>();
        for (int i = 0; i < ancStr.length; i++) {
            String symbolValue = ancStr[i];
            Symbol symbol = Symbol.create(symbolValue);
            try {
                String toValue = null;
                if (!contextAutomaton.hasNextStates(symbol, contextAutomaton.getState(fromValue))) {
                    toValue = getNewStateName(symbolValue);
                    contextAutomaton.addTransition(symbolValue, fromValue, toValue);
                    contextAutomaton.annotate(symbolValue, fromValue, toValue, 0);
                } else {
                    try {
                        State toState = contextAutomaton.getNextState(symbol, contextAutomaton.getState(fromValue));
                        toValue = contextAutomaton.getStateValue(toState);
                    } catch (NotDFAException e) {
                        throw new RuntimeException(e);
                    }
                }
                Transition transition = new Transition(symbol, contextAutomaton.getState(fromValue), contextAutomaton.getState(toValue));
                if (shouldCount && !counted.contains(transition)) {
                    contextAutomaton.annotate(symbolValue, fromValue, toValue,
                                 contextAutomaton.getAnnotation(symbolValue, fromValue, toValue) + 1);
                    counted.add(transition);
                }
                fromValue = toValue;
                path.add(contextAutomaton.getState(toValue));
            } catch (NoSuchTransitionException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
        if (childStr.length == 0)
        	contextAutomaton.addFinalState(fromValue);
        if (!contentFactoryMap.containsKey(fromValue))
        	contentFactoryMap.put(fromValue, (ContentAutomatonFactory) contentFactoryTemplate.newInstance());
        contentFactoryMap.get(fromValue).add(childStr);
    }

    public String getNewStateName(String stateValue) {
        if (!stateCounter.containsKey(stateValue))
            stateCounter.put(stateValue, 1);
        int index = stateCounter.get(stateValue);
        stateCounter.put(stateValue, index + 1);
        return stateValue + getSeparatorChar() + index;
    }

    public ContextAutomaton getAutomaton() {
    	annotate();
    	return contextAutomaton;
    }

    protected void annotate() {
    	for (String stateValue : contextAutomaton.getStateValues()) {
    		ContentAutomatonFactory contentFactory = contentFactoryMap.get(stateValue);
    		try {
	            contextAutomaton.annotate(stateValue, contentFactory.getAutomaton());
            } catch (NoSuchStateException e) {
            	throw new FLTRuntimeException("this should not happen", e);
            }
    	}
    }

    public String stripStateValue(String name) {
        return stripValue(name, getSeparatorChar());
    }

    public static String stripValue(String name, String sep) {
        int index = name.lastIndexOf(sep);
        if (index > 0)
            return name.substring(0, index);
        else
            return name;
    }

}
