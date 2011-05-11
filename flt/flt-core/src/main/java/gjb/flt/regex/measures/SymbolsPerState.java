/*
 * Created on Nov 17, 2008
 * Modified on $Date: 2009-10-27 14:14:01 $
 */
package gjb.flt.regex.measures;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import gjb.flt.regex.Regex;
import gjb.util.tree.Node;
import gjb.util.tree.SExpressionParseException;
import gjb.util.tree.Tree;

/**
 * @author gjb
 * @version $Revision: 1.1 $
 * 
 */
public class SymbolsPerState implements LanguageMeasure {

    protected Regex regex = new Regex();

    public double compute(String regexStr) throws SExpressionParseException {
        Map<String,Integer> distr = computeSymbolDistribution(regexStr);
        double totalSymbols = 0.0;
        for (String symbol : distr.keySet()) {
            totalSymbols += distr.get(symbol);
        }
        if (distr.keySet().size() > 0)
            return totalSymbols/distr.keySet().size();
        else
            return 0.0;
    }

    public int computeMaximum(String regexStr) throws SExpressionParseException {
        Map<String,Integer> distr = computeSymbolDistribution(regexStr);
        int max = 0;
        for (String symbol : distr.keySet())
            if (max < distr.get(symbol))
                max = distr.get(symbol);
        return max;
    }

    public Map<String,Integer> computeSymbolDistribution(String regexStr)
            throws SExpressionParseException {
        Map<String,Integer> distr = new HashMap<String,Integer>();
        Tree tree = regex.getTree(regexStr);
        for (Iterator<Node> it = tree.leaves(); it.hasNext(); ) {
            String symbol = it.next().getKey();
            if (!symbol.equals(regex.epsilonSymbol()) &&
                    !symbol.equals(regex.emptySymbol())) {
                if (!distr.containsKey(symbol))
                    distr.put(symbol, 0);
                distr.put(symbol, distr.get(symbol) + 1);
            }
        }
        return distr;
    }

}
