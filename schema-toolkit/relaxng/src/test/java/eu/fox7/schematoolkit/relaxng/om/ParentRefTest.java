package eu.fox7.schematoolkit.relaxng.om;

import java.util.LinkedList;
import org.junit.Test;

import eu.fox7.schematoolkit.relaxng.om.Define;
import eu.fox7.schematoolkit.relaxng.om.Empty;
import eu.fox7.schematoolkit.relaxng.om.Grammar;
import eu.fox7.schematoolkit.relaxng.om.ParentRef;
import static org.junit.Assert.*;

/**
 * Test of class ParentRef
 * @author Lars Schmidt
 */
public class ParentRefTest extends junit.framework.TestCase {

    /**
     * Test of getDefineList method, of class ParentRef.
     */
    @Test
    public void testGetDefineList() {
        Grammar grammar = new Grammar();
        Grammar grammar2 = new Grammar();
        grammar2.setParentGrammar(grammar);
        Define define = new Define("test");
        define.addPattern(new Empty());
        grammar.addDefinePattern(define);
        ParentRef instance = new ParentRef("test", grammar2);
        java.util.List<Define> result = instance.getDefineList();
        assertTrue(result.contains(define));
    }

}