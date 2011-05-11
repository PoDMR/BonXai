/*
 * Created on Sep 8, 2008
 * Modified on $Date: 2009-10-27 09:30:13 $
 */
package gjb.flt.regex.infer.rwr;

import gjb.flt.regex.infer.InferenceException;
import gjb.flt.regex.infer.rwr.impl.Automaton;

/**
 * @author gjb
 * @version $Revision: 1.3 $
 * 
 */
public class NoOpportunityFoundException extends InferenceException {

    private static final long serialVersionUID = -8637958651292994895L;
    protected Automaton automaton;

    public NoOpportunityFoundException(String msg, Automaton automaton) {
        super(msg);
        this.automaton = automaton;
    }

    public Automaton getAutomaton() {
        return automaton;
    }

}
