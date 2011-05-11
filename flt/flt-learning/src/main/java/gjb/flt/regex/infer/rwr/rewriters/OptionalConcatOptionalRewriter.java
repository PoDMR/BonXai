/*
 * Created on Sep 8, 2008
 * Modified on $Date: 2009-10-26 15:29:20 $
 */
package gjb.flt.regex.infer.rwr.rewriters;

import gjb.flt.regex.Regex;
import gjb.flt.regex.infer.rwr.impl.Automaton;

/**
 * @author gjb
 * @version $Revision: 1.1 $
 * 
 */
public class OptionalConcatOptionalRewriter extends ConcatRewriter {

    @Override
    protected String makeLabel(Automaton automaton, int i, int j) {
        return Regex.LEFT_BRACKET + Regex.CONCAT_OPERATOR + " " +
            Regex.LEFT_BRACKET + Regex.ZERO_OR_ONE_OPERATOR + " " +
            automaton.getLabel(i) + Regex.RIGHT_BRACKET + " " +
            Regex.LEFT_BRACKET + Regex.ZERO_OR_ONE_OPERATOR + " " +
            automaton.getLabel(j) + Regex.RIGHT_BRACKET + Regex.RIGHT_BRACKET;
    }

}
