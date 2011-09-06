package eu.fox7.bonxai.relaxng;

import org.junit.Test;

import eu.fox7.bonxai.relaxng.Data;
import eu.fox7.bonxai.relaxng.Empty;
import eu.fox7.bonxai.relaxng.ExternalRef;
import eu.fox7.bonxai.relaxng.Param;
import eu.fox7.bonxai.relaxng.Pattern;
import eu.fox7.bonxai.relaxng.Text;
import static org.junit.Assert.*;

/**
 * Test of class Data
 * @author Lars Schmidt
 */
public class DataTest extends junit.framework.TestCase {

    /**
     * Test of getType method, of class Data.
     */
    @Test
    public void testGetType() {
        Data instance = new Data("type");
        String expResult = "type";
        String result = instance.getType();
        assertEquals(expResult, result);
    }

    /**
     * Test of getParams method, of class Data.
     */
    @Test
    public void testGetParams() {
        Param param = new Param("name1");
        Param param2 = new Param("2name");
        Param param3 = new Param("na3me");
        Data instance = new Data("type");
        instance.addParam(param);
        instance.addParam(param2);
        instance.addParam(param3);
        assertEquals(param, instance.getParams().getFirst());
        assertEquals(param2, instance.getParams().get(1));
        assertEquals(param3, instance.getParams().getLast());
    }

    /**
     * Test of addParam method, of class Data.
     */
    @Test
    public void testAddParam() {
        Param param = new Param("name1");
        Param param2 = new Param("2name");
        Param param3 = new Param("na3me");
        Data instance = new Data("type");
        instance.addParam(param);
        instance.addParam(param2);
        instance.addParam(param3);
        assertEquals(param, instance.getParams().getFirst());
        assertEquals(param2, instance.getParams().get(1));
        assertEquals(param3, instance.getParams().getLast());
    }

    /**
     * Test of getExceptPatterns method, of class Data.
     */
    @Test
    public void testGetExceptPatterns() {
        Pattern pattern = new Empty();
        Pattern pattern2 = new ExternalRef("http://www.example.org/rng.rng");
        Pattern pattern3 = new Text();
        Data instance = new Data("type");
        instance.addExceptPattern(pattern);
        instance.addExceptPattern(pattern2);
        instance.addExceptPattern(pattern3);
        assertEquals(pattern, instance.getExceptPatterns().getFirst());
        assertEquals(pattern2, instance.getExceptPatterns().get(1));
        assertEquals(pattern3, instance.getExceptPatterns().getLast());
    }

    /**
     * Test of addExceptPattern method, of class Data.
     */
    @Test
    public void testAddExceptPattern() {
        Pattern pattern = new Empty();
        Pattern pattern2 = new ExternalRef("http://www.example.org/rng.rng");
        Pattern pattern3 = new Text();
        Data instance = new Data("type");
        instance.addExceptPattern(pattern);
        instance.addExceptPattern(pattern2);
        instance.addExceptPattern(pattern3);
        assertEquals(pattern, instance.getExceptPatterns().getFirst());
        assertEquals(pattern2, instance.getExceptPatterns().get(1));
        assertEquals(pattern3, instance.getExceptPatterns().getLast());
    }
}
