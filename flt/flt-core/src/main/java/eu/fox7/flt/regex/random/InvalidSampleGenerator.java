/*
 * Created on Aug 28, 2008
 * Modified on $Date: 2009-10-27 14:14:01 $
 */
package eu.fox7.flt.regex.random;

import eu.fox7.flt.automata.FeatureNotSupportedException;
import eu.fox7.flt.automata.factories.sparse.GlushkovFactory;
import eu.fox7.flt.automata.impl.sparse.SparseNFA;
import eu.fox7.flt.automata.matchers.NFAMatcher;
import eu.fox7.flt.regex.UnknownOperatorException;
import eu.fox7.flt.regex.generators.LanguageGenerator;
import eu.fox7.util.tree.SExpressionParseException;

import java.lang.reflect.Constructor;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

/**
 * @author eu.fox7
 * @version $Revision: 1.1 $
 * 
 */
public class InvalidSampleGenerator {

    public static final String NAME_SEP = ";";
    protected Properties properties;
    protected final String[] defaultMutatorNames = {
            "eu.fox7.flt.regex.random.SoreConservingSymbolMutator",
            "eu.fox7.flt.regex.random.SymbolMutator",
            "eu.fox7.flt.regex.random.NewSymbolMutator",
    };
    protected Mutator[] mutators;
    protected GlushkovFactory glushkovFactory;

    public InvalidSampleGenerator() throws MutatorCreationException {
        this(null);
    }

    public InvalidSampleGenerator(Properties properties)
            throws MutatorCreationException {
        this.properties = properties;
        this.glushkovFactory = new GlushkovFactory(properties);
        initMutators();
    }

    protected void initMutators() throws MutatorCreationException {
        String[] mutatorNames = null;
        if (properties != null && properties.containsKey("mutatorNames")) {
            mutatorNames = properties.getProperty("mutatorNames").split(NAME_SEP);
        } else {
            mutatorNames = defaultMutatorNames;
        }
        mutators = new Mutator[mutatorNames.length];
        for (int i = 0; i < mutatorNames.length; i++) {
            mutators[i] = createMutator(mutatorNames[i]);
        }
    }

    protected Mutator createMutator(String mutatorName)
            throws MutatorCreationException {
        try {
            Class<?> c = Class.forName(mutatorName);
            Class<?>[] argTypes = new Class<?>[] {Properties.class};
            Constructor<?> constructor = c.getConstructor(argTypes);
            Object[] args = new Object[] {properties};
            return (Mutator) constructor.newInstance(args);
        } catch (Exception e) {
            e.printStackTrace();
            throw new MutatorCreationException(e);
        }
    }

    public List<String[]> getSample(String regexStr, int sampleSize)
            throws NoMutationFoundException {
        try {
            List<String[]> sample = new LinkedList<String[]>();
            SparseNFA nfa = glushkovFactory.create(regexStr);
            NFAMatcher matcher = new NFAMatcher(nfa);
            String mutatedRegexStr = null;
            for (Mutator mutator : mutators) {
                try {
                    mutatedRegexStr = mutator.mutate(regexStr);
                    break;
                } catch (NoMutationFoundException e) {}
            }
            if (mutatedRegexStr != null) {
                LanguageGenerator regex = new LanguageGenerator(mutatedRegexStr);
                while (sample.size() < sampleSize) {
                    String[] example = convertExample(regex.generateRandomExample());
                    if (!matcher.matches(example))
                        sample.add(example);
                }
                return sample;
            } else {
                throw new NoMutationFoundException();
            }
        } catch (SExpressionParseException e) {
            e.printStackTrace();
            throw new RuntimeException("unexpected exception", e);
        } catch (UnknownOperatorException e) {
            e.printStackTrace();
            throw new RuntimeException("unexpected exception", e);
        } catch (FeatureNotSupportedException e) {
            e.printStackTrace();
            throw new RuntimeException("unexpected exception", e);
        }
    }

    protected String[] convertExample(List<String> list) {
        String[] array = new String[list.size()];
        for (int i = 0; i < array.length; i++)
            array[i] = list.get(i);
        return array;
    }

}
