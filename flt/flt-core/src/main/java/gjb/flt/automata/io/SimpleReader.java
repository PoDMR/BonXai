/*
 * Created on Nov 13, 2006
 * Modified on $Date: 2009-10-27 14:14:01 $
 */
package gjb.flt.automata.io;

import gjb.flt.automata.impl.sparse.SparseNFA;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

/**
 * @author gjb
 * @version $Revision: 1.1 $
 * 
 */
public class SimpleReader implements SparseNFAReader {

    protected BufferedReader reader;

    public SimpleReader(Reader reader) {
        this.reader = new BufferedReader(reader);
    }

    /* (non-Javadoc)
     * @see gjb.util.automata.NFAReader#write(gjb.util.automata.NFA)
     */
    public SparseNFA read() throws NFAReadException {
        SparseNFA nfa = new SparseNFA();
        try {
            String line = null;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.startsWith("#"))
                    continue;
                else if (line.startsWith(SimpleWriter.INIT_STATE))
                    nfa.setInitialState(extractInitialState(line));
                else if (line.startsWith(SimpleWriter.FINAL_STATE)) {
                    String[] finalStates = extractFinalState(line);
                    for (int i = 0; i < finalStates.length; i++)
                        nfa.addFinalState(finalStates[i]);
                } else {
                    String[] values = extractTransition(line);
                    nfa.addTransition(values[0], values[1], values[2]);
                }
            }
        } catch (IOException e) {
            throw new NFAReadException("IO exception", e);
        }
        return nfa;
    }

    protected String extractInitialState(String line) {
        return extractRemainder(SimpleWriter.INIT_STATE, line);
    }

    protected String[] extractFinalState(String line) {
        String statesStr = extractRemainder(SimpleWriter.FINAL_STATE, line);
        return statesStr.split("\\s+");
    }
    
    protected String[] extractTransition(String line) throws NFAReadException {
        String[] values = new String[3];
        String[] sides = line.split(SimpleWriter.SEP);
        if (sides.length != 2)
            throw new NFAReadException("parse exception: " + line, null);
        values[2] = sides[1].trim();
        String[] parts = sides[0].split(SimpleWriter.LHS_SEP);
        if (parts.length != 2)
            throw new NFAReadException("parse exception: " + line, null);
        values[0] = parts[1].trim();
        values[1] = parts[0].trim();
        return values;
    }


    protected String extractRemainder(String prefix, String line) {
        int beginIndex = prefix.length();
        return line.substring(beginIndex).trim();
    }


}
