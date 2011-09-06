package eu.fox7.bonxai.dtd;

import org.junit.Before;
import org.junit.Test;

import eu.fox7.bonxai.dtd.DocumentTypeDefinition;
import eu.fox7.bonxai.dtd.Element;
import eu.fox7.bonxai.dtd.ElementRef;
import static org.junit.Assert.*;

/**
 * Test of class ElementRef
 * @author Lars Schmidt
 */
public class ElementRefTest extends junit.framework.TestCase {

    private DocumentTypeDefinition dtd;

    @Before
    @Override
    public void setUp() {
        this.dtd = new DocumentTypeDefinition();
    }

    /**
     * Test of getElement method, of class ElementRef.
     */
    @Test
    public void testGetElement() {
        Element expResult = new Element("root");
        ElementRef elementRef = new ElementRef(dtd.getElementSymbolTable().updateOrCreateReference("root", expResult));
        Element result = elementRef.getElement();
        assertEquals(expResult, result);
    }

}