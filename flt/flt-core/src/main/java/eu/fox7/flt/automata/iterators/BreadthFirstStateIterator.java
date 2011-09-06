/*
 * Created on Apr 13, 2008
 * Modified on $Date: 2009-11-12 09:10:15 $
 */
package eu.fox7.flt.automata.iterators;

import eu.fox7.flt.automata.impl.sparse.State;
import eu.fox7.flt.automata.impl.sparse.StateNFA;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 * @author eu.fox7
 * @version $Revision: 1.2 $
 * 
 */
public class BreadthFirstStateIterator implements Iterator<State> {

    protected StateNFA nfa;
    protected List<State> toDo = new LinkedList<State>();
    protected Set<State> done = new HashSet<State>();
    
    public BreadthFirstStateIterator(StateNFA nfa) {
        super();
        this.nfa = nfa;
        toDo.add(nfa.getInitialState());
    }

    /* (non-Javadoc)
     * @see java.util.Iterator#hasNext()
     */
    public boolean hasNext() {
        return !toDo.isEmpty();
    }

    /* (non-Javadoc)
     * @see java.util.Iterator#next()
     */
    public State next() {
        if (hasNext()) {
            State state = toDo.get(0);
            toDo.remove(0);
            done.add(state);
            for (State nextState : nfa.getNextStates(state))
                if (!done.contains(nextState) && !toDo.contains(nextState))
                    toDo.add(nextState);
            return state;
        } else
            throw new NoSuchElementException();
    }

    /* (non-Javadoc)
     * @see java.util.Iterator#remove()
     */
    public void remove() {}

}
