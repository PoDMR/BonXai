package eu.fox7.bonxai.relaxng;

import org.junit.Test;

import eu.fox7.bonxai.relaxng.CombineMethod;
import eu.fox7.bonxai.relaxng.Define;
import eu.fox7.bonxai.relaxng.Empty;
import eu.fox7.bonxai.relaxng.ExternalRef;
import eu.fox7.bonxai.relaxng.Pattern;
import eu.fox7.bonxai.relaxng.Text;
import static org.junit.Assert.*;

/**
 * Test of class Define
 * @author lightsabre
 */
public class DefineTest extends junit.framework.TestCase {


    /**
     * Test of getName method, of class Define.
     */
    @Test
    public void testGetName() {
        Define instance = new Define("myDefineName");
        String expResult = "myDefineName";
        String result = instance.getName();
        assertEquals(expResult, result);
    }

    /**
     * Test of setName method, of class Define.
     */
    @Test
    public void testSetName() {
        Define instance = new Define("myDefineName");
        instance.setName("newName");
        String expResult = "newName";
        String result = instance.getName();
        assertEquals(expResult, result);
    }

    /**
     * Test of getCombineMethod method, of class Define.
     */
    @Test
    public void testGetCombineMethod() {
        Define instance = new Define("myDefineName");
        CombineMethod expResult = CombineMethod.choice;
        instance.setCombineMethod(expResult);
        CombineMethod result = instance.getCombineMethod();
        assertEquals(expResult, result);
        instance.setCombineMethod(CombineMethod.interleave);
        assertEquals(CombineMethod.interleave, instance.getCombineMethod());
    }

    /**
     * Test of setCombineMethod method, of class Define.
     */
    @Test
    public void testSetCombineMethod() {
        Define instance = new Define("myDefineName");
        CombineMethod expResult = CombineMethod.choice;
        instance.setCombineMethod(expResult);
        CombineMethod result = instance.getCombineMethod();
        assertEquals(expResult, result);
        instance.setCombineMethod(CombineMethod.interleave);
        assertEquals(CombineMethod.interleave, instance.getCombineMethod());
    }

    /**
     * Test of getPatterns method, of class Define.
     */
    @Test
    public void testGetPatterns() {
        Pattern pattern = new Empty();
        Pattern pattern2 = new ExternalRef("http://www.example.org/rng.rng");
        Pattern pattern3 = new Text();
        Define instance = new Define("2");
        instance.addPattern(pattern);
        instance.addPattern(pattern2);
        instance.addPattern(pattern3);
        assertEquals(pattern, instance.getPatterns().getFirst());
        assertEquals(pattern2, instance.getPatterns().get(1));
        assertEquals(pattern3, instance.getPatterns().getLast());
    }

    /**
     * Test of addPattern method, of class Define.
     */
    @Test
    public void testAddPattern() {
        Pattern pattern = new Empty();
        Pattern pattern2 = new ExternalRef("http://www.example.org/rng.rng");
        Pattern pattern3 = new Text();
        Define instance = new Define("2");
        instance.addPattern(pattern);
        instance.addPattern(pattern2);
        instance.addPattern(pattern3);
        assertEquals(pattern, instance.getPatterns().getFirst());
        assertEquals(pattern2, instance.getPatterns().get(1));
        assertEquals(pattern3, instance.getPatterns().getLast());
    }

}