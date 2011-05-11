/*
 * Created on Mar 4, 2005
 * Modified on $Date: 2009-10-27 14:11:40 $
 */
package gjb.util.sampling;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

/**
 * @author gjb
 * @version $Revision: 1.1 $
 * <p>Class to read a sample from a BufferedReader.  Facilities are provided to
 * retain only unique examples and to allow some variation in the input
 * format.</p>
 * <p>The sample is read from a BufferedReader and the default format is
 * defined as follows: the sample should have one example per line, the tokens
 * of each examples separated by whitespace.  Note that an empty line is
 * interpreted as the empty string <strong>hence the file should not contain
 * a new line at the end of last line</strong>.  Lines starting with a '#' are
 * ignored.  E.g.
 * <pre>
 * a b c c c
 * # the next line denotes the empty string
 * 
 * a b c
 * a c c c
 * </pre>
 * is a valid input file.</p>
 * <p>Various parameters can be set to control the input format:
 * <dl>
 *   <dt>separator</dt>
 *     <dd>pattern that is used to split the tokens in an example</dd>
 *   <dt>comment</dt>
 *     <dd>pattern that is used to recognize a line of comment</dd>
 *   <dt>terminator</dt>
 *     <dd>pattern that can be used to filter out unwanted line endings</dd>
 *   <dt>separator_char</dt>
 *     <dd>a character different from character in the example to construct an
 *         examples signature</dd> 
 * </dl></p>
 * <p>The following code fragment illustrates the class' use when the sample is
 * in the default format:
 * <pre>
 *   ExampleParser parser = new ExampleParser();
 *   BufferedReader reader = new BufferedReader(new FileReader("sample.txt"));
 *   List sample = parser.parse(reader);
 * </pre>
 * while the following illustrates a non-default separator that will moreover
 * return only the unique examples:
 * <pre>
 *   Properties prop = new Properties();
 *   prop.setProperty(ExampleParser.SEPARATOR_FIELD, "\\s*,\\s*");
 *   ExampleParser parser = new ExampleParser(prop);
 *   List examples = parser.parse(reader, true);
 * </pre></p>
 */
public class ExampleParser {

    /**
     * Properties of the parser, initialized in the constructor(s)
     */
    protected Properties properties = new Properties();
    /**
     * name of the Properties field that stores the separator pattern
     */
    public static final String SEPARATOR_FIELD = "separator";
    /**
     * name of the Properties field that stores the separator character
     * used to compute the signature for determining uniqueness
     */
    public static final String SEPARATOR_CHAR_FIELD = "separator_char";
    /**
     * name of the Properties field that stores the comment pattern
     */
    public static final String COMMENT_FIELD = "comment";
    /**
     * name of the Properties field that stores the terminator pattern 
     */
    public static final String TERMINATOR_FIELD = "terminator";
    /**
     * default separator pattern
     */
    public static final String SEPARATOR = "\\s+";
    /**
     * default separator character
     * used to compute the signature for determining uniqueness
     */
    public static final String SEPARATOR_CHAR = " ";
    /**
     * default comment pattern
     */
    public static final String COMMENT = "^\\s*#.+$";
    /**
     * default terminator pattern
     */
    public static final String TERMINATOR = "";
    /**
     * Pattern to strip the terminator
     */
    protected Pattern terminatorPattern;
    /**
     * number of lines read during last parse
     */
    protected int linesRead = -1;

    /**
     * constructor for a parser with the default patterns for the separator,
     * comment and terminator: fields are separated by one or more spaces, comments
     * lines start with '#' and there's no explicit terminator string.  E.g.
     * <pre>
     * a b c
     *   # comment
     * a b
     * 
     * c d
     * </pre>
     * An empty line denotes the empty string, <strong>hence beware of 
     * trailing newlines at the end of the input file</strong>.
     */
    public ExampleParser() {
        properties.setProperty(SEPARATOR_FIELD, SEPARATOR);
        properties.setProperty(COMMENT_FIELD, COMMENT);
        properties.setProperty(TERMINATOR_FIELD, TERMINATOR);
        properties.setProperty(SEPARATOR_CHAR_FIELD, SEPARATOR_CHAR);
        computePatterns();
    }
    
    /**
     * constructor for a parser with non-default patterns for the separator,
     * comment and terminator
     * @param prop
     *            Properties to override the default values in de SEPARATOR_FIELD,
     *            COMMENT_FIELD and TERMINATOR_FIELD
     */
    public ExampleParser(Properties prop) {
        this();
        if (prop != null) {
            for (Enumeration<?> e = prop.propertyNames(); e.hasMoreElements(); ) {
                String key = (String) e.nextElement();
                properties.setProperty(key, prop.getProperty(key));
            }
            computePatterns();
        }
    }

