/*
 * Created on Sep 27, 2008
 * Modified on $Date: 2009-10-26 15:29:20 $
 */
package gjb.flt.regex.infer.rwr.impl.io;

import gjb.flt.regex.infer.rwr.impl.Automaton;

import java.io.IOException;
import java.io.Writer;

/**
 * @author gjb
 * @version $Revision: 1.1 $
 * 
 */
public interface AutomatonWriter {

    public void write(Writer writer, Automaton automaon) throws IOException;

}
