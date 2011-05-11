/*
 * Created on Jul 10, 2008
 * Modified on $Date: 2009-10-27 14:11:40 $
 */
package gjb.util.sampling;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Reader;
import java.util.List;
import java.util.Properties;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.lang.StringUtils;

import gjb.util.cli.Application;

/**
 * @author gjb
 * @version $Revision: 1.1 $
 * 
 */
public class CliSampler extends Application {

    public static final String OUTPUT_SEPARATOR_FIELD = "output_separator";
    public static final String OUTPUT_SEPARATOR = "\t";

    public static void main(String[] args) {
        try {
            Application appl = new CliSampler();
            appl.run(args);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-Errors.RUNTIME_ERROR.ordinal());
        }
    }

    @Override
    protected void action(CommandLine cl) throws Exception {
        Reader reader = null;
        if (cl.hasOption("i")) {
            String inFileName = cl.getOptionValue("i");
            reader = new FileReader(inFileName);
        } else {
            reader = new InputStreamReader(System.in);
        }
        PrintWriter writer = null;
        if (cl.hasOption("o")) {
            String outFileName = cl.getOptionValue("o");
            File file = new File(outFileName);
            file.createNewFile();
            writer = new PrintWriter(new FileWriter(file));
        } else {
            writer = new PrintWriter(new OutputStreamWriter(System.out));
        }
        Properties properties = new Properties();
        properties.setProperty(OUTPUT_SEPARATOR_FIELD, OUTPUT_SEPARATOR);
        if (cl.hasOption("p")) {
            String propFileName = cl.getOptionValue("p");
            properties.load(new FileReader(propFileName));
        }
        int number = Integer.valueOf(cl.getOptionValue("n"));
        List<String[]> sample = ReaderSampler.sample(reader, number, properties);
        String outputSeparator = translate(properties.getProperty(OUTPUT_SEPARATOR_FIELD));
        for (String[] data : sample) {
            writer.println(StringUtils.join(data, outputSeparator));
            writer.flush();
        }
        reader.close();
        writer.close();
    }

    @Override
    protected void defineParameters() {
        addParameter("n", "number", true, true, "number of lines to sample");
        addParameter("i", "in", true, false, "input file");
        addParameter("o", "out", true, false, "output file");
        addParameter("p", "properties", true, false, "properties file");
    }

    protected String translate(String str) {
        if (str.equals("\\t"))
            return "\t";
        else
            return str;
    }

}
