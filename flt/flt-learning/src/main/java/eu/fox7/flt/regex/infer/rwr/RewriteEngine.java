/*
 * Created on Sep 10, 2008
 * Modified on $Date: 2009-10-26 15:29:20 $
 */
package eu.fox7.flt.regex.infer.rwr;

import eu.fox7.flt.regex.infer.rwr.impl.Automaton;

public interface RewriteEngine {

    public String rewriteToRegex(Automaton automaton)
            throws NoOpportunityFoundException;

    public Automaton rewrite(Automaton automaton);

}