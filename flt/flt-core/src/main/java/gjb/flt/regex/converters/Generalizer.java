/*
 * Created on Nov 24, 2005
 * Modified on $Date: 2009-10-27 14:14:01 $
 */
package gjb.flt.regex.converters;

import gjb.flt.regex.Regex;
import gjb.flt.regex.RegexException;
import gjb.util.tree.SExpressionParseException;

import javax.xml.transform.TransformerConfigurationException;

public class Generalizer extends Transformer {

    public static final String TRANSFORMATION_PATH = "transformations";
    protected static final String[] transformerNames = {
            "generalization.xslt"
    };

    public Generalizer() throws TransformerConfigurationException {
        super(TRANSFORMATION_PATH, transformerNames);
    }

    public String generalize(String regexStr)
            throws SExpressionParseException, RegexException {
        return transform(regexStr);
    }

    public String generalize(Regex regex)
            throws SExpressionParseException, RegexException {
        return transform(regex);
    }

}
