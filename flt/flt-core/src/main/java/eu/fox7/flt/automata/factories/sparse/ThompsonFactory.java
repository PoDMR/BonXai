/**
 * Created on Oct 20, 2009
 * Modified on $Date: 2009-11-12 22:18:54 $
 */
package eu.fox7.flt.automata.factories.sparse;

import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;

import eu.fox7.flt.automata.impl.sparse.SparseNFA;
import eu.fox7.flt.regex.Regex;
import eu.fox7.flt.regex.UnknownOperatorException;
import eu.fox7.util.PermutationSet;
import eu.fox7.util.tree.Node;
import eu.fox7.util.tree.SExpressionParseException;
import eu.fox7.util.tree.Tree;


/**
 * @author lucg5005
 * @version $Revision: 1.2 $
 *
 */
public class ThompsonFactory {

	protected Regex regex;

	public ThompsonFactory() {
		this(null);
	}

	public ThompsonFactory(Properties properties) {
		super();
		this.regex = new Regex(properties);
    }

	/**
	 * method to parse the string representation of a regular expression
	 * that returns the corresponding NFA; the regular expression must be given
	 * as an s-expression
	 * @param regex regular expression to parse in prefix notation
	 * @return an <code>NFA</code> that corresponds to the regular expression
	 * @throws SExpressionParseException thrown if the expression is not
	 * a valid s-expression
	 * @throws UnknownOperatorException thrown if an unknown operator is used
	 * in the regular expression
	 */
	public SparseNFA create(String regexStr)
		    throws SExpressionParseException, UnknownOperatorException {
		try {
            Tree tree = regex.getTree(regexStr);
			return create(tree);
		} catch (UnknownOperatorException e) {
			throw new UnknownOperatorException(e.operator(), regexStr);
		}
	}

	/**
	 * method that converts a given parse <code>Tree</code> to the corresponding
	 * <code>NFA</code>
	 * @param tree a <code>Tree</code> object representing a parse tree of some
	 * regular expression
	 * @return an <code>NFA</code> that corresponds to the parse three
	 * @throws UnknownOperatorException thrown if an unknown operator is used
	 * in the regular expression
	 */
	public SparseNFA create(Tree tree) throws UnknownOperatorException {
		return tree.getRoot() == null ? ThompsonBuilder.emptyNFA() : tree2NFA(tree.getRoot());
	}

	/**
	 * method that does the actual conversion between a parse tree and the
	 * corresponding NFA; processing is done recursively depth-first
	 * @param node <code>Node</code> to start the processing in
	 * @return an <code>NFA</code> that corresponds to the parse three
	 * @throws UnknownOperatorException thrown if an unknown operator is used
	 * in the regular expression
	 */
	protected SparseNFA tree2NFA(Node node) throws UnknownOperatorException {
		String o = node.key();
		final Properties properties = regex.getProperties();
		if (node.hasChildren()) {
			SparseNFA[] nfas = new SparseNFA[node.getNumberOfChildren()];
			for (int i = 0; i < node.getNumberOfChildren(); i++) {
				nfas[i] = tree2NFA(node.child(i));
			}
			if (o.equals(properties.getProperty("concat"))) {
				return ThompsonBuilder.concatenate(nfas);
			} else if (o.equals(properties.getProperty("union"))) {
				return ThompsonBuilder.union(nfas);
			} else if (o.equals(properties.getProperty("intersection"))) {
				return ProductNFAFactory.intersection(nfas);
			} else if (o.equals(properties.getProperty("interleave"))) {
			    PermutationSet<SparseNFA> pSet = new PermutationSet<SparseNFA>(nfas);
			    long size = pSet.size();
			    if (size > Integer.MAX_VALUE)
			    	throw new RuntimeException("too many operands in interleave");
				SparseNFA[] nfaSeqs = new SparseNFA[(int) size];
			    int i = 0;
			    for (Iterator<List<SparseNFA>> it = pSet.iterator(); it.hasNext(); ) {
			        List<SparseNFA> seq = it.next();
			        SparseNFA[] nfaSeq = new SparseNFA[seq.size()];
			        int j = 0;
			        for (SparseNFA nfa : seq)
			            nfaSeq[j++] = new SparseNFA(nfa);
			        nfaSeqs[i++] = ThompsonBuilder.concatenate(nfaSeq);
			    }
			    return ThompsonBuilder.union(nfaSeqs);
			} else if (o.equals(properties.getProperty("oneOrMore"))) {
				return ThompsonBuilder.oneOrMore(nfas[0]);
			} else if (o.equals(properties.getProperty("zeroOrMore"))) {
				return ThompsonBuilder.zeroOrMore(nfas[0]);
			} else if (o.equals(properties.getProperty("zeroOrOne"))) {
				return ThompsonBuilder.zeroOrOne(nfas[0]);
			} else if (o.matches(properties.getProperty("mToN"))) {
			    Matcher matcher = regex.mToNMatcher(o);
			    if (matcher.matches()) {
			        String minOccursStr = matcher.group(1);
			        int minOccurs = 0;
			        if (minOccursStr != null && minOccursStr.length() > 0) {
			            minOccurs = Integer.valueOf(minOccursStr).intValue();
			        }
			        String maxOccursStr = matcher.group(2);
			        int maxOccurs = -1;
			        if (maxOccursStr != null && maxOccursStr.length() > 0) {
			            maxOccurs = Integer.valueOf(maxOccursStr).intValue();
			        }
			        if ((minOccurs > maxOccurs && maxOccurs != -1) || maxOccurs == 0) {
			            return ThompsonBuilder.emptyNFA();
			        } else {
			            SparseNFA[] repetitions = maxOccurs == -1 ? new SparseNFA[minOccurs + 1]
			                                                          : new SparseNFA[maxOccurs];
			            for (int i = 0; i < minOccurs; i++) {
			                repetitions[i] = new SparseNFA(nfas[0]);
			            }
			            if (maxOccurs == -1) {
			                repetitions[minOccurs] = ThompsonBuilder.zeroOrMore(new SparseNFA(nfas[0]));
			            } else {
			                for (int i = minOccurs; i < maxOccurs; i++) {
			                    repetitions[i] = ThompsonBuilder.zeroOrOne(new SparseNFA(nfas[0]));
			                }
			            }
			            return ThompsonBuilder.concatenate(repetitions);
			        }
			    } else {
			        throw new Error("This really should have matched");
			    }
			} else {
				throw new UnknownOperatorException(o);
			}
		} else if (o.equals(properties.getProperty("empty"))) {
			return ThompsonBuilder.emptyNFA();
		} else if (o.equals(properties.getProperty("epsilon"))) {
		    return ThompsonBuilder.epsilonNFA();
		} else {
			return ThompsonBuilder.symbolNFA(o);
		}
	}

}
