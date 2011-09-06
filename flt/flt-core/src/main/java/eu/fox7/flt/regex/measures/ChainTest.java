/*
 * Created on Aug 12, 2008
 * Modified on $Date: 2009-11-12 22:18:04 $
 */
package eu.fox7.flt.regex.measures;

import java.util.Properties;

import eu.fox7.flt.regex.Regex;
import eu.fox7.util.tree.Node;
import eu.fox7.util.tree.SExpressionParseException;
import eu.fox7.util.tree.Tree;


/**
 * @author eu.fox7
 * @version $Revision: 1.2 $
 * 
 */
public class ChainTest implements LanguageTest {

    protected Regex regex;

    public ChainTest() {
        super();
        this.regex = new Regex();
    }

    public ChainTest(Properties properties) {
        super();
        this.regex = new Regex(properties);
    }

    public boolean test(String regexStr) throws SExpressionParseException {
        Tree tree = regex.getTree(regexStr);
        return checkRootNode(tree.getRoot());
    }

    protected boolean checkRootNode(Node node) {
        String symbol = node.getKey();
        if (regex.emptySymbol().equals(symbol) ||
                regex.epsilonSymbol().equals(symbol)) {
            return true;
        } else if (regex.concatOperator().equals(symbol)) {
            for (int i = 0; i < node.getNumberOfChildren(); i++)
                if (!checkFactor(node.getChild(i)))
                    return false;
            return true;
        } else if (regex.zeroOrOneOperator().equals(symbol) ||
                regex.zeroOrMoreOperator().equals(symbol) ||
                regex.oneOrMoreOperator().equals(symbol)) {
            return checkFactor(node);
        } else if (regex.unionOperator().equals(symbol)) {
            return checkTerm(node);
        } else if (!regex.isOperatorSymbol(symbol)) {
            return true;
        } else {
            return false;
        }
    }

    protected boolean checkFactor(Node node) {
        String symbol = node.getKey();
        if (regex.zeroOrOneOperator().equals(symbol) ||
                regex.zeroOrMoreOperator().equals(symbol) ||
                regex.oneOrMoreOperator().equals(symbol)) {
            return checkTerm(node.getChild(0));
        } else if (regex.unionOperator().equals(symbol)) {
            return checkTerm(node);
        } else if (!regex.isOperatorSymbol(symbol)) {
            return true;
        } else {
            return false;
        }
    }

    protected boolean checkTerm(Node node) {
        String symbol = node.getKey();
        if (regex.unionOperator().equals(symbol)) {
            for (int i = 0; i < node.getNumberOfChildren(); i++)
                if (regex.isOperatorSymbol(node.getChild(i).getKey()))
                    return false;
            return true;
        } else {
            return !regex.isOperatorSymbol(symbol);
        }
    }

}
