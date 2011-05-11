/**
 * Created on Oct 29, 2009
 * Modified on $Date: 2009-11-04 14:51:03 $
 */
package gjb.flt.treegrammar;

import gjb.flt.treegrammar.data.DataGenerator;
import gjb.flt.treegrammar.data.StringGenerator;
import gjb.flt.treegrammar.generators.XMLGenerator;
import gjb.flt.treegrammar.io.RegularTreeGrammarReader;
import gjb.flt.treegrammar.io.UserObjectsReader;
import gjb.math.GaussianIntegerDistribution;
import gjb.math.IllDefinedDistributionException;
import gjb.math.UserDefinedDistribution;
import gjb.util.xml.XmlDtdValidator;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashSet;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.dom4j.Document;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

/**
 * @author lucg5005
 * @version $Revision: 1.6 $
 *
 */
public class XMLDocumentTest extends TestCase {

    public static Test suite() {
        return new TestSuite(XMLDocumentTest.class);
    }

	public void testParseGrammar() {
        final String grammar =
            "root = a#root-type\n" +
            "a#root-type -> ; (| (b#) (* (a#node-type)))\n" +
            "a#node-type -> ; (? (. (a#node-type) (a#root-type)))\n" +
            "b# -> ; (EPSILON)";
        Reader reader = new StringReader(grammar);
        RegularTreeGrammarReader grammarReader = new RegularTreeGrammarReader();
        try {
        	grammarReader.readGrammar(reader);
        } catch (SyntaxException e) {
        	fail("unexpected exception");
        }
	}

	public void testDocGeneration() {
		final String grammar =
			"root = a#root-type\n" +
			"a#root-type -> ; (. (b#) (a#node-type))\n" +
			"a#node-type -> ; (b#)\n" +
			"b# -> ; (EPSILON)";
		final String targetDoc = 
			"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n\n" +
			"<a>\n" +
			"  <b/>\n" +
			"  <a>\n" +
			"    <b/>\n" +
			"  </a>\n" +
			"</a>\n";
		Reader reader = new StringReader(grammar);
		XMLGenerator xmlGenerator = new XMLGenerator();
        RegularTreeGrammarReader grammarReader = new RegularTreeGrammarReader(xmlGenerator);
		try {
			XMLGrammar xmlGrammar = grammarReader.readGrammar(reader);
			String doc = serialize(xmlGenerator, xmlGrammar);
			assertEquals("document", targetDoc, doc);
		} catch (SyntaxException e) {
			fail("unexpected exception");
		} catch (MaxDepthExceededException e) {
			fail("unexpected exception");
        } catch (IOException e) {
        	fail("unexpected exception");
        }
	}
	
	public void testDocGenerationChoice() {
		final String grammar =
			"root = a#root-type\n" +
			"a#root-type -> ; (. (| (b#) (c#)) (a#node-type))\n" +
			"a#node-type -> ; (b#)\n" +
			"b# -> ; (EPSILON)\n" +
			"c# -> ; (EPSILON)";
		SortedSet<String> targetLanguage = new TreeSet<String>();
		targetLanguage.add(
			"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n\n" +
			"<a>\n" +
			"  <b/>\n" +
			"  <a>\n" +
			"    <b/>\n" +
			"  </a>\n" +
			"</a>\n"
		);
		targetLanguage.add( 
			"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n\n" +
			"<a>\n" +
			"  <c/>\n" +
			"  <a>\n" +
			"    <b/>\n" +
			"  </a>\n" +
			"</a>\n"
		);
		Reader reader = new StringReader(grammar);
		XMLGenerator xmlGenerator = new XMLGenerator();
        RegularTreeGrammarReader grammarReader = new RegularTreeGrammarReader(xmlGenerator);
		try {
			SortedSet<String> language = new TreeSet<String>();
			XMLGrammar xmlGrammar = grammarReader.readGrammar(reader);
			for (int i = 0; i < 64; i++) {
				String doc = serialize(xmlGenerator, xmlGrammar);
				language.add(doc);
				assertTrue("document in language", targetLanguage.contains(doc));
			}
			assertEquals("language (note: there's a low probability this test may fail due to a statistical fluke",
			             targetLanguage, language);
		} catch (SyntaxException e) {
			fail("unexpected exception");
		} catch (MaxDepthExceededException e) {
			fail("unexpected exception");
		} catch (IOException e) {
			fail("unexpected exception");
		}
	}
	
