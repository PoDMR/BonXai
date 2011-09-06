/*
 * Created on Aug 27, 2008
 * Modified on $Date: 2009-10-27 14:14:01 $
 */
package eu.fox7.flt.regex.random;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;

import eu.fox7.util.RandomSelector;
import eu.fox7.util.tree.Node;
import eu.fox7.util.tree.Tree;


/**
 * @author eu.fox7
 * @version $Revision: 1.1 $
 * 
 */
public class SymbolMutator extends AbstractMutator {

    protected static RandomSelector<Node> selector = new RandomSelector<Node>();
    protected static RandomSelector<String> symbolSelector = new RandomSelector<String>();

    public SymbolMutator() {
        this(null);
    }

    public SymbolMutator(Properties properties) {
        super(properties);
    }

    @Override
    protected void mutate(Tree tree) throws NoMutationPossibleException {
        Set<Node> leaves = new HashSet<Node>();
        Set<String> alphabet = new HashSet<String>();
        for (Iterator<Node> it = tree.leaves(); it.hasNext(); ) {
            Node node = it.next();
            leaves.add(node);
            alphabet.add(node.getKey());
        }
        if (leaves.size() > 1 && alphabet.size() > 1) {
            Node leaf = selector.selectOneFrom(leaves);
            alphabet.remove(leaf.getKey());
            String symbolStr = symbolSelector.selectOneFrom(alphabet);
            leaf.setKey(symbolStr);
        } else {
            throw new NoMutationPossibleException("too few symbols");
        }
    }

}
