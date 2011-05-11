/*
 * Created on May 30, 2007
 * Modified on $Date: 2009-10-27 14:14:01 $
 */
package gjb.flt.regex.random;

import java.io.IOException;
import java.io.Writer;

import org.apache.commons.lang.StringUtils;

/**
 * @author gjb
 * @version $Revision: 1.1 $
 * 
 */
public class SimpleWriter implements SampleWriter {

    public static final String REGEX_PREFIX = "regex = ";
    public static final String EXAMPLE_SEP = " ";
    public static final String TERMINATOR = "------";
    protected Writer writer;

    public SimpleWriter(Writer writer) {
        this.writer = writer;
    }

    /* (non-Javadoc)
     * @see gjb.util.regex.random.SampleWriter#write(gjb.util.regex.random.Sample)
     */
    public void write(Sample sample) throws SampleWriteException {
        try {
            writer.write(REGEX_PREFIX + sample.getRegexStr() + "\n");
            for (String[] example : sample.getSample())
                writer.write(StringUtils.join(example, EXAMPLE_SEP) + "\n");
            writer.write(TERMINATOR + "\n");
        } catch (IOException e) {
            throw new SampleWriteException("I/O exception", e);
        }
    }

}
