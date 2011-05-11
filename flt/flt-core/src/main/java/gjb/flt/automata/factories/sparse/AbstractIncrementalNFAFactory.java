/*
 * Created on Feb 7, 2006
 * Modified on $Date: 2009-10-28 12:17:59 $
 */
package gjb.flt.automata.factories.sparse;

import gjb.flt.automata.ModifiableNFA;
import gjb.util.sampling.ExampleParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Iterator;
import java.util.Properties;

/**
 * @author gjb
 * @version $Revision: 1.2 $
 * 
 */
public abstract class AbstractIncrementalNFAFactory<NFAType extends ModifiableNFA>
        implements IncrementalAutomatonFactory<NFAType> {

    protected NFAType nfa;

    public void add(Reader reader, Properties properties) throws IOException {
        ExampleParser parser = new ExampleParser(properties);
        BufferedReader bReader = new BufferedReader(reader);
        for (Iterator<String[]> exampleIt = parser.iterator(bReader);
             exampleIt.hasNext(); )
            add(exampleIt.next());
    }

    public void add(Reader reader) throws IOException {
        add(reader, null);
    }

    public abstract void add(String[] example);

    public NFAType getAutomaton() {
        return nfa;
    }

    public abstract AbstractIncrementalNFAFactory<NFAType> newInstance();

}
