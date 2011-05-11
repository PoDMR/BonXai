/*
 * Created on Oct 29, 2007
 * Modified on $Date: 2009-10-27 14:14:01 $
 */
package gjb.flt.regex.converters;

import gjb.flt.regex.Regex;
import gjb.flt.regex.UnknownOperatorException;
import gjb.util.tree.SExpressionParseException;

/**
 * @author gjb
 * @version $Revision: 1.1 $
 * 
 */
public interface Converter {

    public String convert(Regex regex) throws ConversionException;
    public String convert(String regexStr)
        throws SExpressionParseException, UnknownOperatorException,
               ConversionException;

}
