/*
 * Created on Jul 18, 2006
 * Modified on $Date: 2009-11-09 13:13:38 $
 */
package eu.fox7.util.xml;

import eu.fox7.util.xml.acstring.ExampleParser;
import eu.fox7.util.xml.acstring.ExampleParsingException;
import eu.fox7.util.xml.acstring.ParseResult;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Iterator;

/**
 * @author eu.fox7
 * @version $Revision: 1.1 $
 * 
 */
public class ExampleIterator implements Iterator<ParseResult> {

    public static final String DEFAULT_COMMENT = "#";
    protected String comment = DEFAULT_COMMENT;
    protected BufferedReader reader;
    protected ParseResult nextExample = null;
    protected ExampleParser parser;

    public ExampleIterator(File file)
            throws ExampleParsingException, FileNotFoundException {
        this(new FileReader(file));
    }

    public ExampleIterator(File file, ExampleParser parser)
            throws FileNotFoundException, ExampleParsingException {
        this(new FileReader(file), parser);
    }

    public ExampleIterator(Reader reader) throws ExampleParsingException {
        this(reader, null);
    }

    public ExampleIterator(Reader reader, ExampleParser parser)
            throws ExampleParsingException {
        this.reader = new BufferedReader(reader);
        if (parser == null)
            this.parser = new DefaultLineParser();
        else
            this.parser = parser;
        nextExample = parseExample();
        
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public boolean hasNext() {
        if (nextExample == null)
            try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        return nextExample != null;
    }

    public ParseResult next() {
        ParseResult example = nextExample;
        try {
            nextExample = parseExample();
        } catch (ExampleParsingException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return example;
    }

    public void remove() {}

    protected ParseResult parseExample() throws ExampleParsingException {
        ParseResult example = null;
        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.startsWith(getComment()))
                    continue;
                return parser.parse(line);
            }
        } catch (IOException e) {
            throw new ExampleParsingException(e);
        }
        return example;
    }

    public static class DefaultLineParser implements ExampleParser {

        public static final String DEFAULT_SEPARATOR = " ";
        protected String separator = DEFAULT_SEPARATOR;

        public DefaultLineParser() {
            super();
        }

        public ParseResult parse(String line) {
            if (line.length() != 0)
                return new DefaultContentParserResult(line.split(getSeparator()));
            else
                return new DefaultContentParserResult(new String[0]);
        }

        public String getSeparator() {
            return separator;
        }

        public void setSeparator(String separator) {
            this.separator = separator;
        }

        public static class DefaultContentParserResult implements ParseResult {
            
            protected String[] example;
     
            public DefaultContentParserResult(String[] example) {
                this.example = example;
            }

            public String[] getContent() {
                return example;
            }

            public String[] getContext() {
                return null;
            }

        }

    }

}
