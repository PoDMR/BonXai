/*
 * Created on Sep 6, 2008
 * Modified on $Date: 2009-10-26 15:29:20 $
 */
package gjb.flt.regex.infer.rwr;

import gjb.flt.regex.infer.rwr.impl.Automaton;

/**
 * @author gjb
 * @version $Revision: 1.2 $
 * 
 */
public interface AutomatonRewriter {

    public Automaton rewrite(Automaton automaton, int[] indices);

}
