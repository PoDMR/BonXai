/*
 * Created on Aug 5, 2008
 * Modified on $Date: 2009-11-04 15:27:36 $
 */
package gjb.util.datafolder;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.dom4j.DocumentException;


/**
 * @author gjb
 * @version $Revision: 1.1 $
 * 
 */
public class DataFolderXmlTest extends TestCase {

    public static Test suite() {
        return new TestSuite(DataFolderXmlTest.class);
      }

    public void testSingleDataSet() {
        Data data = null;
        try {
            data = new Data("test data", "gjb");
            DataSet set = data.addDataSet("test set 1");
            set.addColumnHeader("item", Class.forName("java.lang.Integer"));
            set.addColumnHeader("frequency", Class.forName("java.lang.Double"));
            for (int i = 0; i < 5; i++) {
                set.addData(i);
                set.addData(Math.random());
            }
        } catch (SetupException e) {
            e.printStackTrace();
            fail("unexpected expression");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            fail("unexpected expression");
        } catch (AddDataModeViolationException e) {
            e.printStackTrace();
            fail("unexpected expression");
        }
        toXmlAndBackAndForth(data);
    }

    public void testTwoDataSets() {
        Data data = null;
        try {
            data = new Data("test data", "gjb");
            DataSet set = data.addDataSet("test set 1");
            set.addColumnHeader("item", Class.forName("java.lang.Integer"));
            set.addColumnHeader("frequency", Class.forName("java.lang.Double"));
            for (int i = 0; i < 5; i++) {
                set.addData(i);
                set.addData(Math.random());
            }
            set = data.addDataSet("test set 2");
            set.addColumnHeader("name", Class.forName("java.lang.String"));
            set.addColumnHeader("count", Class.forName("java.lang.Integer"));
            set.addColumnHeader("price", Class.forName("java.lang.Double"));
            for (int i = 1; i <= 7; i++) {
                set.addData("thingy " + i);
                set.addData(2*i);
                set.addData(Math.sqrt(i));
            }
        } catch (SetupException e) {
            e.printStackTrace();
            fail("unexpected expression");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            fail("unexpected expression");
        } catch (AddDataModeViolationException e) {
            e.printStackTrace();
            fail("unexpected expression");
        }
        toXmlAndBackAndForth(data);
    }
    
    public void testDataSetsAndGroups() {
        Data data = null;
        try {
            data = new Data("test data", "gjb");
            DataSet set = data.addDataSet("test set 1");
            set.addColumnHeader("item", Class.forName("java.lang.Integer"));
            set.addColumnHeader("frequency", Class.forName("java.lang.Double"));
            for (int i = 0; i < 5; i++) {
                set.addData(i);
                set.addData(Math.random());
            }
            AbstractDataCollection group = data.addGroup("group 1");
            set = group.addDataSet("test set 2");
            set.addColumnHeader("name", Class.forName("java.lang.String"));
            set.addColumnHeader("count", Class.forName("java.lang.Integer"));
            set.addColumnHeader("price", Class.forName("java.lang.Double"));
            for (int i = 1; i <= 7; i++) {
                set.addData("thingy " + i);
                set.addData(2*i);
                set.addData(Math.sqrt(i));
            }
            set = group.addDataSet("test set 3");
            set.addColumnHeader("x", Class.forName("java.lang.Double"));
            set.addColumnHeader("sin(x)", Class.forName("java.lang.Double"));
            for (double d = 0.0; d <= 1.0; d += 0.1) {
                set.addData(d);
                set.addData(Math.sin(d));
            }
        } catch (SetupException e) {
            e.printStackTrace();
            fail("unexpected expression");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            fail("unexpected expression");
        } catch (AddDataModeViolationException e) {
            e.printStackTrace();
            fail("unexpected expression");
        }
        toXmlAndBackAndForth(data);
    }

    protected void toXmlAndBackAndForth(Data data) {
        try {
            Writer strWriter = new StringWriter();
            DataWriter xmlWriter = new XMLDataWriter(strWriter);
            xmlWriter.write(data);
            xmlWriter.close();
            String targetXmlStr = strWriter.toString();
            StringReader strReader = new StringReader(targetXmlStr);
            DataReader xmlReader = new XMLDataReader(strReader);
            data = xmlReader.read();
            strWriter = new StringWriter();
            xmlWriter = new XMLDataWriter(strWriter);
            xmlWriter.write(data);
            xmlWriter.close();
            String xmlStr = strWriter.toString();
            assertEquals("xml equal", targetXmlStr, xmlStr);
        } catch (IOException e) {
            e.printStackTrace();
            fail("unexpected expression");
        } catch (DocumentException e) {
            e.printStackTrace();
            fail("unexpected expression");
        }
    }
}
