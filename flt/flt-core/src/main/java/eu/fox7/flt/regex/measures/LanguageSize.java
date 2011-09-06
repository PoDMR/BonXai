/*
 * Created on Oct 23, 2007
 * Modified on $Date: 2009-10-27 14:14:01 $
 */
package eu.fox7.flt.regex.measures;

import eu.fox7.flt.automata.FeatureNotSupportedException;
import eu.fox7.flt.automata.factories.sparse.GlushkovFactory;
import eu.fox7.flt.automata.impl.sparse.StateNFA;
import eu.fox7.flt.regex.UnknownOperatorException;
import eu.fox7.util.tree.SExpressionParseException;

/**
 * @author eu.fox7
 * @version $Revision: 1.1 $
 * 
 */
public class LanguageSize implements LanguageMeasure, RelativeLanguageMeasure {

    protected GlushkovFactory glushkov = new GlushkovFactory();
    protected eu.fox7.flt.automata.measures.LanguageSize measure = new eu.fox7.flt.automata.measures.LanguageSize();

    /* (non-Javadoc)
     * @see eu.fox7.util.regex.measures.LanguageMeasure#compute(java.lang.String)
     */
    public double compute(String regexStr)
            throws SExpressionParseException, UnknownOperatorException,
                   FeatureNotSupportedException {
        StateNFA nfa = glushkov.create(regexStr);
        return measure.compute(nfa);
    }

    /**
     * computes the relative language size of inclRegexStr with respect to
     * regexStr; note that this measure will only be meaningful if
     * (1) L(inclRegexStr) \subseteq L(regexStr), and (2) inclRegexStr and
     * regexStr are 1-unambiguous.
     */
    public double compute(String inclRegexStr, String regexStr)
            throws SExpressionParseException, UnknownOperatorException,
                   FeatureNotSupportedException {
        StateNFA inclNFA = glushkov.create(inclRegexStr);
        StateNFA nfa = glushkov.create(regexStr);
        return measure.compute(inclNFA, nfa);
    }

}
