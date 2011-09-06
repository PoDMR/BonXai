/*
 * Created on Aug 27, 2008
 * Modified on $Date: 2009-10-27 14:14:01 $
 */
package eu.fox7.flt.regex.random;

import eu.fox7.flt.automata.FeatureNotSupportedException;
import eu.fox7.flt.automata.NotDFAException;
import eu.fox7.flt.automata.factories.sparse.GlushkovFactory;
import eu.fox7.flt.automata.impl.sparse.StateNFA;
import eu.fox7.flt.automata.measures.EquivalenceTest;
import eu.fox7.flt.regex.Glushkov;
import eu.fox7.flt.regex.Regex;
import eu.fox7.flt.regex.UnknownOperatorException;
import eu.fox7.util.tree.SExpressionParseException;
import eu.fox7.util.tree.Tree;

import java.util.Properties;

/**
 * @author eu.fox7
 * @version $Revision: 1.1 $
 * 
 */
public abstract class AbstractMutator implements Mutator {

    protected Properties properties;
    protected Regex regex;
    protected Glushkov glushkov;
    protected GlushkovFactory glushkovFactory;
    protected int maxTries = 5;
    protected boolean isAmbiguityAllowed = false;

    public AbstractMutator() {
        this(null);
    }

    public AbstractMutator(Properties properties) {
        this.properties = properties;
        this.regex = new Regex(properties);
        this.glushkov = new Glushkov(properties);
        this.glushkovFactory = new GlushkovFactory(properties);
    }

    public int getMaxTries() {
        return maxTries;
    }

    public void setMaxTries(int maxTries) {
        this.maxTries = maxTries;
    }

    public boolean isAmbiguityAllowed() {
        return isAmbiguityAllowed;
    }

    public void setAmbiguityAllowed(boolean isAmbiguityAllowed) {
        this.isAmbiguityAllowed = isAmbiguityAllowed;
    }

    public String mutate(String regexStr)
            throws SExpressionParseException, NoMutationFoundException {
        int nrTries = 0;
        String newRegexStr = null;
        boolean ok = false;
        try {
            do {
                nrTries++;
                Tree tree = regex.getTree(regexStr);
                mutate(tree);
                newRegexStr = tree.toSExpression();
                ok = isAmbiguous(newRegexStr) &&
                         isMutated(regexStr, newRegexStr);
            } while (!ok && nrTries < maxTries);
        } catch (NotDFAException e) {
            e.printStackTrace();
            throw new RuntimeException("unexpected excpetion", e);
        } catch (UnknownOperatorException e) {
            e.printStackTrace();
            throw new RuntimeException("unexpected excpetion", e);
        } catch (FeatureNotSupportedException e) {
            e.printStackTrace();
            throw new RuntimeException("unexpected excpetion", e);
        }
        if (ok)
            return newRegexStr;
        else
            throw new NoMutationFoundException();
    }

    protected boolean isAmbiguous(String regexStr)
            throws SExpressionParseException, UnknownOperatorException,
                   FeatureNotSupportedException {
        return isAmbiguityAllowed() || !glushkov.isAmbiguous(regexStr);
    }

    protected boolean isMutated(String origRegexStr, String mutatedRegexStr) throws NotDFAException {
        try {
            StateNFA origNFA = glushkovFactory.create(origRegexStr);
            StateNFA mutatedNFA = glushkovFactory.create(mutatedRegexStr);
            return !EquivalenceTest.areEquivalent(origNFA, mutatedNFA);
        } catch (SExpressionParseException e) {
            e.printStackTrace();
            throw new RuntimeException("unexpected excpetion", e);
        } catch (UnknownOperatorException e) {
            e.printStackTrace();
            throw new RuntimeException("unexpected excpetion", e);
        } catch (FeatureNotSupportedException e) {
            e.printStackTrace();
            throw new RuntimeException("unexpected excpetion", e);
        }
    }

    protected abstract void mutate(Tree tree) throws NoMutationPossibleException;

}
