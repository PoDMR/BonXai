/**
 * Created on Nov 4, 2009
 * Modified on $Date: 2009-11-04 13:18:07 $
 */
package eu.fox7.util.xml;

import java.io.File;
import java.io.FileNotFoundException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;

import eu.fox7.util.xml.XmlDtdValidator;

import junit.framework.TestCase;

/**
 * @author lucg5005
 * @version $Revision: 1.1 $
 *
 */
public class XmlDtdValidatorTest extends TestCase {

	public void testValidDocument() {
		final String[] xmlFileName = {this.getClass().getResource("/test-data/xmlFile1.xml").getFile()};
		final String dtdFileNames = this.getClass().getResource("/test-data/xmlFile1.dtd").getFile();
		final boolean[] ok = {true};
		validate(dtdFileNames, xmlFileName, ok);
	}

	public void testInvalidDocument() {
		final String dtdFileName = this.getClass().getResource("/test-data/xmlFile1.dtd").getFile();
		final String[] xmlFileNames = {this.getClass().getResource("/test-data/xmlFile2.xml").getFile()};
		final boolean[] ok = {false};
		validate(dtdFileName, xmlFileNames, ok);
	}
	
	public void testValidMultipleDocuments() {
		final String dtdFileName = this.getClass().getResource("/test-data/xmlFile1.dtd").getFile();
		final String[] xmlFileNames = {
				this.getClass().getResource("/test-data/xmlFile1.xml").getFile(),
				this.getClass().getResource("/test-data/xmlFile2.xml").getFile(),
				this.getClass().getResource("/test-data/xmlFile3.xml").getFile()};
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
