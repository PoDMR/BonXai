package de.tudortmund.cs.bonxai.relaxng;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test of class optional
 * @author Lars Schmidt
 */
public class OptionalTest extends junit.framework.TestCase {

    /**
     * Test of getPatterns method, of class Optional.
     */
    @Test
    public void testGetPatterns() {
        Pattern pattern = new Empty();
        Pattern pattern2 = new ExternalRef("http://www.example.org/rng.rng");
        Pattern pattern3 = new Text();
        Optional instance = new Optional();
        instance.addPattern(pattern);
        instance.addPattern(pattern2);
        instance.addPattern(pattern3);
        assertEquals(pattern, instance.getPatterns().getFirst());
        assertEquals(pattern2, instance.getPatterns().get(1));
        assertEquals(pattern3, instance.getPatterns().getLast());
    }

    /**
     * Test of addPattern method, of class Optional.
     */
    @Test
    public void testAddPattern() {
        Pattern pattern = new Empty();
        Pattern pattern2 = new ExternalRef("http://www.example.org/rng.rng");
        Pattern pattern3 = new Text();
        Optional instance = new Optional();
        instance.addPattern(pattern3);
        instance.addPattern(pattern);
        instance.addPattern(pattern2);
        assertEquals(pattern3, instance.getPatterns().getFirst());
        assertEquals(pattern, instance.getPatterns().get(1));
        assertEquals(pattern2, instance.getPatterns().getLast());
    }
}
