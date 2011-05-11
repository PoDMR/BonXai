/*
 * Created on May 25, 2007
 * Modified on $Date: 2009-11-12 22:19:36 $
 */
package gjb.flt.regex.random;

import gjb.flt.regex.Regex;
import gjb.flt.regex.random.ConfigurationException;
import gjb.flt.regex.random.RandomRegexFactory;
import gjb.util.tree.Node;
import gjb.util.tree.Tree;

import java.io.IOException;
import java.io.StringReader;
import java.util.Iterator;
import java.util.Properties;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * @author gjb
 * @version $Revision: 1.2 $
 * 
 */
public class RandomRegexFactoryTest extends TestCase {

    public static Test suite() {
        return new TestSuite(RandomRegexFactoryTest.class);
    }

    public void test_default() throws IOException, ConfigurationException {
        String propertiesStr =
            "alphabetSize=5\n";
        Properties properties = new Properties();
        properties.load(new StringReader(propertiesStr));
        RandomRegexFactory factory = new RandomRegexFactory(properties);
        for (int i = 0; i < 10; i++)
            try {
                Regex regex = factory.create();
                assertEquals("symbols", 5, countLeaves(regex.getTree()));
            } catch (ConfigurationException e) {
                fail("unexpected exception");
            }
    }

    public void test_alphabet() throws IOException, ConfigurationException {
        String propertiesStr =
            "alphabetSize=6\n" +
            "alphabet=a,b,c,d,e, f\n";
        Properties properties = new Properties();
        properties.load(new StringReader(propertiesStr));
        RandomRegexFactory factory = new RandomRegexFactory(properties);
        for (int i = 0; i < 10; i++)
            try {
                Regex regex = factory.create();
                assertEquals("symbols", 6, countLeaves(regex.getTree()));
            } catch (ConfigurationException e) {
                fail("unexpected exception");
            }
    }

    public void test_typeDistribution() throws IOException, ConfigurationException {
        String propertiesStr =
            "alphabetSize=6\n" +
            "alphabet=a,b,c,d,e, f\n" +
            "typeDistribution=3,2,4";
        Properties properties = new Properties();
        properties.load(new StringReader(propertiesStr));
        RandomRegexFactory factory = new RandomRegexFactory(properties);
        for (int i = 0; i < 10; i++)
            try {
                Regex regex = factory.create();
                assertEquals("symbols", 12, countLeaves(regex.getTree()));
            } catch (ConfigurationException e) {
                fail("unexpected exception");
            }
    }
    
    public void test_emptyString() throws IOException, ConfigurationException {
        String propertiesStr =
            "alphabetSize=0\n";
        Properties properties = new Properties();
        properties.load(new StringReader(propertiesStr));
        RandomRegexFactory factory = new RandomRegexFactory(properties);
        Regex regex = factory.create();
        Tree tree = regex.getTree();
        assertEquals("empty string", Regex.EPSILON_SYMBOL, tree.getRoot().getKey());
        assertEquals("children", 0, tree.getRoot().getNumberOfChildren());
    }

    protected int countLeaves(Tree tree) {
        int count = 0;
        for (Iterator<Node> it = tree.leaves(); it.hasNext(); it.next())
            count++;
        return count;
    }

}
