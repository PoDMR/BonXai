/*
 * Created on Nov 7, 2005
 * Modified on $Date: 2009-10-27 14:14:01 $
 */
package gjb.flt.regex.converters;

import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;

import gjb.flt.regex.InvalidXMLException;
import gjb.flt.regex.NoRegularExpressionDefinedException;
import gjb.flt.regex.Regex;
import gjb.flt.regex.RegexException;
import gjb.flt.regex.UnknownOperatorException;
import gjb.util.tree.SExpressionParseException;

/**
 * @author gjb
 * @version $Revision: 1.1 $
 * 
 */
public class Simplifier extends Transformer {

    public static final String TRANSFORMATION_PATH = "transformations";
    protected static final String[] transformerNames = {
        "eliminate-zeroOrMore.xslt",
        "lift-single-operand-in-nary.xslt",
        "concat-associativity.xslt",
        "union-associativity.xslt",
        "reduce-concat-multiplicity.xslt",
        "lift-optional-in-union.xslt",
        "lift-multiplicity-in-union.xslt",
        "lift-multiplicity-in-concat-in-union.xslt",
        "lift-multiplicity-in-concat.xslt",
        "reduce-multiple-multiplicities.xslt",
        "reduce-multiple-optionals.xslt",
        "swap-multiplicity-optional.xslt",
        "reduce-concat-optionals-union-repeat.xslt",
        "optionals-concat-multiplicity-to-union.xslt"
    };
    
    public Simplifier() throws TransformerConfigurationException {
        this(TRANSFORMATION_PATH);
    }

    public Simplifier(String transformationPath)
            throws TransformerConfigurationException {
        super(transformationPath, transformerNames);
    }
    
    public String simplify(String regexStr)
            throws SExpressionParseException, RegexException {
        return transform(regexStr);
    }

    public String simplify(Regex regex)
            throws SExpressionParseException, RegexException {
        return transform(regex);
    }

    public Regex simplifyRegex(Regex regex)
            throws NoRegularExpressionDefinedException, TransformerException,
                   InvalidXMLException, SExpressionParseException,
                   UnknownOperatorException {
        return transformRegex(regex);
    }
    
}
