/*
 * Created on Jun 28, 2005
 * Modified on $Date: 2009-11-03 15:48:34 $
 */
package gjb.flt.treegrammar.generators;


import gjb.flt.treegrammar.MaxDepthExceededException;
import gjb.flt.treegrammar.SyntaxException;
import gjb.flt.treegrammar.XMLGrammar;
import gjb.flt.treegrammar.io.RegularTreeGrammarReader;
import gjb.flt.treegrammar.io.UserObjectsReader;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.text.DecimalFormat;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.MissingArgumentException;
import org.apache.commons.cli.MissingOptionException;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.PosixParser;
import org.apache.commons.cli.UnrecognizedOptionException;
import org.dom4j.Document;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

/**
 * <p> XMLGenetor is the command line front-end for the XMLDocument XML document
 * generator.  Use
 * <pre>
 *   java -jar xmlGenerator -h
 * </pre>
 * to see the available options and usage information. </p>
 * <p> A grammar is specified using the following BNC:
 * <pre>
 *   grammar   -> root-spec rule-spec+
 *   root-spec -> 'root' '=' element ('[' distr ']')?
 *   element   -> name '#' type
 *   distr     -> name
 *   rule-spec -> element '->' attr-spec* ';' regex
 *   attr-spec -> '@' name '[' generator ']'
 *   generator -> '#' name
 *   regex     -> '(' ('EPSILON' | generator | element | unary-op| nary-op) ')' 
 *   unary-op  -> ('?' | '*' | '+') distr? regex
 *   nary-op   -> ('|' | '.') distr? regex regex+
 *   name      -> [A-Za-z] [A-za-z0-9_]*
 *   type      -> name
 *   value     -> number
 * </pre>
 * Generators and distributions are specified in a separate user objects file with
 * the following BNC:
 * <pre>
 *   userobjects -> (distr | generator | max-depth)+
 *   distr       -> name '=' class '(' (param (',' param)*)? ')'
 *   generator   -> name '=' class '(' (param (',' param)*)? ')'
 *   max-depth   -> 'max-depth' '=' [1-9][0-9]+
 *   param       -> name '=' value
 *   name        -> [A-Za-z] [A-za-z0-9_]*
 * </pre>
 * @author gjb
 * @version $Revision: 1.2 $
 * 
 */
public class XMLGenerationTool {

	public static void main(String[] args) {
		Options options = initOptions();
		CommandLine cl = initCommandLine(args, options);
		if (cl.hasOption("h")) {
			printHelp(options);
			System.exit(0);
		}
		String userObjectsFileName = null;
		if (cl.hasOption("u")) {
		    userObjectsFileName = cl.getOptionValue("u");
		}
		String grammarFileName = cl.getOptionValue("g");
		String output = null;
		if (cl.hasOption("o")) {
		    output = cl.getOptionValue("o");
		}
		int number = 1;
		if (cl.hasOption("n")) {
		    number = Integer.parseInt(cl.getOptionValue("n"));
		}
        int maxTries = 1;
        if (cl.hasOption("m")) {
            maxTries = Integer.parseInt(cl.getOptionValue("m"));
        }
        boolean isVerbose = false;
        if (cl.hasOption("v")) {
            isVerbose = true;
        }
		XMLGrammar xmlDoc = null;
		try {
			XMLGenerator treeGenerator = new XMLGenerator();
            if (userObjectsFileName != null) {
                if (isVerbose) System.err.println("reading user object file");
                initUserObjects(treeGenerator, userObjectsFileName);
                if (isVerbose) System.err.println("user object file read");
            } else if (isVerbose)
                System.err.println("no user object file given");
            if (isVerbose) System.err.println("reading grammar file");
            xmlDoc = initGrammar(treeGenerator, grammarFileName);
            if (isVerbose) System.err.println("grammar file read");
            if (number == 1) {
                Writer writer;
                if (output == null) {
                    writer = new OutputStreamWriter(System.out);
                } else {
                    writer = new FileWriter(output);
                }
                int tries = generateXML(treeGenerator, xmlDoc, maxTries, writer, isVerbose);
                writer.close();
                if (isVerbose) System.err.println("example generated after " +
                                                  tries + " tries");
            } else {
                DecimalFormat formatter = new DecimalFormat(computeFormat(number));
                Writer writer = null;
                for (int n = 1; n <= number; n++) {
                    if (isVerbose) System.err.println("generating example " + n);
                    if (output == null) {
                        writer = new OutputStreamWriter(System.out);
                    } else {
                        String suffix = "-" + formatter.format(n) + ".xml";
                        writer = new FileWriter(output + suffix);
                    }
                    int tries = generateXML(treeGenerator, xmlDoc, maxTries, writer, isVerbose);
                    if (isVerbose) System.err.println("example " + n +
                                                      " generated after " +
                                                      tries + " tries");
                    if (output != null)
                        writer.close();
                    else
                        writer.flush();
                }
                if (output == null)
                    writer.close();
            }
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
            System.exit(-6);
        } catch (IOException e1) {
            e1.printStackTrace();
            System.exit(-6);
        } catch (SyntaxException e1) {
            Exception embE = e1.getException();
            embE.printStackTrace();
            e1.printStackTrace();
            System.exit(-7);
        } catch (MaxDepthExceededException e1) {
            e1.printStackTrace();
            System.exit(-8);
        }
	}

