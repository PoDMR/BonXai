/*
 * Created on Sep 10, 2008
 * Modified on $Date: 2009-10-26 15:29:20 $
 */
package gjb.flt.regex.infer.rwr;

import gjb.flt.regex.infer.rwr.impl.Automaton;

public interface RewriteEngine {

    public String rewriteToRegex(Automaton automaton)
            throws NoOpportunityFoundException;

    public Automaton rewrite(Automaton automaton);

}