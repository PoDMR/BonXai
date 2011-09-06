/*
 * Created on Sep 27, 2008
 * Modified on $Date: 2009-10-26 15:29:20 $
 */
package eu.fox7.flt.regex.infer.rwr.impl.io;

import eu.fox7.flt.regex.infer.rwr.impl.Automaton;

import java.io.IOException;
import java.io.Reader;

/**
 * @author eu.fox7
 * @version $Revision: 1.1 $
 * 
 */
public interface AutomatonReader {

    public Automaton read(Reader reader) throws IOException;

}
