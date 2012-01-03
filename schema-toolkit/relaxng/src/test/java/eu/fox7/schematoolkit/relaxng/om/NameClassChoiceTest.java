package eu.fox7.schematoolkit.relaxng.om;

import java.util.LinkedHashSet;
import org.junit.Test;

import eu.fox7.schematoolkit.relaxng.om.Name;
import eu.fox7.schematoolkit.relaxng.om.NameClass;
import eu.fox7.schematoolkit.relaxng.om.NameClassChoice;
import static org.junit.Assert.*;

/**
 * Test of class NameClassChoice
 * @author Lars Schmidt
 */
public class NameClassChoiceTest extends junit.framework.TestCase {

    /**
     * Test of getChoiceNames method, of class NameClassChoice.
     */
    @Test
    public void testGetChoiceNames() {
        NameClassChoice instance = new NameClassChoice();
        Name name = new Name("myName");
        instance.addChoiceName(name);
        LinkedHashSet<NameClass> result = instance.getChoiceNames();
        assertTrue(result.contains(name));
    }

    /**
     * Test of addChoiceName method, of class NameClassChoice.
     */
    @Test
    public void testAddChoiceName() {
        NameClassChoice instance = new NameClassChoice();
        Name name = new Name("myName");
        instance.addChoiceName(name);
        LinkedHashSet<NameClass> result = instance.getChoiceNames();
        assertTrue(result.contains(name));
    }

}