/**
 * Created on May 6, 2009
 * Modified on $Date: 2009-05-06 13:52:39 $
 */
package eu.fox7.xml.xsdanalyser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import eu.fox7.util.tree.SExpressionSerializer;
import eu.fox7.util.tree.Tree;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.XSDTypeDefinition;
import org.junit.Test;


/**
 * @author lucg5005
 * @version $Revision: 1.1 $
 *
 */
public class RegexAnalysisTest {

    private XSDSchema xsd=Analyser.loadSchema(this.getClass().getResource("/XSDs/test-03.xsd").getFile());

	@Test
    public void countingRegex() {
        RegexAnalysis regexAnalysis = new RegexAnalysis(xsd);
        assertEquals("number of regexes", 2, regexAnalysis.numberOfRegexes());
        Set<String> regexSet = new HashSet<String>();
        for (Iterator<XSDTypeDefinition> it = regexAnalysis.getTypeDefIterator(); it.hasNext(); ) {
            Tree tree = regexAnalysis.getRegex(it.next());
            SExpressionSerializer serializer = new SExpressionSerializer();
            String regex = serializer.serialize(tree);
            regexSet.add(regex);
        }
        assertEquals("number in set", 2, regexSet.size());
        assertTrue("alpha", regexSet.contains("(* (. (#a) (? (#b)) ({0,3} (#c)) ({2,5} (#d))))"));
    }

    @Test
    public void allRegex() {
    	RegexAnalysis regexAnalysis = new RegexAnalysis(xsd);
    	assertEquals("number of regexes", 2, regexAnalysis.numberOfRegexes());
    	Set<String> regexSet = new HashSet<String>();
    	for (Iterator<XSDTypeDefinition> it = regexAnalysis.getTypeDefIterator(); it.hasNext(); ) {
    		Tree tree = regexAnalysis.getRegex(it.next());
    		SExpressionSerializer serializer = new SExpressionSerializer();
    		String regex = serializer.serialize(tree);
    		regexSet.add(regex);
    	}
    	assertEquals("number in set", 2, regexSet.size());
    	assertTrue("beta", regexSet.contains("(% (#a) (? (#c)))"));
    }
    
}
