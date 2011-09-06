/*
 * Created on May 30, 2007
 * Modified on $Date: 2009-10-27 14:14:01 $
 */
package eu.fox7.flt.regex.random;

import eu.fox7.flt.automata.NotDFAException;
import eu.fox7.flt.automata.impl.sparse.StateNFA;
import eu.fox7.flt.automata.measures.Coverage;
import eu.fox7.flt.regex.UnknownOperatorException;
import eu.fox7.flt.regex.generators.LanguageGenerator;
import eu.fox7.util.tree.SExpressionParseException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author eu.fox7
 * @version $Revision: 1.1 $
 * 
 */
public class Sample {

    protected String regexStr;
    protected LanguageGenerator generator;
    protected List<String[]> sample;
    protected StateNFA nfa;

    public Sample(String regexStr)
            throws SExpressionParseException, UnknownOperatorException {
        this(regexStr, 0);
    }

    public Sample(String regexStr, int size)
            throws SExpressionParseException, UnknownOperatorException {
        this.regexStr = regexStr;
        this.generator = new LanguageGenerator(regexStr);
        create(size);
    }

    public int size() {
        return sample != null ? sample.size() : 0;
    }

    public List<String[]> getSample() {
        return Collections.unmodifiableList(sample);
    }

    protected void create(int size) {
        this.sample = new ArrayList<String[]>();
        add(size);
    }

    protected void add(String[] example) {
        sample.add(example);
    }

    public void add(int size) {
        for (int i = 0; i < size; i++) {
            try {
                List<String> example = generator.generateRandomExample();
                sample.add(example.toArray(new String[0]));
            } catch (UnknownOperatorException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public boolean covers() throws NotDFAException {
        if (nfa == null)
            nfa = generator.getNFA();
        return Coverage.isCovered(nfa, sample);
    }

    public double getCoverage() throws NotDFAException {
        if (nfa == null)
            nfa = generator.getNFA();
        return Coverage.compute(nfa, sample);
        
    }

    public String getRegexStr() {
        return regexStr;
    }

}
