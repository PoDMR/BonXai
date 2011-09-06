/*
 * Created on Sep 27, 2008
 * Modified on $Date: 2009-10-26 15:29:20 $
 */
package eu.fox7.flt.regex.infer.rwr.impl.io;

import eu.fox7.flt.regex.infer.rwr.impl.Automaton;

import java.io.IOException;
import java.io.Writer;

/**
 * @author eu.fox7
 * @version $Revision: 1.1 $
 * 
 */
public interface AutomatonWriter {

    public void write(Writer writer, Automaton automaon) throws IOException;

}
