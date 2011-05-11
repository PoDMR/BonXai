/**
 * Created on Nov 4, 2009
 * Modified on $Date: 2009-11-04 13:18:07 $
 */
package gjb.util.xml;

import java.io.File;
import java.io.FileNotFoundException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;

import junit.framework.TestCase;

/**
 * @author lucg5005
 * @version $Revision: 1.1 $
 *
 */
public class XmlDtdValidatorTest extends TestCase {

	public void testValidDocument() {
		final String[] xmlFileName = {"test-data/xmlFile1.xml"};
		final String dtdFileNames = "test-data/xmlFile1.dtd";
		final boolean[] ok = {true};
		validate(dtdFileNames, xmlFileName, ok);
	}

	public void testInvalidDocument() {
		final String dtdFileName = "test-data/xmlFile1.dtd";
		final String[] xmlFileNames = {"test-data/xmlFile2.xml"};
		final boolean[] ok = {false};
		validate(dtdFileName, xmlFileNames, ok);
	}
	
	public void testValidMultipleDocuments() {
		final String dtdFileName = "test-data/xmlFile1.dtd";
		final String[] xmlFileNames = {
				"test-data/xmlFile1.xml",
				"test-data/xmlFile2.xml",
				"test-data/xmlFile3.xml"};
		final boolean[] ok = {true, false, true};
		validate(dtdFileName, xmlFileNames, ok);
	}

	protected void validate(final String dtdFileName, final String[] xmlFileNames,
	                        boolean[] oks) {
	    try {
	    	File dtdFile = new File(dtdFileName);
	    	assertTrue("DTD file exists", dtdFile.exists());
	    	XmlDtdValidator validator = new XmlDtdValidator(dtdFileName);
	    	for (int i = 0; i < xmlFileNames.length; i++) {
	    		File xmlFile = new File(xmlFileNames[i]);
	    		assertTrue("XML file exists", xmlFile.exists());
	    		assertEquals("valid", oks[i], validator.isValid(xmlFileNames[i]));
	    	}
        } catch (TransformerConfigurationException e) {
	        e.printStackTrace();
	        fail("unexpected exception");
        } catch (ParserConfigurationException e) {
        	e.printStackTrace();
        	fail("unexpected exception");
        } catch (FileNotFoundException e) {
	        e.printStackTrace();
	        fail("unexpected exception: is the test data in place?");
        }
    }

}
