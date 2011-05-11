/**
 * Created on Nov 4, 2009
 * Modified on $Date: 2009-11-04 14:49:03 $
 */
package gjb.util.xml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.dom4j.io.XMLWriter;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * @author lucg5005
 * @version $Revision: 1.2 $
 *
 */
public class XmlDtdValidator {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	protected String docType;
	protected boolean isVerbose = false;
	protected boolean ignoreWarnings = true;
	protected static DocumentBuilderFactory builderFactory;
	protected static DocumentBuilder validatingBuilder;
	protected static DocumentBuilder nonvalidatingBuilder;
    protected static TransformerFactory transformationFactory;
    protected static Transformer transformer;

	static {
		builderFactory = DocumentBuilderFactory.newInstance();
		transformationFactory = TransformerFactory.newInstance();
	}

	public XmlDtdValidator(String docType)
	        throws ParserConfigurationException,
	               TransformerConfigurationException {
		this.docType = docType;
		if (validatingBuilder == null) {
			builderFactory.setValidating(true);
			validatingBuilder = builderFactory.newDocumentBuilder();
			validatingBuilder.setErrorHandler(new SimpleErrorHandler());
		}
		if (nonvalidatingBuilder == null) {
			builderFactory.setValidating(false);
			nonvalidatingBuilder = builderFactory.newDocumentBuilder();
			nonvalidatingBuilder.setErrorHandler(new SimpleErrorHandler());
		}
		if (transformer == null)
			transformer = transformationFactory.newTransformer();
	}

	public boolean isValid(String xmlFileName) throws FileNotFoundException {
		File xmlFile = new File(xmlFileName);
		return isValid(xmlFile);
	}

	public boolean isValid(File xmlFile) throws FileNotFoundException {
	    FileInputStream xmlStream = new FileInputStream(xmlFile);
		return isValid(xmlStream);
    }

	public boolean isValid(InputStream xmlStream) {
		org.w3c.dom.Document xmlDocument;
		try {
			xmlDocument = nonvalidatingBuilder.parse(xmlStream);
			return isValid(xmlDocument);
		} catch (SAXException e) {
			if (isVerbose())
				e.printStackTrace();
			return false;
		} catch (IOException e) {
			if (isVerbose()) {
				System.err.println("Fatal expection: " + e.getMessage());
				e.printStackTrace();
			}
			return false;
		}
    }

	public boolean isValid(org.w3c.dom.Document xmlDocument) {
		try {
			DOMSource source = new DOMSource(xmlDocument);
			File tmpFile = File.createTempFile("tmp", ".xml");
			StreamResult result = new StreamResult(new FileOutputStream(tmpFile));
			transformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, getDocType());
			transformer.transform(source, result);
			builderFactory.setValidating(true);
			InputStream tmpStream = new FileInputStream(tmpFile);
			validatingBuilder.parse(tmpStream);
		} catch (SAXException e) {
			if (isVerbose())
				e.printStackTrace();
			return false;
		} catch (IOException e) {
			if (isVerbose()) {
				System.err.println("Fatal expection: " + e.getMessage());
				e.printStackTrace();
			}
			return false;
		} catch (TransformerException e) {
			if (isVerbose()) {
				System.err.println("Fatal expection: " + e.getMessage());
				e.printStackTrace();
			}
			return false;
		}
		return true;
	}

	public boolean isValid(org.dom4j.Document xmlDocument) {
		try {
	        File tmpFile = File.createTempFile("tmp", ".xml");
	        XMLWriter xmlWriter = new XMLWriter(new FileWriter(tmpFile));
	        xmlWriter.write(xmlDocument);
	        xmlWriter.close();
	        InputStream xmlStream = new FileInputStream(tmpFile);
			org.w3c.dom.Document w3cDocument = nonvalidatingBuilder.parse(xmlStream);
			return isValid(w3cDocument);
		} catch (SAXException e) {
			if (isVerbose())
				e.printStackTrace();
			return false;
		} catch (IOException e) {
			if (isVerbose()) {
				System.err.println("Fatal expection: " + e.getMessage());
				e.printStackTrace();
			}
			return false;
        }
	}

	public String getDocType() {
    	return docType;
    }

	public boolean isVerbose() {
    	return isVerbose;
    }

	public void setVerbose(boolean isVerbose) {
    	this.isVerbose = isVerbose;
    }

	public boolean ignoresWarnings() {
    	return ignoreWarnings;
    }

	public void setIgnoreWarnings(boolean ignoreWarnings) {
    	this.ignoreWarnings = ignoreWarnings;
    }

	public class SimpleErrorHandler implements ErrorHandler {
		
		public void fatalError(SAXParseException e) throws SAXException {
			if (isVerbose())
				System.err.println("Line: " + e.getLineNumber() + "\n" +
				                   "Fatal Error: " + e.getMessage());
			throw e;
		}

		public void error(SAXParseException e) throws SAXParseException {
			if (isVerbose())
				System.err.println("Line: " + e.getLineNumber() + "\n" +
				                   "Error: " + e.getMessage());
			throw e;
		}

		public void warning(SAXParseException e) throws SAXParseException{
			if (isVerbose())
				System.err.println("Line: " + e.getLineNumber() + "\n" +
				                   "Warning: " + e.getMessage());
			if (!ignoresWarnings())
				throw e;
		}
		
	}

}
