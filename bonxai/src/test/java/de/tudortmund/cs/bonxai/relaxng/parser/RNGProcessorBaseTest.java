package de.tudortmund.cs.bonxai.relaxng.parser;

import com.sun.org.apache.xerces.internal.dom.CoreDocumentImpl;
import com.sun.org.apache.xerces.internal.dom.ElementImpl;
import de.tudortmund.cs.bonxai.relaxng.Text;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;
import org.w3c.dom.Node;

/**
 * Test of class RNGProcessorBase
 * @author Lars Schmidt
 */
public class RNGProcessorBaseTest extends junit.framework.TestCase {

    @After
    @Override
    public void tearDown() {
        RNGProcessorBase.setDebug(false);
    }

    /**
     * Test of setDebug method, of class RNGProcessorBase.
     */
    @Test
    public void testSetDebug() {
        Boolean debug = false;
        RNGProcessorBase.setDebug(debug);
        assertFalse(RNGProcessorBase.getDebug());
        RNGProcessorBase.setDebug(true);
        assertTrue(RNGProcessorBase.getDebug());
    }

    /**
     * Test of getDebug method, of class RNGProcessorBase.
     */
    @Test
    public void testGetDebug() {
        Boolean debug = false;
        RNGProcessorBase.setDebug(debug);
        assertFalse(RNGProcessorBase.getDebug());
        RNGProcessorBase.setDebug(true);
        assertTrue(RNGProcessorBase.getDebug());
    }
 
    /**
     * Test of valueIsValid method, of class RNGProcessorBase.
     */
    @Test
    public void testValueIsValid() {
        RNGProcessorBase instance = new RNGProcessorBase(null) {

            @Override
            protected Object processNode(Node node) throws Exception {
                return null;
            }

            @Override
            protected void processChild(Node childNode) throws Exception {
            }
        };
        String value = "element";
        boolean expResult = true;
        boolean result = instance.valueIsValid(value);
        assertEquals(expResult, result);
        assertFalse(instance.valueIsValid("Element"));
    }

    /**
     * Test of setPatternAttributes method, of class RNGProcessorBase.
     * @throws Exception 
     */
    @Test
    public void testSetPatternAttributes() throws Exception {
        RNGProcessorBase instance = new RNGProcessorBase(null) {

            @Override
            protected Object processNode(Node node) throws Exception {
                return null;
            }

            @Override
            protected void processChild(Node childNode) throws Exception {
            }
        };

        ElementImpl node = new ElementImpl(new CoreDocumentImpl(), "name");
        node.setAttribute("ns", "http://www.example.org");
        node.setAttribute("datatypeLibrary", "http://www.example.org/dt");
        node.setAttribute("xmlns", "http://www.relaxng.org/");

        Text pattern = new Text();
        instance.setPatternAttributes(pattern, node);

        assertEquals("http://www.example.org/dt", pattern.getAttributeDatatypeLibrary());
        assertEquals("http://www.example.org", pattern.getAttributeNamespace());
        assertEquals("http://www.relaxng.org/", pattern.getDefaultNamespace());
    }
}
