/*
 * Created on Jul 14, 2008
 * Modified on $Date: 2009-10-27 14:14:01 $
 */
package eu.fox7.flt.regex.converters;

import eu.fox7.flt.regex.NoRegularExpressionDefinedException;
import eu.fox7.flt.regex.Regex;
import eu.fox7.flt.regex.RegexException;
import eu.fox7.util.tree.SExpressionParseException;

import javax.xml.transform.TransformerConfigurationException;

/**
 * @author eu.fox7
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
