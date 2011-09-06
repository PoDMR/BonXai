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
public class GraphAutomatonWriter implements AutomatonWriter {

    public static final String SEP = "#";
    public static final char TERMINATOR = ';';

    public void write(Writer writer, Automaton automaton) throws IOException {
        for (int i = 0; i < automaton.getNumberOfStates() - 1; i++)
            writer.append(automaton.getLabel(i)).append(SEP);
        for (int i = 0; i < automaton.getNumberOfStates(); i++)
            for (int j = 0; j < automaton.getNumberOfStates(); j++)
                if (automaton.get(i, j) != 0)
                    writer.append("1");
                else
                    writer.append("0");
        writer.append(TERMINATOR);
    }

}
