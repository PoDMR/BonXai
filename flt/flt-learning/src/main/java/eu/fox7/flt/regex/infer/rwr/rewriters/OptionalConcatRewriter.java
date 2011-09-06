/*
 * Created on Sep 7, 2008
 * Modified on $Date: 2009-10-26 15:29:20 $
 */
package eu.fox7.flt.regex.infer.rwr.rewriters;

import eu.fox7.flt.regex.Regex;
import eu.fox7.flt.regex.infer.rwr.impl.Automaton;

/**
 * @author eu.fox7
 * @version $Revision: 1.1 $
 * 
 */
public class OptionalConcatRewriter extends ConcatRewriter {

    @Override
    protected String makeLabel(Automaton automaton, int i, int j) {
        return Regex.LEFT_BRACKET + Regex.CONCAT_OPERATOR + " " +
            Regex.LEFT_BRACKET + Regex.ZERO_OR_ONE_OPERATOR + " " +
            automaton.getLabel(i) + Regex.RIGHT_BRACKET + " " +
            automaton.getLabel(j) + Regex.RIGHT_BRACKET;
    }

}