    /**
     * method that computes the various Patterns needed to parse the examples
     */
    protected void computePatterns() {
        if (!properties.getProperty(TERMINATOR_FIELD).equals("")) {
            terminatorPattern = Pattern.compile(properties.getProperty(TERMINATOR_FIELD));
        }
    }

    /**
     * method to parse a line of input
     * @param line
     *            String to parse
     * @return String[] that is null for a comment line, non-null otherwise, each
     *         String represents a token of the example, hence the empty string
     *         is represented by an array of length 0.
     */
    public String[] parse(String line) {
        line = line.trim();
        if (line.matches(properties.getProperty(COMMENT_FIELD))) {
            return null;
        }
        if (terminatorPattern != null) {
            Matcher matcher = terminatorPattern.matcher(line);
            line = matcher.replaceAll("").trim();
        }
        if (line.length() == 0) {
            return new String[0];
        } else {
            return line.split(properties.getProperty(SEPARATOR_FIELD));
        }
    }

    /**
     * method that reads a sample from the specified BufferedReader and returns
     * it as a list of samples
     * @param reader
     *            BufferedReader to read the sample from
     * @return List with the examples as String[]
     * @throws IOException if an exception occurs reading the sample
     */
    public List<String[]> parse(BufferedReader reader) throws IOException {
        return parse(reader, false);
    }

    /**
     * method that reads a sample from the specified BufferedReader and returns
     * it as a list of samples, a boolean flag specifies whether or not to
     * return only unique examples
     * @param reader
     *            BufferedReader to read the sample from
     * @param unique
     *            boolean true indicates that only unique examples should be returned,
     *            false indicates all examples will be returned
     * @return List with the <strong>unqiue</strong> examples as String[]
     * @throws IOException if an exception occurs reading the sample
     */
    public List<String[]> parse(BufferedReader reader, boolean unique) throws IOException {
        List<String[]> list = new LinkedList<String[]>();
        Set<String> uniqueSet = new HashSet<String>();
        String separator = properties.getProperty(SEPARATOR_CHAR_FIELD);
        String line = null;
        linesRead = 0;
        while ((line = reader.readLine()) != null) {
            linesRead++;
            String[] example = parse(line);
            if (example != null) {
                if (!unique) {
                    list.add(example);
                } else {
                    String signature = StringUtils.join(example, separator);
                    if (!uniqueSet.contains(signature)) {
                        list.add(example);
                        uniqueSet.add(signature);
                    }
                }
            }
        }
        return list;
    }

    /**
     * method that returns the actual value used by this ExampleParser
     * @return String separator
     */
    public String getSeparator() {
        return properties.getProperty(SEPARATOR_FIELD);
    }

    /**
     * method that provides an Iterator over the sample while it is being read
     * from a reader
     * @param reader
     *            BufferedReader that is the data source
     * @return Iterator<String[]> over the examples
     * @throws IOException if an exception occurs reading the sample
     */
    public Iterator<String[]> iterator(BufferedReader reader)
            throws IOException {
        return new ExampleIterator(reader);
    }

    /**
     * method that returns the actual value used by this ExampleParser
     * @return String separator character
     */
    public String getSeparatorChar() {
        return properties.getProperty(SEPARATOR_CHAR_FIELD);
    }

    /**
     * method that returns the actual value used by this ExampleParser
     * @return String comment indicator
     */
    public String getComment() {
        return properties.getProperty(COMMENT_FIELD);
    }

    /**
     * method that returns the actual value used by this ExampleParser
     * @return String terminator
     */
    public String getTerminator() {
        return properties.getProperty(TERMINATOR_FIELD);
    }

    /**
     * method that returns the number of lines read during the last parse
     * operation
     * @return int number of lines read
     */
    public int getLinesRead() {
        return linesRead;
    }

    protected class ExampleIterator implements Iterator<String[]> {

        protected BufferedReader reader;
        protected String[] currentExample;

        protected ExampleIterator(BufferedReader reader) throws IOException {
            this.reader = reader;
            String line = null;
            currentExample = null;
            if ((line = reader.readLine()) != null)
                currentExample = parse(line);
        }

        public boolean hasNext() {
            return currentExample != null;
        }

        public String[] next() {
            if (!hasNext()) throw new NoSuchElementException();
            String[] result = currentExample;
            String line = null;
            currentExample = null;
            try {
                if ((line = reader.readLine()) != null)
                    currentExample = parse(line);
            } catch (IOException e) {
                e.printStackTrace();
                throw new NoSuchElementException();
            }
            return result;
        }

        public void remove() {}
        
    }

}
