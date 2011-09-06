/*
 * Created on Aug 27, 2008
 * Modified on $Date: 2009-10-27 14:14:01 $
 */
package eu.fox7.flt.regex.random;

import eu.fox7.util.tree.SExpressionParseException;

/**
 * @author eu.fox7
 * @version $Revision: 1.1 $
 * 
 */
public interface Mutator {

    public String mutate(String regexStr)
        throws SExpressionParseException, NoMutationFoundException;

}
