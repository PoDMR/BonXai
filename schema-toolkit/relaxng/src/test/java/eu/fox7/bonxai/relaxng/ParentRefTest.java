package eu.fox7.bonxai.relaxng;

import java.util.LinkedList;
import org.junit.Test;

import eu.fox7.bonxai.relaxng.Define;
import eu.fox7.bonxai.relaxng.Empty;
import eu.fox7.bonxai.relaxng.Grammar;
import eu.fox7.bonxai.relaxng.ParentRef;
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
        Define define = new Define("test");
        define.addPattern(new Empty());
        grammar.addDefinePattern(define);
        ParentRef instance = new ParentRef(grammar.getDefineLookUpTable().getReference("test"), grammar, new Grammar());
        java.util.List<Define> result = instance.getDefineList();
        assertTrue(result.contains(define));
    }

}