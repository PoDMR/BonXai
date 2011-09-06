/*
 * Created on May 30, 2007
 * Modified on $Date: 2009-10-27 14:14:01 $
 */
package eu.fox7.flt.regex.random;

import eu.fox7.flt.regex.UnknownOperatorException;
import eu.fox7.util.tree.SExpressionParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.LinkedList;
import java.util.List;

/**
 * @author eu.fox7
 * @version $Revision: 1.1 $
 * 
 */
public class SimpleReader implements SampleReader {

    protected BufferedReader reader;

    public SimpleReader(Reader reader) {
        this.reader = new BufferedReader(reader);
    }

    /* (non-Javadoc)
     * @see eu.fox7.util.regex.random.SampleReader#read()
     */
    public List<Sample> read() throws SampleReadException {
        List<Sample> samples = new LinkedList<Sample>();
        State state = State.START;
        String line = null;
        Sample sample = null;
        try {
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (state == State.START) {
                    if (line.startsWith(SimpleWriter.REGEX_PREFIX)) {
                        String[] strs = line.split("=");
                        sample = new Sample(strs[1].trim());
                        state = State.EXAMPLES;
                    } else
                        throw new SampleReadException("sample description must begin with regex", null);
                } else if (state == State.EXAMPLES) {
                    if (line.equals(SimpleWriter.TERMINATOR)) {
                        state = State.START;
                        samples.add(sample);
                    } else {
                        String[] example = line.split(SimpleWriter.EXAMPLE_SEP);
                        sample.add(example);
                    }
                }
            }
        } catch (SExpressionParseException e) {
            throw new SampleReadException("syntax error in regular expression '" + line + "'", e);
        } catch (IOException e) {
            throw new SampleReadException("I/O exception", e);
        } catch (UnknownOperatorException e) {
            throw new SampleReadException("syntax error in regular expression '" + line + "'", e);
        }
        return samples;
    }

    protected enum State {
        START,
        EXAMPLES
    }

}
