/*
 * Created on Oct 29, 2008
 * Modified on $Date: 2009-10-26 18:37:40 $
 */
package eu.fox7.util.xml;

import java.io.FileWriter;

import org.apache.commons.cli.CommandLine;
import org.dom4j.Document;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import eu.fox7.util.cli.Application;


/**
 * @author eu.fox7
 * @version $Revision: 1.1 $
 * 
 */
public class XmlFormatter extends Application {

    public static void main(String[] args) {
        try {
            Application appl = new XmlFormatter();
            appl.run(args);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-Errors.RUNTIME_ERROR.ordinal());
        }
    }

    @Override
    protected void action(CommandLine cl) throws Exception {
        SAXReader reader = new SAXReader();
        Document doc = null;
        if (cl.hasOption("i")) {
            String url = cl.getOptionValue("i");
            doc = reader.read(url);
        } else {
            doc = reader.read(System.in);
        }
        OutputFormat format = null;
        if (cl.hasOption("c"))
            format = OutputFormat.createCompactFormat();
        else {
            format = OutputFormat.createPrettyPrint();
            if (cl.hasOption("n")) {
                int size = Integer.parseInt(cl.getOptionValue("n"));
                format.setIndentSize(size);
            }
        }
        format.setSuppressDeclaration(cl.hasOption("s"));
        XMLWriter writer = null;
        if (cl.hasOption("o"))
            writer = new XMLWriter(new FileWriter(cl.getOptionValue("o")), format);
        else
            writer = new XMLWriter(System.out, format);
        writer.write(doc);
        writer.close();
    }

    @Override
    protected void defineParameters() {
        addParameter("i", "in-file", true, false, "input XML file to format");
        addParameter("o", "out-file", true, false, "output XML file");
        addParameter("c", "compact-format", false, false, "use compact format");
        addParameter("n", "indent-size", true, false, "indent size used in pretty print mode");
        addParameter("s", "suppress-declaration", false, false, "suppress the <?xml ...> line");
    }

}
