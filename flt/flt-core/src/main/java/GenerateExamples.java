import eu.fox7.flt.automata.factories.sparse.GlushkovFactory;
import eu.fox7.flt.automata.impl.sparse.SparseNFA;
import eu.fox7.flt.regex.generators.LanguageGenerator;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.MissingArgumentException;
import org.apache.commons.cli.MissingOptionException;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.PosixParser;
import org.apache.commons.cli.UnrecognizedOptionException;
import org.apache.commons.lang.StringUtils;

/*
 * Created on Mar 31, 2005
 * Modified on $Date: 2009-10-27 14:14:01 $
 */

/**
 * @author eu.fox7
 * @version $Revision: 1.1 $
 * 
 * <p> GenerateExamples generates strings in the language of a given regular
 * expression.  The regular expression can be specified in two ways, either
 * on the command line as a string following the <code>-e &lt;regex&gt;</code>
 * swith, or in a file with a name given following the
 * <code>-f &lt;file-name&gt;</code> switch.  In both cases, the regular
 * expression is formatted in prefix notation, e.g.,
 * <code>(+ (. (? (a)) (| (b) (c))))</code>. </p>
 * <p> Four generation methods are supported:
 * <ol>
 *   <li> all strings in the language upto and including a specified length
 *        using the <code>-l &lt;length&gt;</code>; </li>
 *   <li> using a tree representation the characteristic strings are generated
 *        with loops repeated a specified number of times, this mode is selected
 *        by the <code>-c &lt;loop-iterations&gt;</code> switch; </li> 
 *   <li> using a Glushkov automaton (indicated with the <code>-g</code> switch)
 *        with a given stop probability specified as 
 *        <code>-p &lt;stop-probability&gt;</code>; the lower this probability,
 *        the longer the typical strings generated; and </li>
 *   <li> using a tree representation of the regular expression (indicated with
 *        the <code>-t</code> switch) where the probability to repeat is given
 *        by the <code>-r &gt;repeat-probability&lt;</code> and the probability
 *        to ignore an optonial subexpression by
 *        <code>-o &gt;ignore-optional-probability&lt;</code> the length of the
 *        typical examples will increase with higher repeat probability,
 *        decrease with higher ignore optional probabilities. </li>
 * </ol>
 * </p>
 * <p> Lastly, the number for the latter two modes, the number of examples to
 * generate can be specified using the <code>-N &gt;number&lt;</code> switch. </p>
 * <p> Help can be obtained using the <code>-h</code> switch. </p>
 */
public class GenerateExamples {

    public static void main(String[] args) {
		Options options = initOptions();
		CommandLine cl = parseCommandLine(args, options);
		try {
		    String regexStr = createRegexString(cl);
		    if (cl.hasOption("g")) {
		        GlushkovFactory glushkov = new GlushkovFactory();
		        SparseNFA nfa = glushkov.create(regexStr);
		        if (!cl.hasOption("p")) {
		            throw new MissingArgumentException("p");
		        }
		        double prob = Double.parseDouble(cl.getOptionValue("p"));
		        int numberOfExamples = parseNumberOfExamples(cl);
		        if (cl.hasOption("v")) {
		            System.out.println("# regex: " + regexStr);
		            System.out.println("# prob: " + prob);
		            System.out.println("# N: " + numberOfExamples);
		        }
		        printExampleSampleWithStopProb(nfa, prob, numberOfExamples);
            } else if (cl.hasOption("l")) {
		        GlushkovFactory glushkov = new GlushkovFactory();
		        SparseNFA nfa = glushkov.create(regexStr);
                int length = Integer.parseInt(cl.getOptionValue("l"));
                if (cl.hasOption("v")) {
                    System.out.println("# regex: " + regexStr);
                    System.out.println("# length: " + length);
                }
                printAllExampleUptoLength(nfa, length);
		    } else if (cl.hasOption("t")) {
		        LanguageGenerator regex = new LanguageGenerator(regexStr);
		        if (!cl.hasOption("r")) {
		            throw new MissingOptionException("r");
		        }
                double repetitionStopProb = Double.parseDouble(cl.getOptionValue("r"));
		        if (!cl.hasOption("o")) {
		            throw new MissingOptionException("o");
		        }
		        double ignoreOptionProb = Double.parseDouble(cl.getOptionValue("o"));
		        regex.setAverageRepetitions(1.0/repetitionStopProb);
		        regex.setIgnoreOptionalProbability(ignoreOptionProb);
                int numberOfExamples = parseNumberOfExamples(cl);
                if (cl.hasOption("v")) {
                    System.out.println("# regex: " + regexStr);
                    System.out.println("# repetition stop probability: " + repetitionStopProb);
                    System.out.println("# ignore optional probability: " + ignoreOptionProb);
                    System.out.println("# N: " + numberOfExamples);
                }
		        for (int i = 0; i < numberOfExamples; i++) {
		            List<String> list = regex.generateRandomExample();
		            if (list != null) {
		                System.out.println(StringUtils.join(list.iterator(), " "));
		            }
		        }
		    } else if (cl.hasOption("c")) {
                LanguageGenerator regex = new LanguageGenerator(regexStr);
                int iterations = Integer.parseInt(cl.getOptionValue("c"));
                if (cl.hasOption("v")) {
                    System.out.println("# regex: " + regexStr);
                    System.out.println("# iterations: " + iterations);
                }
                SortedSet<String> examples = regex.getAllCharacteristicStrings(iterations);
                for (String example : examples) {
                    System.out.println(example);
                }
            }
		} catch (Exception e1) {
		    e1.printStackTrace();
		}
    }

