/*
 * Created on Aug 27, 2008
 * Modified on $Date: 2009-10-27 14:14:01 $
 */
package gjb.flt.regex.random;

import gjb.util.tree.SExpressionParseException;

/**
 * @author gjb
 * @version $Revision: 1.1 $
 * 
 */
public interface Mutator {

    public String mutate(String regexStr)
        throws SExpressionParseException, NoMutationFoundException;

}
