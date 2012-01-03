package eu.fox7.schematoolkit.xsd.writer;

import java.io.StringWriter;

import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.*;

public abstract class TestHelper
{
    protected static void print(Node node)
    {
        System.out.println(getString(node));
    }


    protected static String getString(Node node)
    {
        TransformerFactory transformerFactory;
        Transformer transformer;
        DOMSource source;
        StringWriter sw;
        String xsdString = "";

        try
        {
            sw = new StringWriter();

            transformerFactory = TransformerFactory.newInstance();
            transformerFactory.setAttribute("indent-number", 4);

            transformer = transformerFactory.newTransformer();
            source = new DOMSource(node);
            StreamResult result = new StreamResult(sw);

            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.STANDALONE, "no");
            transformer.setOutputProperty(OutputKeys.METHOD, "xml");
            transformer.transform(source, result);
            xsdString = sw.getBuffer().toString();
        } catch (Exception e)
        {
        }

        return xsdString;
    }
}
