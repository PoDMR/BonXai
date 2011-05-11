/*
 * Created on Feb 7, 2006
 * Modified on $Date: 2009-10-28 12:17:59 $
 */
package gjb.flt.automata.factories.sparse;

import gjb.flt.automata.ModifiableNFA;

import java.io.IOException;
import java.io.Reader;
import java.util.Properties;

public interface IncrementalAutomatonFactory<NFAType extends ModifiableNFA>
        extends AutomatonFactory<NFAType> {

    public void add(Reader reader, Properties properties) throws IOException;

    public void add(Reader reader) throws IOException;

    public void add(String[] str);

    public NFAType getAutomaton();

    public IncrementalAutomatonFactory<NFAType> newInstance();

}