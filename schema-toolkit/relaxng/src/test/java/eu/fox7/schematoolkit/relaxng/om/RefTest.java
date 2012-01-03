package eu.fox7.schematoolkit.relaxng.om;

import java.util.LinkedList;
import org.junit.Test;

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
        Ref instance = new Ref("test", grammar);
        java.util.List<Define> result = instance.getDefineList();
        assertTrue(result.contains(define));
    }

}