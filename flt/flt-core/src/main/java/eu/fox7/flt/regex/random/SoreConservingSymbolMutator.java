/*
 * Created on Aug 27, 2008
 * Modified on $Date: 2009-10-27 14:14:01 $
 */
package eu.fox7.flt.regex.random;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import eu.fox7.util.RandomSelector;
import eu.fox7.util.tree.Node;
import eu.fox7.util.tree.Tree;


/**
 * @author eu.fox7
 * @version $Revision: 1.1 $
 * 
 * This mutator can be used on any regular expression, but is only guaranteed
 * to produce a new regular expression different from the original one for SOREs.
 * It simply exchanges two alphabet symbols in the regular expression.
 */
public class SoreConservingSymbolMutator extends AbstractMutator {

    protected static RandomSelector<Node> selector = new RandomSelector<Node>();

    public SoreConservingSymbolMutator() {
        this(null);
    }

    public SoreConservingSymbolMutator(Properties properties) {
        super(properties);
    }

    @Override
    protected void mutate(Tree tree) throws NoMutationPossibleException {
        Set<Node> leaves = new HashSet<Node>();
        for (Iterator<Node> it = tree.leaves(); it.hasNext(); ) {
            leaves.add(it.next());
        }
        if (leaves.size() >= 2) {
            List<Node> exchangeSymbols = selector.selectSublistFrom(leaves, 2);
            String symbolStr = exchangeSymbols.get(0).getKey();
            exchangeSymbols.get(0).setKey(exchangeSymbols.get(1).getKey());
            exchangeSymbols.get(1).setKey(symbolStr);
        } else {
            throw new NoMutationPossibleException("too few symbols");
        }
    }

}
