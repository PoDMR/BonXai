/*
 * Created on Oct 23, 2007
 * Modified on $Date: 2009-10-27 14:14:01 $
 */
package gjb.flt.regex.measures;

import gjb.flt.automata.FeatureNotSupportedException;
import gjb.flt.regex.UnknownOperatorException;
import gjb.util.tree.SExpressionParseException;

/**
 * @author gjb
 * @version $Revision: 1.1 $
 * 
 */
public interface RelativeLanguageMeasure {

    public double compute(String regexStr1, String regexStr2)
        throws SExpressionParseException, UnknownOperatorException,
               FeatureNotSupportedException;

}
