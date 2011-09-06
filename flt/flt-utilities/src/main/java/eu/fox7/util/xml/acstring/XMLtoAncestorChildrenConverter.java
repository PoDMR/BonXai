/*
 * Created on May 30, 2008
 * Modified on $Date: 2009-11-09 11:49:45 $
 */
package eu.fox7.util.xml.acstring;

import eu.fox7.util.xml.AbstractXMLSampler;
import eu.fox7.util.xml.ConfigurationException;

import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Iterator;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.MissingArgumentException;
import org.apache.commons.cli.MissingOptionException;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.PosixParser;
import org.apache.commons.cli.UnrecognizedOptionException;
import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.Element;

/**
 * @author eu.fox7
 * @version $Revision: 1.1 $
 * 
 */
public class XMLtoAncestorChildrenConverter extends AbstractXMLSampler {

    public static final String DEFAULT_EXTENSION = ".xml";
    public static final String CHILDREN_SEP = " ";
    public static final String ANC_SEP = "/";
    protected Writer writer;

    public static void main(String[] args) {
        CommandLine cl = parseCommandLine(args);
        Writer writer = null;
        String extension = DEFAULT_EXTENSION;
        if (cl.hasOption(Params.EXTENSION.flag()))
            extension = cl.getOptionValue(Params.EXTENSION.flag());
        if (cl.hasOption(Params.OUTPUT.flag())) {
            String fileName = cl.getOptionValue(Params.OUTPUT.flag());
            try {
                writer = new FileWriter(fileName);
            } catch (IOException e) {
                e.printStackTrace();
                System.err.println("### Can't open file '" + fileName + "' for writing");
                System.exit(-Errors.FILE_WRITE_ERROR.ordinal());
            }
        } else {
            writer = new OutputStreamWriter(System.out);
        }
        XMLtoAncestorChildrenConverter converter = new XMLtoAncestorChildrenConverter(writer);
        converter.setVerbose(cl.hasOption(Params.VERBOSE.flag()));
        converter.setFileSuffix(extension);
        File source = new File(cl.getOptionValue(Params.PATH.flag()));
        try {
            converter.parse(source);
        } catch (ConfigurationException e) {
            e.printStackTrace();
            System.err.println("### Configuration exception: '" + e.getMessage() + "'");
            System.exit(-Errors.CONFIGURATION_EXCEPTION.ordinal());
        } catch (IOException e) {
	        e.printStackTrace();
            System.err.println("### I/O exception: '" + e.getMessage() + "'");
            System.exit(-Errors.IOEXCEPTION_EXCEPTION.ordinal());
        }
    }

    public XMLtoAncestorChildrenConverter(Writer writer) {
        this.writer = writer;
    }

    @Override
    protected void add(Document document) throws ConfigurationException, IOException {
    	add(document.getRootElement());
    	writer.flush();
    }

    protected void add(Element element)
            throws ConfigurationException, IOException {
        ancestorPath.push(element);
        String ancStr = StringUtils.join(getAncestorString(), ANC_SEP);
        String childrenStr = StringUtils.join(getChildString(element), CHILDREN_SEP);
        writer.append(ANC_SEP);
        writer.append(ancStr);
        writer.append(" ");
        writer.append(AncestorChildrenExampleParser.DEFAULT_ANCESTOR_CHILDREN_SEP);
        writer.append(" ");
        writer.append(childrenStr);
        writer.append("\n");
        for (Iterator<?> elemIt = element.elementIterator(); elemIt.hasNext(); )
            add((Element) elemIt.next());
        ancestorPath.pop();
    }

    public static CommandLine parseCommandLine(String[] args) {
        Options options = initOptions();
        CommandLineParser clParser = new PosixParser();
        try {
            CommandLine cl = clParser.parse(options, args);
            if (cl.hasOption(Params.HELP.flag())) {
                printHelp(options);
                System.exit(Errors.SUCCESS.ordinal());
            } else {
                return cl;
            }
        } catch (MissingOptionException e) {
            System.err.println("### required option '" +
                               e.getMessage() + "' is missing");
            printHelp(options);
            System.exit(-Errors.MISSING_REQUIRED_OPTION.ordinal());
        } catch (MissingArgumentException e) {
            System.err.println("### required argument for option '" +
                               e.getMessage() + "' is missing");
            printHelp(options);
            System.exit(-Errors.MISSING_REQUIRED_ARGUMENT.ordinal());
        } catch (UnrecognizedOptionException e) {
            System.err.println("### option '" +
                               e.getMessage() + "' isn't recognized");
            printHelp(options);
            System.exit(-Errors.UNDEFINED_OPTION.ordinal());
        } catch (org.apache.commons.cli.ParseException e) {
            System.err.println("### " + e.getMessage());
            printHelp(options);
            System.exit(-Errors.CLI_PARSE_EXCEPTION.ordinal());
        }
        return null;
    }

    protected static Options initOptions() {
        Options options = new Options();
        for (Params param : Params.values()) {
            Option option = new Option(param.flag(), param.longFlag(),
                                       param.hasValue(), param.comment());
            option.setRequired(param.isRequired());
            options.addOption(option);
        }
        return options;
    }

    protected static void printHelp(Options options) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("java eu.fox7.util.schemax.XMLtoAncestorChildrenConverter",
                            options);
    }

    protected enum Errors {
        SUCCESS,
        MISSING_REQUIRED_OPTION,
        MISSING_REQUIRED_ARGUMENT,
        UNDEFINED_OPTION,
        CLI_PARSE_EXCEPTION,
        FILE_READ_ERROR,
        FILE_WRITE_ERROR,
        CONFIGURATION_EXCEPTION,
        IOEXCEPTION_EXCEPTION;
    }

    protected enum Params {
        PATH("p", "path", true,
             "path of the XML data, either a file or a directory", true),
        OUTPUT("o", "output", true, "output file name", false),
        EXTENSION("e", "extension", true, "file name extension of files to parse", false),
        HELP("h", "help", false, "print usage information", false),
        VERBOSE("v", "verbose", false, "get feedback during run", false);
        
        private String flag;
        private String longFlag;
        private boolean hasValue;
        private String comment;
        private boolean isRequired;
    
        Params(String flag, String longFlag, boolean hasValue, String comment,
               boolean isRequired) {
            this.flag = flag;
            this.longFlag = longFlag;
            this.hasValue = hasValue;
            this.comment = comment;
            this.isRequired = isRequired;
        }
    
        public String flag() {
            return flag;
        }
    
        public String longFlag() {
            return longFlag;
        }
    
        public boolean hasValue() {
            return hasValue;
        }
    
        public String comment() {
            return comment;
        }
    
        public boolean isRequired() {
            return isRequired;
        }

    }

    public static class XmlNameFilter implements FilenameFilter {

        protected String extension;

        public XmlNameFilter(String extension) {
            this.extension = extension;
        }

        public boolean accept(File dir, String filename) {
            return filename.toLowerCase().endsWith(extension);
        }

    }

}
