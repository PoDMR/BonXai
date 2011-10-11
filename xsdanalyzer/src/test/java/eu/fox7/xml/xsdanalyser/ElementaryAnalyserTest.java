/*
 * Created on Mar 15, 2007
 * Modified on $Date: 2009-06-25 11:48:19 $
 */
package eu.fox7.xml.xsdanalyser;

import static org.junit.Assert.*;

import eu.fox7.util.tree.SExpressionSerializer;
import eu.fox7.util.tree.Tree;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.XSDTypeDefinition;
import org.junit.Test;

/**
 * @author gjb
 * @version $Revision: 1.2 $
 * 
 */
public class ElementaryAnalyserTest {

    private XSDSchema xsd = Analyser.loadSchema("XSDs/test-01.xsd");

    @Test
    public void elementNames() {
        ElementNameAnalysis elementNameAnalysis = new ElementNameAnalysis(xsd);
        assertEquals("number of elements", 2, elementNameAnalysis.getNumberOfElements());
        Map<String,Integer> counter = new HashMap<String,Integer>();
        for (Iterator<String> it = elementNameAnalysis.getElementNameIterator(); it.hasNext(); ) {
            String name = it.next();
            if (!counter.containsKey(name))
                counter.put(name, 0);
            counter.put(name, counter.get(name) + 1);
        }
        assertEquals("a", new Integer(1), counter.get("a"));
        assertEquals("b", new Integer(1), counter.get("b"));
    }

    @Test
    public void typeNames() {
        TypeAnalysis typeAnalysis = new TypeAnalysis(xsd);
        assertEquals("number of a types", 2, typeAnalysis.getTypes("#a").size());
        assertEquals("number of b types", 1, typeAnalysis.getTypes("#b").size());
    }

    @Test
    public void regexes() {
        RegexAnalysis regexAnalysis = new RegexAnalysis(xsd);
        assertEquals("number of regexes", 1, regexAnalysis.numberOfRegexes());
        for (Iterator<XSDTypeDefinition> it = regexAnalysis.getTypeDefIterator(); it.hasNext(); ) {
            Tree tree = regexAnalysis.getRegex(it.next());
            SExpressionSerializer serializer = new SExpressionSerializer();
            String regex = serializer.serialize(tree);
            assertEquals("regex", "(* (. (#a) (? (#b))))", regex);
        }
    }

}