    protected static int parseNumberOfExamples(CommandLine cl)
            throws NumberFormatException {
        int numberOfExamples = 1;
        if (cl.hasOption("N")) {
            numberOfExamples = Integer.parseInt(cl.getOptionValue("N"));
        }
        return numberOfExamples;
    }

    protected static void printExampleSampleWithStopProb(SparseNFA nfa, double prob,
                                                         int numberOfExamples) {
    	eu.fox7.flt.automata.generators.LanguageGenerator g = new eu.fox7.flt.automata.generators.LanguageGenerator(nfa);
        for (int i = 0; i < numberOfExamples; i++) {
            List<String> tokenList = g.generateRandomExample(prob);
            if (tokenList != null) {
                System.out.println(StringUtils.join(tokenList.iterator(), " "));
            }
        }
    }

    protected static void printAllExampleUptoLength(SparseNFA nfa, int length) {
    	eu.fox7.flt.automata.generators.LanguageGenerator g = new eu.fox7.flt.automata.generators.LanguageGenerator(nfa);
        for (Iterator<List<String>> it = g.generatingRun(length); it.hasNext();) {
            List<String> list = it.next();
            System.out.println(StringUtils.join(list.iterator(), " "));
        }
    }

    protected static String createRegexString(CommandLine cl)
            throws FileNotFoundException, IOException, FileFormatException {
        String regexStr = null;
        if (cl.hasOption("e")) {
            regexStr = cl.getOptionValue("e");
        } else {
            BufferedReader regexReader = new BufferedReader(new FileReader(cl.getOptionValue("f")));
            String line = null;
            if ((line = regexReader.readLine()) != null) {
                regexStr = line.trim();
            } else {
                throw new FileFormatException();
            }
        }
        return regexStr;
    }

	protected static CommandLine parseCommandLine(String[] args, Options options) {
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
    	if (cl.hasOption("h")) {
    		printHelp(options);
            System.exit(0);
    	}
        return cl;
    }

    protected static Options initOptions() {
        Options options = new Options();
		setRegexRepresentationOptions(options);
        setGenerationOptions(options);
        setHelpOptions(options);
        return options;
	}

    protected static void setHelpOptions(Options options)
            throws IllegalArgumentException {
        Option hOption = new Option("h", "help", false,
									"help on using RegexCostDistribution");
		options.addOption(hOption);
    }

    protected static void setGenerationOptions(Options options)
            throws IllegalArgumentException {
        OptionGroup actionGroup = new OptionGroup();
        setTreeBasedGenerationOptions(options, actionGroup);
		setGlushkovBasedGenerationOptions(options, actionGroup);
		setMaximalLengthGenerationOptions(actionGroup);
        setCharacteristicStringGenerationOptions(actionGroup);
		Option NOption = new Option("N", "number", true,
		                            "number of examples to generate");
        options.addOption(NOption);
        Option vOption = new Option("v", "verbose", false, "print parameters");
        options.addOption(vOption);
        options.addOptionGroup(actionGroup);
    }

    protected static void setCharacteristicStringGenerationOptions(OptionGroup actionGroup) throws IllegalArgumentException {
        Option cOption = new Option("c", "characteristic-strings", true,
                                    "number of times a multiplicity is instantiated");
        actionGroup.addOption(cOption);
    }

    protected static void setMaximalLengthGenerationOptions(OptionGroup actionGroup) throws IllegalArgumentException {
        Option lOption = new Option("l", "length", true,
		                            "length of the longest string to generate");
        actionGroup.addOption(lOption);
    }

    protected static void setGlushkovBasedGenerationOptions(Options options,
                                                            OptionGroup actionGroup)
            throws IllegalArgumentException {
        Option gOption = new Option("g", "glushkov", false,
		                            "generate examples from a Glushkov automaton");
        actionGroup.addOption(gOption);
        Option pOption = new Option("p", "prob", true,
                                    "probability to stop in a final state");
        options.addOption(pOption);
    }

    protected static void setTreeBasedGenerationOptions(Options options,
                                                        OptionGroup actionGroup)
            throws IllegalArgumentException {
        actionGroup.setRequired(true);

        Option tOption = new Option("t", "tree", false,
		                            "generate examples from a tree");
        actionGroup.addOption(tOption);
        Option rOption = new Option("r", "stop-repeat", true,
                                    "probability to stop in an iteration");
        options.addOption(rOption);
        Option oOption = new Option("o", "ignore-option", true,
                                    "probability to ignore an optional element");
        options.addOption(oOption);
    }

    protected static void setRegexRepresentationOptions(Options options) throws IllegalArgumentException {
        Option eOption = new Option("e", "regex", true,
		                            "regular expression to evaluate the sample against");
		Option fOption = new Option("f", "file", true,
		                            "file containing the regular expression");
		OptionGroup regexRepresentationOptions = new OptionGroup();
		regexRepresentationOptions.addOption(eOption);
		regexRepresentationOptions.addOption(fOption);
		regexRepresentationOptions.setRequired(true);
        options.addOptionGroup(regexRepresentationOptions);
    }

	protected static void printHelp(Options options) {
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp("java GenerateExamples",
							options);
	}


    }
