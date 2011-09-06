/*
 * Created on Jul 5, 2006
 * Modified on $Date: 2009-10-27 14:14:01 $
 */
package eu.fox7.flt.regex.converters;

import eu.fox7.flt.regex.Regex;
import eu.fox7.flt.regex.RegexException;
import eu.fox7.util.tree.SExpressionParseException;

import javax.xml.transform.TransformerConfigurationException;

/**
 * @author eu.fox7
 * @version $Revision: 1.1 $
 * 
 */
public class ZeroOrMoreEliminator extends Transformer {

    public static final String TRANSFORMATION_PATH = "transformations";
    protected static final String[] transformerNames = {
            "eliminate-zeroOrMore.xslt"
    };
    
    public ZeroOrMoreEliminator() throws TransformerConfigurationException {
        super(TRANSFORMATION_PATH, transformerNames);
    }

    public String eliminate(String regexStr)
            throws SExpressionParseException, RegexException {
        return transform(regexStr);
    }

    public String eliminate(Regex regex)
            throws SExpressionParseException, RegexException {
        return transform(regex);
    }

}
