/*
 * Created on Oct 23, 2007
 * Modified on $Date: 2009-10-27 14:14:01 $
 */
package eu.fox7.flt.regex.measures;

import eu.fox7.flt.automata.FeatureNotSupportedException;
import eu.fox7.flt.regex.UnknownOperatorException;
import eu.fox7.util.tree.SExpressionParseException;

/**
 * @author eu.fox7
 * @version $Revision: 1.1 $
 * 
 */
public interface LanguageMeasure {

    public double compute(String regexStr)
        throws SExpressionParseException, UnknownOperatorException,
               FeatureNotSupportedException;

}
