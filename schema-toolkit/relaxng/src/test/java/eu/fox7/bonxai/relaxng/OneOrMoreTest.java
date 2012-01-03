package eu.fox7.bonxai.relaxng;

import org.junit.Test;

import eu.fox7.bonxai.relaxng.Empty;
import eu.fox7.bonxai.relaxng.ExternalRef;
import eu.fox7.bonxai.relaxng.OneOrMore;
import eu.fox7.bonxai.relaxng.Pattern;
import eu.fox7.bonxai.relaxng.Text;
import static org.junit.Assert.*;

/**
 * Test of class oneOrMore
 * @author Lars Schmidt
 */
public class OneOrMoreTest extends junit.framework.TestCase {

    /**
     * Test of getPatterns method, of class OneOrMore.
     */
    @Test
    public void testGetPatterns() {
        Pattern pattern = new Empty();
        Pattern pattern2 = new ExternalRef("http://www.example.org/rng.rng");
        Pattern pattern3 = new Text();
        OneOrMore instance = new OneOrMore();
        instance.addPattern(pattern);
        instance.addPattern(pattern2);
        instance.addPattern(pattern3);
        assertEquals(pattern, instance.getPatterns().getFirst());
        assertEquals(pattern2, instance.getPatterns().get(1));
        assertEquals(pattern3, instance.getPatterns().getLast());
    }

    /**
     * Test of addPattern method, of class OneOrMore.
     */
    @Test
    public void testAddPattern() {
        Pattern pattern = new Empty();
        Pattern pattern2 = new ExternalRef("http://www.example.org/rng.rng");
        Pattern pattern3 = new Text();
        OneOrMore instance = new OneOrMore();
        instance.addPattern(pattern3);
        instance.addPattern(pattern);
        instance.addPattern(pattern2);
        assertEquals(pattern3, instance.getPatterns().getFirst());
        assertEquals(pattern, instance.getPatterns().get(1));
        assertEquals(pattern2, instance.getPatterns().getLast());
    }
}
