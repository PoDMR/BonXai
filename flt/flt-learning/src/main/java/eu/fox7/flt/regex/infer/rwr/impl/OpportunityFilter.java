/*
 * Created on Nov 21, 2008
 * Modified on $Date: 2009-10-27 09:30:13 $
 */
package eu.fox7.flt.regex.infer.rwr.impl;


/**
 * @author eu.fox7
 * @version $Revision: 1.1 $
 * 
 */
public interface OpportunityFilter {

    public boolean isPassed(Opportunity opportunity, Automaton automaton);

}
