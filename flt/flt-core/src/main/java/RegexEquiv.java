import eu.fox7.flt.automata.factories.sparse.ProductNFAFactory;
import eu.fox7.flt.automata.factories.sparse.ThompsonBuilder;
import eu.fox7.flt.automata.factories.sparse.ThompsonFactory;
import eu.fox7.flt.automata.impl.sparse.SparseNFA;
import eu.fox7.flt.automata.measures.EmptynessTest;

import java.util.HashSet;
import java.util.Set;

/*
 * Created on Nov 16, 2005
 * Modified on $Date: 2009-10-27 14:14:01 $
 */

/**
 * @author eu.fox7
 * @version $Revision: 1.1 $
 * 
 */
public class RegexEquiv {

    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
            System.err.println("### two regexes should be given");
            System.exit(-1);
        }
        ThompsonFactory factory = new ThompsonFactory();
        if (languagesEqual(factory.create(args[0]), factory.create(args[1]))) {
            System.out.println("eq");
            System.exit(0);
        } else {
            System.out.println("neq");
            System.exit(1);
        }
    }

    @SuppressWarnings("unchecked")
    protected static boolean languagesEqual(SparseNFA nfa1, SparseNFA nfa2) {
        SparseNFA[] nfas = {nfa1, nfa2};
        Set alphabet = new HashSet();
        alphabet.addAll(nfa1.getSymbolValues());
        alphabet.addAll(nfa2.getSymbolValues());
        SparseNFA intersectionCompl = ThompsonBuilder.complement(ProductNFAFactory.intersection(nfas), alphabet);
        SparseNFA union = ThompsonBuilder.union(nfas);
        SparseNFA[] nfas2 = {intersectionCompl, union};
        EmptynessTest emptyTest = new EmptynessTest();
        return emptyTest.test(ProductNFAFactory.intersection(nfas2));
    }

}
