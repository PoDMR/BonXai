/*
 * Created on Mar 19, 2007
 * Modified on $Date: 2009-11-09 13:14:38 $
 */
package gjb.util.xml;

import gjb.util.xml.acstring.AncestorChildrenDocumentIterator;

import java.io.StringReader;
import java.util.Iterator;

import org.apache.commons.lang.StringUtils;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * @author gjb
 * @version $Revision: 1.1 $
 * 
 */
public class AncestorChildrenDocumentIteratorTest extends TestCase {

    public static Test suite() {
        return new TestSuite(AncestorChildrenDocumentIteratorTest.class);
    }

    protected String[] docs = {
            "/schema ::- element element\n" +
            "/schema/element ::- \n" +
            "/schema/element ::- \n",
            "/schema ::- annotation element complextype\n" +
            "/schema/annotation ::- \n" +
            "/schema/element ::- \n" +
            "/schema/complextype ::- \n",
            "/schema ::- element\n" +
            "/schema/element ::- \n"
    };
    protected int[] docSizes = {3, 4, 2};
    protected String sample = StringUtils.join(docs);

    public void test_shortExample() throws Exception {
        int counter = 0;
        Iterator<String> docIt = new AncestorChildrenDocumentIterator(new StringReader(sample));
        for ( ; docIt.hasNext(); ) {
            String doc = docIt.next();
            assertEquals("doc " + (counter + 1),
                         docSizes[counter],
                         doc.split("\n").length);
            assertEquals("doc" + (counter + 1) + " contents",
                         docs[counter],
                         doc);
            counter++;
        }
        assertEquals("number of docs", 3, counter);
        
    }

    public void test_shortExampleOffset() throws Exception {
        int counter = 0;
        Iterator<String> docIt = new AncestorChildrenDocumentIterator(new StringReader(sample),
                                                                      1, Integer.MAX_VALUE);
        for ( ; docIt.hasNext(); ) {
            String doc = docIt.next();
            assertEquals("doc " + (counter + 1),
                         docSizes[counter + 1],
                         doc.split("\n").length);
            assertEquals("doc" + (counter + 1) + " contents",
                         docs[counter + 1],
                         doc);
            counter++;
        }
        assertEquals("number of docs", 2, counter);
    }

    public void test_shortExampleNumberToRead() throws Exception {
        int counter = 0;
        Iterator<String> docIt = new AncestorChildrenDocumentIterator(new StringReader(sample),
                                                                      0, 2);
        for ( ; docIt.hasNext(); ) {
            String doc = docIt.next();
            assertEquals("doc " + (counter + 1),
                         docSizes[counter],
                         doc.split("\n").length);
            assertEquals("doc" + (counter + 1) + " contents",
                         docs[counter],
                         doc);
            counter++;
        }
        assertEquals("number of docs", 2, counter);
    }
    
    public void test_shortExampleOffsetNumberToRead() throws Exception {
        int counter = 0;
        Iterator<String> docIt = new AncestorChildrenDocumentIterator(new StringReader(sample),
                                                                      1, 1);
        for ( ; docIt.hasNext(); ) {
            String doc = docIt.next();
            assertEquals("doc " + (counter + 1),
                         docSizes[counter + 1],
                         doc.split("\n").length);
            assertEquals("doc" + (counter + 1) + " contents",
                         docs[counter + 1],
                         doc);
            counter++;
        }
        assertEquals("number of docs", 1, counter);
    }
    
    public void test_allXsds() throws Exception {
        final String sampleFileName = this.getClass().getResource("/test-data/ancestor-strings.txt").getFile();
        int counter = 0;
        for (Iterator<String> docIt = new AncestorChildrenDocumentIterator(sampleFileName); docIt.hasNext(); docIt.next()) {
            counter++;
        }
        assertEquals("number of docs", 697, counter);
    }

}