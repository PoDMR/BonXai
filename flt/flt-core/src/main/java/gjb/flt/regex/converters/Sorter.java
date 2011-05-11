/*
 * Created on Jul 14, 2008
 * Modified on $Date: 2009-10-27 14:14:01 $
 */
package gjb.flt.regex.converters;

import gjb.flt.regex.NoRegularExpressionDefinedException;
import gjb.flt.regex.Regex;
import gjb.flt.regex.RegexException;
import gjb.util.tree.SExpressionParseException;

import javax.xml.transform.TransformerConfigurationException;

/**
 * @author gjb
 * @version $Revision: 1.1 $
 * 
 */
public class Sorter extends Transformer {

    public static final String TRANSFORMATION_PATH = "transformations";
    protected static final String[] transformerNames = {
        "sort-unions.xslt"
    };
    
    public Sorter() throws TransformerConfigurationException {
        this(TRANSFORMATION_PATH);
    }

    public Sorter(String transformationPath)
            throws TransformerConfigurationException {
        super(transformationPath, transformerNames);
    }

    public String sort(String regexStr)
            throws SExpressionParseException, RegexException {
        return transform(regexStr);
    }

    public String sort(Regex regex)
            throws SExpressionParseException, RegexException {
        return transform(regex);
    }

    public Regex sortRegex(Regex regex) throws NoRegularExpressionDefinedException {
        return transformRegex(regex);
    }

}
