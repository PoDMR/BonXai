/*
 * Created on Oct 29, 2007
 * Modified on $Date: 2009-10-27 14:14:01 $
 */
package eu.fox7.flt.regex.converters;

import eu.fox7.flt.regex.Regex;
import eu.fox7.flt.regex.UnknownOperatorException;
import eu.fox7.util.tree.SExpressionParseException;

/**
 * @author eu.fox7
 * @version $Revision: 1.1 $
 * 
 */
public interface Converter {

    public String convert(Regex regex) throws ConversionException;
    public String convert(String regexStr)
        throws SExpressionParseException, UnknownOperatorException,
               ConversionException;

}
