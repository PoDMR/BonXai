/*
 * Created on Nov 24, 2005
 * Modified on $Date: 2009-10-27 14:14:01 $
 */
package gjb.flt.regex.converters;

import gjb.flt.regex.InvalidXMLException;
import gjb.flt.regex.NoRegularExpressionDefinedException;
import gjb.flt.regex.Regex;
import gjb.flt.regex.RegexException;
import gjb.flt.regex.UnknownOperatorException;
import gjb.flt.regex.io.XMLReader;
import gjb.flt.regex.io.XMLWriter;
import gjb.util.tree.SExpressionParseException;

import java.io.File;

import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.stream.StreamSource;

import org.dom4j.Document;
import org.dom4j.io.DocumentResult;
import org.dom4j.io.DocumentSource;

/**
 * @author gjb
 * @version $Revision: 1.1 $
 * 
 */
public class Transformer {

    public static final String TRANSFORMER_PATH = "gjb.util.regex.transformerPath";
	protected javax.xml.transform.Transformer[] transformers;

    public Transformer(String path, String[] transformerNames)
            throws TransformerConfigurationException {
        init(path, transformerNames);
    }

    protected void init(String path, String[] transformerNames)
            throws TransformerFactoryConfigurationError,
                   TransformerConfigurationException {
        TransformerFactory factory = TransformerFactory.newInstance();
        transformers = new javax.xml.transform.Transformer[transformerNames.length];
        if (System.getProperties().containsKey(TRANSFORMER_PATH))
        	path = System.getProperty(TRANSFORMER_PATH) +
        	    (path != null ? File.separator + path : "");
        for (int i = 0; i < transformerNames.length; i++) {
            String fileName = path + File.separator + transformerNames[i];
            transformers[i] = factory.newTransformer(new StreamSource(fileName));
        }
    }

    public String transform(String regexStr)
            throws SExpressionParseException, RegexException {
        return transform(new Regex(regexStr));
    }

    public String transform(Regex regex)
            throws SExpressionParseException, RegexException {
        XMLWriter xmlWriter = new XMLWriter(null);
        Document doc = xmlWriter.toXML(regex);
        String oldStr = null;
        String newStr = doc.asXML();
        while (!newStr.equals(oldStr)) {
            oldStr = newStr;
            for (int i = 0; i < transformers.length; i++) {
                DocumentResult result = new DocumentResult();
                try {
	                transformers[i].transform(new DocumentSource(doc),
	                                          result);
                } catch (TransformerException e) {
                	throw new RegexException("XML transformation exception", e);
                }
                doc = result.getDocument();
            }
            newStr = doc.asXML();
        }
        XMLReader xmlReader = new XMLReader(null, regex.getProperties());
        try {
	        regex = xmlReader.fromXML(doc);
        } catch (InvalidXMLException e) {
        	throw new RuntimeException(e);
        }
        return regex.toString();
    }

    public Regex transformRegex(Regex regex) throws NoRegularExpressionDefinedException
             {
        XMLWriter xmlWriter = new XMLWriter(null);
        Document doc = xmlWriter.toXML(regex);
        String oldStr = null;
        String newStr = doc.asXML();
        while (!newStr.equals(oldStr)) {
            oldStr = newStr;
            for (int i = 0; i < transformers.length; i++) {
                DocumentResult result = new DocumentResult();
                try {
	                transformers[i].transform(new DocumentSource(doc),
	                                          result);
                } catch (TransformerException e) {
                	throw new RuntimeException(e);
                }
                doc = result.getDocument();
            }
            newStr = doc.asXML();
        }
        XMLReader xmlReader = new XMLReader(null, regex.getProperties());
        try {
	        regex = xmlReader.fromXML(doc);
        } catch (UnknownOperatorException e) {
        	throw new RuntimeException(e);
        } catch (InvalidXMLException e) {
        	throw new RuntimeException(e);
        }
        return regex;
    }

}
