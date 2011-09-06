package eu.fox7.bonxai.relaxng;

import java.util.LinkedList;
import org.junit.Test;

import eu.fox7.bonxai.relaxng.Define;
import eu.fox7.bonxai.relaxng.Empty;
import eu.fox7.bonxai.relaxng.Grammar;
import eu.fox7.bonxai.relaxng.Ref;
import static org.junit.Assert.*;

/**
 * Test of class Ref
 * @author Lars Schmidt
 */
public class RefTest extends junit.framework.TestCase {

    /**
     * Test of getDefineList method, of class Ref.
     */
    @Test
    public void testGetDefineList() {
        Grammar grammar = new Grammar();
        Define define = new Define("test");
        define.addPattern(new Empty());
        grammar.addDefinePattern(define);
        Ref instance = new Ref(grammar.getDefineLookUpTable().getReference("test"), grammar);
        LinkedList<Define> result = instance.getDefineList();
        assertTrue(result.contains(define));
    }

}