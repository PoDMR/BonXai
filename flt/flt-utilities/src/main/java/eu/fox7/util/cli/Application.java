/*
 * Created on Jul 3, 2008
 * Modified on $Date: 2009-10-26 18:37:40 $
 */
package eu.fox7.util.cli;

import eu.fox7.util.cli.Parameters.Parameter;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.MissingArgumentException;
import org.apache.commons.cli.MissingOptionException;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.PosixParser;
import org.apache.commons.cli.UnrecognizedOptionException;

/**
 * @author eu.fox7
 * @version $Revision: 1.1 $
 * 
 * Base class for command line applications, designed to eliminate some of the
 * trivial work involved in their development.  Classes that extend this class
 * must implement two methods: <code>defineParameters()</code> and
 * <code>action(CommandLineParser)</code> and a main method must comply to
 * a basic layout.
 * <dl>
 *   <dt><code>defineParameters()</code></dt>
 *   <dd> In this method, zero or more command line parameters are added to
 *        <code>parameters</code>, an instance of the <code>Parameters</code>
 *        class. To that end, <code>parameters</code> <code>add()</code> method
 *        is called. An example is given below:
 *        <code>
 *            protected void defineParameters() {
 *                addParameter("f", "file", true, true,
 *                             "file containing graph representation");
 *            }
 *        </code>
 *   </dd>
 *   <dt><code>action(CommandLineParser)</code></dt>
 *   <dd> This method defines what the command line program is supposed to do,
 *        an instance of <code>CommandLineParser</code> is passed to it, so
 *        that it has access to all command line options.
 *   </dd>
 *   <dt>main(String[])</dt>
 *   <dd> This method should instanciate the class and call the
 *        <code>run(String[])</code> method on it. Below is an example
 *        implementation:
 *        <code>
 *            public static void main(String[] args) {
 *                try {
 *                    Application appl = new SomeCliApplication();
 *                    appl.run(args);
 *                } catch (Exception e) {
 *                    e.printStackTrace();
 *                    System.exit(-Errors.RUNTIME_ERROR.ordinal());
 *                }
 *            }
 *        </code>
 *   </dd>
 * </dl>
 * 
 */
public abstract class Application {

    protected Parameters parameters;

    public Application() {
        super();
        this.parameters = new Parameters();
        parameters.add("?", "help", false, false, "print help message");
    }

    public final void run(String[] args) throws Exception {
        CommandLine cl = parseCommandLine(args);
        action(cl);
    }

    public final void addParameter(String flag, String longFlag,
                                   boolean hasValue, boolean isRequired,
                                   String comment) {
        this.parameters.add(flag, longFlag, hasValue, isRequired, comment);
    }

    /**
     * method that defines what the application is supposed to do
     * @param cl CommandLine object containing all information passed via
     *           the application's command line invocation
     * @throws Exception
     */
    protected abstract void action(CommandLine cl) throws Exception;

    /**
     * method to add command line parameters, typically just call the
     * <code>add()</code> method on the application's <code>Parameter</code>
     * instance
     */
    protected abstract void defineParameters();

    protected Options initOptions() {
        Options options = new Options();
        defineParameters();
        for (Parameter param : parameters) {
            Option option = new Option(param.flag(), param.longFlag(),
                                       param.hasValue(), param.comment());
            option.setRequired(param.isRequired);
            options.addOption(option);
        }
        return options;
    }

    protected CommandLine parseCommandLine(String[] args) {
        Options options = initOptions();
        CommandLineParser clParser = new PosixParser();
        try {
            CommandLine cl = clParser.parse(options, args);
            if (cl.hasOption("?")) {
                printHelp(options);
                System.exit(Errors.SUCCESS.ordinal());
            } else {
                return cl;
            }
        } catch (MissingOptionException e) {
            System.err.println("### " + e.getMessage());
            printHelp(options);
            System.exit(-Errors.MISSING_REQUIRED_OPTION.ordinal());
        } catch (MissingArgumentException e) {
            System.err.println("### " + e.getMessage());
            printHelp(options);
            System.exit(-Errors.MISSING_REQUIRED_ARGUMENT.ordinal());
        } catch (UnrecognizedOptionException e) {
            System.err.println("### " + e.getMessage());
            printHelp(options);
            System.exit(-Errors.UNDEFINED_OPTION.ordinal());
        } catch (org.apache.commons.cli.ParseException e) {
            System.err.println("### " + e.getMessage());
            printHelp(options);
            System.exit(-Errors.CLI_PARSE_EXCEPTION.ordinal());
        }
        return null;
    }

    protected void printHelp(Options options) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("java " + this.getClass().getCanonicalName(),
                            options);
    }

    protected enum Errors {
        SUCCESS,
        MISSING_REQUIRED_OPTION,
        MISSING_REQUIRED_ARGUMENT,
        UNDEFINED_OPTION,
        CLI_PARSE_EXCEPTION,
        INCONSISTENT_OPTIONS,
        FILE_READ_ERROR,
        RUNTIME_ERROR
    }

}
