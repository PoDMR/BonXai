/*
 * Created on Mar 16, 2007
 * Modified on $Date: 2007-03-15 23:21:17 $
 */
package eu.fox7.xml.xsdanalyser;

import eu.fox7.util.tree.SExpressionSerializer;
import eu.fox7.util.tree.Tree;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.XSDTypeDefinition;
import org.junit.Test;
import static org.junit.Assert.*;


/**
 * @author gjb
 * @version $Revision: 1.1 $
 * 
 */
public class RestrictionTest {

    @Test
    public void restrictionRegexes() {
        XSDSchema xsd=Analyser.loadSchema(this.getClass().getResource("/XSDs/test-02.xsd").getFile());
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
        assertTrue("alpha", regexSet.contains("(* (. (#a) (? (#b)) (? (#c)) (? (#d))))"));
        assertTrue("beta", regexSet.contains("(* (. (#a) (#c)))"));
    }

}
