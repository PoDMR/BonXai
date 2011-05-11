/*
 * Created on Oct 22, 2007
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
public interface SampleMeasure {

    public double compute(String regex, String[][] sample)
        throws SExpressionParseException, FeatureNotSupportedException,
        UnknownOperatorException;

}