	public void testDocGenerationChoices() {
		final String grammar =
			"root = a#root-type\n" +
			"a#root-type -> ; (. (| (b#) (c#)) (a#node-type))\n" +
			"a#node-type -> ; (| (b#) (c#))\n" +
			"b# -> ; (EPSILON)\n" +
			"c# -> ; (EPSILON)";
		SortedSet<String> targetLanguage = new TreeSet<String>();
		targetLanguage.add(
		                   "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n\n" +
		                   "<a>\n" +
		                   "  <b/>\n" +
		                   "  <a>\n" +
		                   "    <b/>\n" +
		                   "  </a>\n" +
		                   "</a>\n"
		);
		targetLanguage.add( 
		                   "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n\n" +
		                   "<a>\n" +
		                   "  <c/>\n" +
		                   "  <a>\n" +
		                   "    <b/>\n" +
		                   "  </a>\n" +
		                   "</a>\n"
		);
		targetLanguage.add(
		                   "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n\n" +
		                   "<a>\n" +
		                   "  <b/>\n" +
		                   "  <a>\n" +
		                   "    <c/>\n" +
		                   "  </a>\n" +
		                   "</a>\n"
		);
		targetLanguage.add( 
		                   "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n\n" +
		                   "<a>\n" +
		                   "  <c/>\n" +
		                   "  <a>\n" +
		                   "    <c/>\n" +
		                   "  </a>\n" +
		                   "</a>\n"
		);
		Reader reader = new StringReader(grammar);
		XMLGenerator xmlGenerator = new XMLGenerator();
        RegularTreeGrammarReader grammarReader = new RegularTreeGrammarReader(xmlGenerator);
		try {
			SortedSet<String> language = new TreeSet<String>();
			XMLGrammar xmlGrammar = grammarReader.readGrammar(reader);
			for (int i = 0; i < 2048; i++) {
				String doc = serialize(xmlGenerator, xmlGrammar);
				language.add(doc);
				assertTrue("document in language", targetLanguage.contains(doc));
			}
			assertEquals("language (note: there's a low probability this test may fail due to a statistical fluke",
			             targetLanguage, language);
		} catch (SyntaxException e) {
			fail("unexpected exception");
		} catch (MaxDepthExceededException e) {
			fail("unexpected exception");
		} catch (IOException e) {
			fail("unexpected exception");
		}
	}

	public void testParseGrammarAttributes() {
        String grammar =
            "root = a#root-type\n" +
            "a#root-type -> q#CDATA?; (|[skewed] (b#) (* (a#node-type)))\n" +
            "a#node-type -> ; (?[skewed] (. (a#node-type) (a#root-type)))\n" +
            "b# -> r#CDATA, s#CDATA?; (EPSILON)";
        Reader reader = new StringReader(grammar);
        try {
        	XMLGenerator xmlGenerator = new XMLGenerator();
            DataGenerator cdata = new StringGenerator();
            xmlGenerator.addGenerator("CDATA", cdata);
            GaussianIntegerDistribution deptDistr = new GaussianIntegerDistribution(10.0, 2.0);
            deptDistr.setMin(5);
            deptDistr.setMax(15);
            xmlGenerator.setDepthDistribution(deptDistr);
            SortedMap<Integer,Double> map = new TreeMap<Integer,Double>();
            map.put(0, 0.1);
            map.put(1, 0.9);
            UserDefinedDistribution skewed = new UserDefinedDistribution(map);
            xmlGenerator.addDistribution("skewed", skewed);
            RegularTreeGrammarReader grammarReader = new RegularTreeGrammarReader(xmlGenerator);
	        grammarReader.readGrammar(reader);
        } catch (SyntaxException e) {
        	e.printStackTrace();
        	fail("unexpected exception");
        } catch (IllDefinedDistributionException e) {
        	e.printStackTrace();
        	fail("unexpected exception");
        }

	}

	public void testUserObjectGeneration() {
        try {
	        String userObjectShortStr =
	            "generator: short = gjb.flt.treegrammar.data.StringGenerator(min = 1, max = 3)";
	        String userObjectLongStr =
	            "generator: long = gjb.flt.treegrammar.data.StringGenerator(min = 15, max = 18)";
	        UserObject shortObj = new UserObject(userObjectShortStr);
	        UserObject longObj = new UserObject(userObjectLongStr);
	        DataGenerator shortGen = (DataGenerator) shortObj.getObject();
	        DataGenerator longGen = (DataGenerator) longObj.getObject();
	        Set<Integer> shortSizeTargets = new HashSet<Integer>();
	        shortSizeTargets.add(1);
	        shortSizeTargets.add(2);
	        shortSizeTargets.add(3);
	        Set<Integer> longSizeTargets = new HashSet<Integer>();
	        longSizeTargets.add(15);
	        longSizeTargets.add(16);
	        longSizeTargets.add(17);
	        longSizeTargets.add(18);
	        Set<Integer> shortSizes = new HashSet<Integer>();
	        Set<Integer> longSizes = new HashSet<Integer>();
	        for (int i = 0; i < 100; i++) {
	            final String shortStr = shortGen.getData();
	            final int shortLength = shortStr.length();
	        	assertTrue("short length", shortSizeTargets.contains(shortLength));
	        	shortSizes.add(shortLength);
	        	final String longStr = longGen.getData();
	        	final int longLength = longStr.length();
	        	assertTrue("long length", longSizeTargets.contains(longLength));
	        	longSizes.add(longLength);
	        }
	        assertEquals("short sizes", shortSizeTargets, shortSizes);
	        assertEquals("long sizes", longSizeTargets, longSizes);
        } catch (SyntaxException e) {
        	fail("unexpected exception");
        }
	}

