/*
 * Created on Aug 27, 2008
 * Modified on $Date: 2009-10-27 14:14:01 $
 */
package eu.fox7.flt.regex.random;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.lang.RandomStringUtils;

import eu.fox7.util.RandomSelector;
import eu.fox7.util.tree.Node;
import eu.fox7.util.tree.Tree;


/**
 * @author eu.fox7
 * @version $Revision: 1.1 $
 * 
 */
public class NewSymbolMutator extends AbstractMutator {

    protected static RandomSelector<Node> selector = new RandomSelector<Node>();

    public NewSymbolMutator() {
        this(null);
    }

    public NewSymbolMutator(Properties properties) {
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
        Node leaf = selector.selectOneFrom(leaves);
        String newSymbolStr = null;
        do {
            newSymbolStr = RandomStringUtils.randomAlphabetic(5);
        } while (alphabet.contains(newSymbolStr));
        leaf.setKey(newSymbolStr);
    }

}
