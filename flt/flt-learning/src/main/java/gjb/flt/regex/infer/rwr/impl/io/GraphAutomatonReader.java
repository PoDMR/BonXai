/*
 * Created on Sep 28, 2008
 * Modified on $Date: 2009-10-26 15:29:20 $
 */
package gjb.flt.regex.infer.rwr.impl.io;

import gjb.flt.regex.infer.rwr.impl.Automaton;
import gjb.flt.regex.infer.rwr.impl.AutomatonConstructionException;
import gjb.flt.regex.infer.rwr.impl.GraphAutomaton;

import java.io.IOException;
import java.io.Reader;

/**
 * @author gjb
 * @version $Revision: 1.1 $
 * 
 */
public class GraphAutomatonReader implements AutomatonReader {

    public Automaton read(Reader reader) throws IOException {
        String str = "";
        int c = -1;
        while ((c = reader.read()) >= 0) {
            if (c == GraphAutomatonWriter.TERMINATOR)
                break;
            str += ((char) c);
        }
        String[] parts = str.split(GraphAutomatonWriter.SEP);
        String matrixRepresentation = parts[parts.length - 1];
        int alphabetSize = parts.length - 1;
        Automaton automaton = new GraphAutomaton(alphabetSize);
        try {
            for (int i = 0; i < alphabetSize; i++)
                for (int j = 0; j < alphabetSize; j++)
                    if (matrixRepresentation.charAt(i*parts.length + j) == '1')
                        automaton.addTransition(parts[i], parts[j]);
            for (int j = 0; j < alphabetSize; j++)
                if (matrixRepresentation.charAt(alphabetSize*parts.length + j) == '1')
                    automaton.addInitial(parts[j]);
            for (int i = 0; i < alphabetSize; i++)
                if (matrixRepresentation.charAt(i*parts.length + alphabetSize) == '1')
                    automaton.addFinal(parts[i]);
            if (matrixRepresentation.charAt(alphabetSize*parts.length + alphabetSize) == '1')
                automaton.addEpsilon();
        } catch (AutomatonConstructionException e) {
            throw new RuntimeException("unexpected exception", e);
        }
        return automaton;
    }

}