	public void testParseGrammarUserObjects() {
        String userObjects =
            "distribution: skewed = gjb.math.UserDefinedDistribution(0 = 0.1, 1 = 0.9)\n" +
            "generator: CDATA = gjb.flt.treegrammar.data.StringGenerator()\n" +
            "distribution: depth = gjb.math.GaussianIntegerDistribution(mean = 10.0, variance = 2.0)";
        String grammar =
            "root = a#root-type[depth]\n" +
            "a#root-type -> q#CDATA?; (|[skewed] (b#) (* (a#node-type)))\n" +
            "a#node-type -> ; (?[skewed] (. (a#node-type) (a#root-type)))\n" +
            "b# -> r#CDATA, s#CDATA?; (EPSILON)";
        Reader userObjectReader = new StringReader(userObjects);
        XMLGenerator treeGenerator = new XMLGenerator();
        UserObjectsReader userObjectsReader = new UserObjectsReader(treeGenerator);
        RegularTreeGrammarReader treeGrammarReader = new RegularTreeGrammarReader(treeGenerator);
        Reader grammarReader = new StringReader(grammar);
        try {
        	userObjectsReader.readUserObjects(userObjectReader);
        	treeGrammarReader.readGrammar(grammarReader);
        } catch (SyntaxException e) {
        	fail("unexpected exception");
        }
	}

	public void testValidity() {
		final String userObjects =
			"generator: ID = gjb.flt.treegrammar.data.UniqueSymbolGenerator()\n" +
			"generator: NAME = gjb.flt.treegrammar.data.StringGenerator()";
        final String grammar =
            "root = addressBook#\n" +
            "addressBook# ->       ; (+ (address#))\n" +
            "address#     -> id#ID ; (. (name#) (firstname#) (* (company#)) (? (homepage#)))\n" +
            "name#        ->       ; (#NAME)\n" +
            "firstname#   ->       ; (EPSILON)\n" +
            "company#     ->       ; (EPSILON)\n" +
            "homepage#    ->       ; (EPSILON)";
        final String dtdFileName = "test-data/xmlFile1.dtd";
        final int nrDocuments = 50;
        Reader userObjectReader = new StringReader(userObjects);
        XMLGenerator treeGenerator = new XMLGenerator();
        UserObjectsReader userObjectsReader = new UserObjectsReader(treeGenerator);
        RegularTreeGrammarReader treeGrammarReader = new RegularTreeGrammarReader(treeGenerator);
        Reader grammarReader = new StringReader(grammar);
        try {
        	userObjectsReader.readUserObjects(userObjectReader);
        	XMLGrammar xmlGrammar = treeGrammarReader.readGrammar(grammarReader);
        	XmlDtdValidator validator = new XmlDtdValidator(dtdFileName);
        	validator.setVerbose(true);
        	for (int i = 0; i < nrDocuments; i++) {
        		Document xmlDocument = treeGenerator.generateExample(xmlGrammar);
        		assertTrue("valid", validator.isValid(xmlDocument));
        	}
        } catch (SyntaxException e) {
        	e.printStackTrace();
        	fail("unexpected exception");
        } catch (MaxDepthExceededException e) {
        	e.printStackTrace();
        	fail("unexpected exception");
        } catch (TransformerConfigurationException e) {
        	e.printStackTrace();
        	fail("unexpected exception");
        } catch (ParserConfigurationException e) {
        	e.printStackTrace();
        	fail("unexpected exception");
        }
	}

	protected String serialize(XMLGenerator xmlGenerator, XMLGrammar xmlDoc)
	        throws SyntaxException, MaxDepthExceededException, IOException {
		Document doc = xmlGenerator.generateExample(xmlDoc);
		OutputFormat format = OutputFormat.createPrettyPrint();
		Writer writer = new StringWriter();
		XMLWriter xmlWriter = new XMLWriter(writer, format);
		xmlWriter.write(doc);
		String str = writer.toString();
		return str;
	}

}
