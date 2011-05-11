import gjb.flt.regex.RegexException;
import gjb.flt.regex.converters.Simplifier;
import gjb.util.cli.Application;
import gjb.util.tree.SExpressionParseException;

import java.io.BufferedReader;
import java.io.FileReader;

import javax.xml.transform.TransformerConfigurationException;

import org.apache.commons.cli.CommandLine;

/*
 * Created on Jul 8, 2008
 * Modified on $Date: 2009-10-27 14:14:01 $
 */

/**
 * @author gjb
 * @version $Revision: 1.1 $
 * 
 */
public class RegexSimplifier extends Application {

    protected Simplifier simplifier = null;
    
    public static void main(String[] args) {
        try {
            Application appl;
            appl = new RegexSimplifier();
            appl.run(args);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-Errors.RUNTIME_ERROR.ordinal());
        }
        System.exit(Errors.SUCCESS.ordinal());
    }

    public RegexSimplifier() throws TransformerConfigurationException {
        super();
        this.simplifier = new Simplifier();
    }

    @Override
    protected void action(CommandLine cl) throws Exception {
        if (cl.hasOption('r')) {
            String regexStr = cl.getOptionValue('r');
            simplify(regexStr);
        } else if (cl.hasOption('f')) {
            String fileName = cl.getOptionValue('f');
            BufferedReader reader = new BufferedReader(new FileReader(fileName));
            String line = null;
            while ((line = reader.readLine()) != null) {
                String regexStr = line.trim();
                simplify(regexStr);
            }
        } else {
            throw new RuntimeException("no input data given");
        }
    }

    protected void simplify(String regexStr)
            throws SExpressionParseException, RegexException {
        String simpleRegex = simplifier.simplify(regexStr);
        System.out.println(regexStr + "\t" + simpleRegex);
    }

    @Override
    protected void defineParameters() {
        addParameter("r", "regex", true, false,
                     "regular expression to be simplified");
        addParameter("f", "file", true, false,
                     "file containing the regular expressions to be simplified");
    }

}