    protected static Options initOptions() {
        Options options = new Options();
        Option uOption = new Option("u", "user-objects", true,
                                    "user objects definitions file");
        uOption.setArgName("file-name");
        options.addOption(uOption);
        Option gOption = new Option("g", "grammar-file", true,
                                    "grammar file");
        gOption.setArgName("file-name");
        gOption.setRequired(true);
        options.addOption(gOption);
        Option oOption = new Option("o", "output", true,
                                    "(base) name of the output file(s), i.e. if multiple files are to be generated, <output> is treated as a prefix and file names of the form 'output-n.xml' are constructed.  For a single file, output will be its file name ");
        oOption.setArgName("output");
        options.addOption(oOption);
    	Option nOption = new Option("n", "number", true,
    	                            "number of example files to generate");
    	nOption.setArgName("number");
        Option mOption = new Option("m", "max-tries", true,
                                    "maximum number of tries for maximum depth violations");
        mOption.setArgName("max-tries");
    	options.addOption(nOption);
        options.addOption(mOption);
        Option vOption = new Option("v", "verbose", false, "verbose output");
        options.addOption(vOption);
    	Option hOption = new Option("h", "help", false,
    								"help on using RegexCostDistribution");
    	options.addOption(hOption);
    	return options;
    }

    protected static CommandLine initCommandLine(String[] args,
                                                 Options options) {
        CommandLineParser clParser = new PosixParser();
    	CommandLine cl = null;
    	try {
    		cl = clParser.parse(options, args);
    	} catch (MissingOptionException e) {
    		System.err.println("### required option '" +
    						   e.getMessage() + "' is missing");
    		printHelp(options);
    		System.exit(-1);
    	} catch (MissingArgumentException e) {
    		System.err.println("### required argument for option '" +
    						   e.getMessage() + "' is missing");
    		printHelp(options);
    		System.exit(-2);
    	} catch (UnrecognizedOptionException e) {
    		System.err.println("### option '" +
    						   e.getMessage() + "' isn't recognized");
    		printHelp(options);
    		System.exit(-3);
    	} catch (org.apache.commons.cli.ParseException e) {
    		System.err.println("### " + e.getMessage());
    		printHelp(options);
    		System.exit(-4);
    	}
        return cl;
    }

    protected static void initUserObjects(XMLGenerator treeGenerator,
                                          String userObjectsFileName)
            throws FileNotFoundException, IOException {
        Reader userObjectReader = new FileReader(userObjectsFileName);
        UserObjectsReader userObjectsReader = new UserObjectsReader(treeGenerator);
        try {
            userObjectsReader.readUserObjects(userObjectReader);
        } catch (SyntaxException e1) {
            e1.printStackTrace();
            Exception embE = e1.getException();
            if (embE != null) {
                embE.printStackTrace();
            }
            System.exit(-5);
        }
        userObjectReader.close();
    }

    protected static XMLGrammar initGrammar(XMLGenerator treeGenerator,
                                            String grammarFileName)
            throws FileNotFoundException, IOException {
        Reader grammarReader = new FileReader(grammarFileName);
        RegularTreeGrammarReader treeGrammarReader = new RegularTreeGrammarReader(treeGenerator);
        XMLGrammar xmlGrammar = null;
        try {
            xmlGrammar = treeGrammarReader.readGrammar(grammarReader);
        } catch (SyntaxException e1) {
            e1.printStackTrace();
            Exception embE = e1.getException();
            if (embE != null) {
                embE.printStackTrace();
            }
            System.exit(-5);
        } finally {
        	grammarReader.close();
        }
        return xmlGrammar;
    }

    protected static int generateXML(XMLGenerator treeGenerator, XMLGrammar xmlDoc, int maxTries,
                                     Writer writer, boolean isVerbose)
            throws IOException, SyntaxException, MaxDepthExceededException {
        Document doc = null;
        int tries = 1;
        do {
            try {
                if (isVerbose) System.err.println("generating example: try " +
                                                  tries);
                doc = treeGenerator.generateExample(xmlDoc);
            } catch (MaxDepthExceededException e) {
                if (tries >= maxTries) throw e;
                tries++;
            }
        } while (doc == null);
        OutputFormat format = OutputFormat.createPrettyPrint();
        XMLWriter xmlWriter = new XMLWriter(writer, format);
        xmlWriter.write(doc);
        
        return tries;
    }

    protected static String computeFormat(int number) {
        String formatStr = "0";
        for (int i = 0; i < Math.floor(Math.log10(number)); i++) {
            formatStr += "0";
        }
        return formatStr;
    }

    protected static void printHelp(Options options) {
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp("java gjb.xml.model.XMLGenerator -g grammar-file [-u user-objects-file] [-o output] [-n number] [-m max-tries] [-h]",
							options);
	}

}
