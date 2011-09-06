/*
 * Created on Sep 6, 2008
 * Modified on $Date: 2009-10-26 15:29:20 $
 */
package eu.fox7.flt.regex.infer.rwr;

import eu.fox7.flt.regex.infer.rwr.impl.Automaton;

/**
 * @author eu.fox7
 * @version $Revision: 1.2 $
 * 
 */
public interface AutomatonRewriter {

    public Automaton rewrite(Automaton automaton, int[] indices);

}
