package eu.fox7.schematoolkit.relaxng.om;

import org.junit.Test;

import eu.fox7.schematoolkit.relaxng.om.Empty;
import eu.fox7.schematoolkit.relaxng.om.ExternalRef;
import eu.fox7.schematoolkit.relaxng.om.List;
import eu.fox7.schematoolkit.relaxng.om.Pattern;
import eu.fox7.schematoolkit.relaxng.om.Text;
import static org.junit.Assert.*;

/**
 * Test of class list
 * @author Lars Schmidt
 */
public class ListTest extends junit.framework.TestCase {

    /**
     * Test of getPatterns method, of class List.
     */
    @Test
    public void testGetPatterns() {
        Pattern pattern = new Empty();
        Pattern pattern2 = new ExternalRef("http://www.example.org/rng.rng");
        Pattern pattern3 = new Text();
        List instance = new List();
        instance.addPattern(pattern);
        instance.addPattern(pattern2);
        instance.addPattern(pattern3);
        assertEquals(pattern, instance.getPatterns().getFirst());
        assertEquals(pattern2, instance.getPatterns().get(1));
        assertEquals(pattern3, instance.getPatterns().getLast());
    }

    /**
     * Test of addPattern method, of class List.
     */
    @Test
    public void testAddPattern() {
        Pattern pattern = new Empty();
        Pattern pattern2 = new ExternalRef("http://www.example.org/rng.rng");
        Pattern pattern3 = new Text();
        List instance = new List();
        instance.addPattern(pattern3);
        instance.addPattern(pattern);
        instance.addPattern(pattern2);
        assertEquals(pattern3, instance.getPatterns().getFirst());
        assertEquals(pattern, instance.getPatterns().get(1));
        assertEquals(pattern2, instance.getPatterns().getLast());
    }
}
