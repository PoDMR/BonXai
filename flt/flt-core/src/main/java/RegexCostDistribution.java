/*
 * Created on Feb 24, 2005
 * Modified: $Date: 2009-10-27 14:14:01 $
 */
import eu.fox7.flt.regex.measures.CostDistribution;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStreamReader;

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

/**
 * <p>Program to calculate the cost distribution of a given sample with respect
 * to a specified regular expression.  The regular expression is given in the form
 * of an S-expression, the sample is read from a file that is specified, or from
 * stdin otherwise.  E.g.
 * <pre>
 *   java -cp flt.jar RegexCostDistribution -e '(. (a) (* (| (b) (c))) (? (d)))'
 * </pre>
 * or
 * <pre>
 *   java -cp flt.jar RegexCostDistribution -e '(. (a) (* (| (b) (c))) (? (d)))'
 *                                          -f sample.txt
 * </pre>
 * Help can be obtained as follows:
 * <pre>
 *   java -cp flt.jar RegexCostDistribution -h
 * </pre>
 * </p>
 * <p>The sample file lists one example per line, its tokens separated by whitespace.
 * <strong>Note that this implies that an empty line is interpreted as the empty
 * string.  This may lead to undesirable side effects.</strong>  Comments start with
 * a '#' and are ignored.  E.g.
 * <pre>
 * a b c b d
 * # the line below is the empty string
 * 
 * a b c c
 * a c c c
 * </pre>
 * would be a valid sample file.  <strong>Beware of trailing new lines!</strong></p>
 * 
 * @author eu.fox7
 * @version $Revision: 1.1 $
 */
public class RegexCostDistribution {

	public static void main(String[] args) {
		Options options = new Options();
		initOptions(options);
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
		} else {
			try {
				InputStreamReader reader;
				if (cl.hasOption("f")) {
					reader = new FileReader(cl.getOptionValue("f"));
				} else {
					reader = new InputStreamReader(System.in);
				}
				BufferedReader buffer = new BufferedReader(reader);
				String regexStr = null;
				if (cl.hasOption("e")) {
				    regexStr = cl.getOptionValue("e");
				} else {
				    BufferedReader regexReader = new BufferedReader(new FileReader(cl.getOptionValue("r")));
				    String line = null;
				    if ((line = regexReader.readLine()) != null) {
				        regexStr = line.trim();
				    } else {
				        throw new FileFormatException();
				    }
				}
				System.err.println("analysing for expression '" + regexStr + "'");
				CostDistribution cd = new CostDistribution(regexStr, null, buffer);
				for (int i = 0; i <= cd.getMaxMatchCost(); i++) {
				    System.out.println(i + "\t" + cd.getCost(i));
				}
				buffer.close();
			} catch (Exception e) {
			    System.err.println("exception: " + e.getMessage());
				System.exit(1);
			}
		}
	}

	protected static void initOptions(Options options) {
		Option fOption = new Option("f", "file", true,
									"input file name that contains the sample");
		fOption.setArgName("sampleFile");
		fOption.setRequired(false);
		Option eOption = new Option("e", "regex", true,
		                            "regular expression to evaluate the sample against");
		eOption.setArgName("regexStr");
		Option rOption = new Option("r", "regex-file", true,
		                            "file containing the regular expression");
		rOption.setArgName("regexFile");
		OptionGroup regexOptions = new OptionGroup();
		regexOptions.addOption(eOption);
		regexOptions.addOption(rOption);
		regexOptions.setRequired(true);
		Option hOption = new Option("h", "help", false,
									"help on using RegexCostDistribution");
		options.addOption(fOption);
		options.addOptionGroup(regexOptions);
		options.addOption(hOption);
	}

	protected static void printHelp(Options options) {
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp("java RegexCostDistribution [-f sampleFile] -e regex [-h]",
							options);
	}

}
